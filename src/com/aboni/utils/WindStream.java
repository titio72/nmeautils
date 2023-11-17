/*
 * Copyright (c) 2022,  Andrea Boni
 * This file is part of NMEARouter.
 * NMEARouter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * NMEARouter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with NMEARouter.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aboni.utils;

import com.aboni.data.LPFFilter;
import com.aboni.nmea.sentences.VWRSentence;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.*;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Side;
import net.sf.marineapi.nmea.util.Units;

public class WindStream {

	public static class Conf {

		private boolean useVWR;
		private boolean forceCalcTrue;
		private boolean smoothWind;
		private double windSmoothingFactor;
		private boolean skipFirstCalculation;

		public Conf() {
			useVWR = false;
			forceCalcTrue = false;
			smoothWind = false;
			windSmoothingFactor = 0.5;
			skipFirstCalculation = true;
		}

		public boolean isUseVWR() {
			return useVWR;
		}

		public void setUseVWR(boolean useVWR) {
			this.useVWR = useVWR;
		}

		public boolean isForceCalcTrue() {
			return forceCalcTrue;
		}

		public void setForceCalcTrue(boolean forceCalcTrue) {
			this.forceCalcTrue = forceCalcTrue;
		}

		public boolean isSmoothWind() {
			return smoothWind;
		}

		public void setSmoothWind(boolean smoothWind) {
			this.smoothWind = smoothWind;
		}

		public double getWindSmoothingFactor() {
			return windSmoothingFactor;
		}

		public void setWindSmoothingFactor(double windSmoothingFactor) {
			this.windSmoothingFactor = windSmoothingFactor;
		}

		public boolean isSkipFirstCalculation() {
			return skipFirstCalculation;
		}

		public void setSkipFirstCalculation(boolean skipFirstCalculation) {
			this.skipFirstCalculation = skipFirstCalculation;
		}
	}
	
	private final NMEATrueWind windCalc;
	
	private long lastMWVTrue;
	private long lastMWD;
	
	private long lastSentMWD;
	
	private static final long CALC_THRESHOLD = 5000;
	private static final long SEND_THROTTLING_MWD = 900;

	private Conf conf;
	private final TalkerId tid;
		
	public WindStream(TalkerId tid) {
		this(tid, new Conf());
	}
	
	public WindStream(TalkerId tid, Conf conf) {
		this.tid = tid;
		this.conf = conf;
		windCalc = new NMEATrueWind(tid);
		lastMWVTrue = 0;
		lastMWD = 0;
	}
	
	public void setConf(Conf conf) {
		this.conf = conf;
	}
	
	public Conf getConf() {
		return conf;
	}

	private boolean shallCalcMWVTrue(long now) {
		if (lastMWVTrue ==0 && conf.skipFirstCalculation) lastMWVTrue = now;
		return (now - lastMWVTrue) > CALC_THRESHOLD;
	}
	
	private boolean shallCalcMWD(long now) {
		if (lastMWD==0 && conf.skipFirstCalculation) lastMWD = now;
		return (now - lastMWD) > CALC_THRESHOLD && (now - lastSentMWD) > SEND_THROTTLING_MWD;
	}
	
	public void onSentence(Sentence s) {
		onSentence(s, System.currentTimeMillis());
	}
	
	public void onSentence(Sentence s, long time) {
		switch (s.getSentenceId()) {
			case "VWR":
				if (conf.useVWR) {
					handleVWR(s, time);
				}
				break;
			case "MWV":
				if (!conf.useVWR) {
					MWVSentence mwv = (MWVSentence) s;
					if (mwv.isTrue()) {
						handleTrueMWV(time, mwv);
					} else {
						handleAppMWV(time, mwv);
					}
				}
				break;
			case "MWD":
				handleMWD(s, time);
				break;
			case "VHW":
				VHWSentence vhw = (VHWSentence) s;
				windCalc.setSpeed(vhw, time);
				onProcSentence(s, time);
				break;
			case "HDG":
				HDGSentence hdg = (HDGSentence) s;
				windCalc.setHeading(hdg, time);
				onProcSentence(s, time);
				break;
			case "HDM":
				HDMSentence hdm = (HDMSentence) s;
				windCalc.setHeading(hdm, time);
				onProcSentence(s, time);
				break;
			default:
				onProcSentence(s, time);
				break;
		}
	}

	private void handleMWD(Sentence s, long time) {
		MWDSentence mwd = (MWDSentence)s;
		lastMWD = time;

		double wSpeed = mwd.getWindSpeedKnots();
		if (conf.smoothWind) {
			lastMWDSpeedValue = LPFFilter.getLPFReading(conf.windSmoothingFactor, lastMWDSpeedValue, wSpeed);
			wSpeed = lastMWDSpeedValue;
		}
		mwd.setWindSpeedKnots(wSpeed);
		
		windCalc.setWind(mwd, time);
		onProcSentence(s, time);
	}

	private void handleAppMWV(long time, MWVSentence mwv) {
		onProcSentence(mwv, time);
		
		double wSpeed = mwv.getSpeed();
		double wAngle = Utils.normalizeDegrees0To360(mwv.getAngle());
		if (conf.smoothWind) {
			if (lastMWVAppSpeedValue >=0) {
				lastMWVAppSpeedValue = LPFFilter.getLPFReading(conf.windSmoothingFactor, lastMWVAppSpeedValue, wSpeed);
				lastMWVAppAngleValue = Utils.getNormal(wAngle, lastMWVAppAngleValue);
				lastMWVAppAngleValue = Utils.normalizeDegrees0To360(LPFFilter.getLPFReading(conf.windSmoothingFactor, lastMWVAppAngleValue, wAngle));
			} else {
				lastMWVAppSpeedValue = wSpeed;
				lastMWVAppAngleValue = wAngle;
			}
			wSpeed = lastMWVAppSpeedValue;
			wAngle = lastMWVAppAngleValue;
		}
		mwv.setSpeed(wSpeed);
		mwv.setAngle(wAngle);
		
		windCalc.setWind(mwv, time);
		if (shallCalcMWVTrue(time)) {
			windCalc.calcMWVSentence(time);
			if (windCalc.getTrueWind()!=null) {
				onProcSentence(windCalc.getTrueWind(), time);
			}
			if (shallCalcMWD(time)) {
				windCalc.calcMWDSentence(time);
				if (windCalc.getWind()!=null) {
					onProcSentence(windCalc.getWind(), time);
				}
			}
		}
	}

	private void handleTrueMWV(long time, MWVSentence mwv) {
		if (!conf.forceCalcTrue) {
			lastMWVTrue = time;
			
			double wSpeed = mwv.getSpeed();
			double wAngle = Utils.normalizeDegrees0To360(mwv.getAngle());
			if (conf.smoothWind) {
				if (lastMWVTrueSpeedValue >=0) {
					lastMWVTrueSpeedValue = LPFFilter.getLPFReading(conf.windSmoothingFactor, lastMWVTrueSpeedValue, wSpeed);
					lastMWVTrueAngleValue = Utils.getNormal(wAngle, lastMWVTrueAngleValue);
					lastMWVTrueAngleValue = Utils.normalizeDegrees0To360(LPFFilter.getLPFReading(conf.windSmoothingFactor, lastMWVTrueAngleValue, wAngle));
				} else {
					lastMWVTrueSpeedValue = wSpeed;
					lastMWVTrueAngleValue = wAngle;
				}
				wSpeed = lastMWVTrueSpeedValue;
				wAngle = lastMWVTrueAngleValue;
			}
			mwv.setSpeed(wSpeed);
			mwv.setAngle(wAngle);
			
			windCalc.setWind(mwv, time);
			onProcSentence(mwv, time);
			if (shallCalcMWD(time)) {
				windCalc.calcMWDSentence(time);
				if (windCalc.getWind()!=null) {
					onProcSentence(windCalc.getWind(), time);
				}
			}
		}
	}

	private double lastMWVAppSpeedValue = -1; // init with an impossible value so to able to identify the first snapshot
	private double lastMWVTrueSpeedValue = -1; // init with an impossible value so to able to identify the first snapshot
	private double lastMWDSpeedValue;
	private double lastVWRSpeedValue;

	private double lastMWVAppAngleValue;
	private double lastMWVTrueAngleValue;
	
	private void handleVWR(Sentence s, long time) {
		VWRSentence vwr = (VWRSentence) s;
		MWVSentence mwvApp = (MWVSentence) SentenceFactory.getInstance().createParser(tid,  SentenceId.MWV);
		mwvApp.setAngle(vwr.getSide()==Side.STARBOARD?vwr.getAngle():(360-vwr.getAngle()));
		
		double wSpeed = vwr.getSpeed();
		if (conf.smoothWind) {
			lastVWRSpeedValue = LPFFilter.getLPFReading(conf.windSmoothingFactor, lastVWRSpeedValue, wSpeed);
			wSpeed = lastVWRSpeedValue;
		}
		mwvApp.setSpeed(wSpeed);

		mwvApp.setStatus(DataStatus.ACTIVE);
		mwvApp.setSpeedUnit(Units.KNOT);
		mwvApp.setTrue(false);
		onProcSentence(mwvApp, time);
		windCalc.setWind(mwvApp, time);
		if (shallCalcMWVTrue(time)) {
			windCalc.calcMWVSentence(time);
			onProcSentence(windCalc.getTrueWind(), time);
		}
		if (shallCalcMWD(time)) {
			windCalc.calcMWDSentence(time);
			onProcSentence(windCalc.getWind(), time);
		}
	}

	protected void onProcSentence(Sentence s, long time) {
		// do nothing
	}
}
