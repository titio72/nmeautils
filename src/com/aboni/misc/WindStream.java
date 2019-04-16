package com.aboni.misc;

import com.aboni.nmea.sentences.NMEASentenceItem;
import com.aboni.nmea.sentences.VWRSentence;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.*;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Side;
import net.sf.marineapi.nmea.util.Units;

import java.io.BufferedReader;
import java.io.FileReader;

@SuppressWarnings("unused")
public class WindStream {

	public static class Conf {
		public boolean useVWR = false;
		public boolean forceCalcTrue = false;
		public boolean smoothWind = false;
		public double windSmoothingFactor = 0.5;
		public boolean skipFirstCalculation = true;

		public Conf() {}
	}
	
	private final NMEATrueWind windCalc;
	
	private long lastMWV_T;
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
		lastMWV_T = 0;
		lastMWD = 0;
	}
	
	public void setConf(Conf conf) {
		this.conf = conf;
	}
	
	public Conf getConf() {
		return conf;
	}

	private boolean shallCalcMWV_T(long now) {
		if (lastMWV_T==0 && conf.skipFirstCalculation) lastMWV_T = now;
		return (now - lastMWV_T) > CALC_THRESHOLD;
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
		double wAngle = Utils.normalizeDegrees0_360(mwv.getAngle());
		if (conf.smoothWind) {
			if (lastMWV_RSpeedValue>=0) {
				lastMWV_RSpeedValue = LPFFilter.getLPFReading(conf.windSmoothingFactor, lastMWV_RSpeedValue, wSpeed);
				lastMWV_RAngleValue = Utils.getNormal(wAngle, lastMWV_RAngleValue);
				lastMWV_RAngleValue = Utils.normalizeDegrees0_360(LPFFilter.getLPFReading(conf.windSmoothingFactor, lastMWV_RAngleValue, wAngle));
			} else {
				lastMWV_RSpeedValue = wSpeed;
				lastMWV_RAngleValue = wAngle;
			}
			wSpeed = lastMWV_RSpeedValue;
			wAngle = lastMWV_RAngleValue;
		}
		mwv.setSpeed(wSpeed);
		mwv.setAngle(wAngle);
		
		windCalc.setWind(mwv, time);
		if (shallCalcMWV_T(time)) {
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
			lastMWV_T = time;
			
			double wSpeed = mwv.getSpeed();
			double wAngle = Utils.normalizeDegrees0_360(mwv.getAngle());
			if (conf.smoothWind) {
				if (lastMWV_TSpeedValue>=0) { 
					lastMWV_TSpeedValue = LPFFilter.getLPFReading(conf.windSmoothingFactor, lastMWV_TSpeedValue, wSpeed);
					lastMWV_TAngleValue = Utils.getNormal(wAngle, lastMWV_TAngleValue);
					lastMWV_TAngleValue = Utils.normalizeDegrees0_360(LPFFilter.getLPFReading(conf.windSmoothingFactor, lastMWV_TAngleValue, wAngle));
				} else {
					lastMWV_TSpeedValue = wSpeed;
					lastMWV_TAngleValue = wAngle;
				}
				wSpeed = lastMWV_TSpeedValue;
				wAngle = lastMWV_TAngleValue;
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

	private double lastMWV_RSpeedValue = -1; // init with an impossible value so to able to identify the first snapshot
	private double lastMWV_TSpeedValue = -1; // init with an impossible value so to able to identify the first snapshot
	private double lastMWDSpeedValue;
	private double lastVWRSpeedValue;

	private double lastMWV_RAngleValue;
	private double lastMWV_TAngleValue;
	
	private void handleVWR(Sentence s, long time) {
		VWRSentence vwr = (VWRSentence) s;
		MWVSentence mwv_r = (MWVSentence) SentenceFactory.getInstance().createParser(tid,  SentenceId.MWV);
		mwv_r.setAngle(vwr.getSide()==Side.STARBOARD?vwr.getAngle():(360-vwr.getAngle()));
		
		double wSpeed = vwr.getSpeed();
		if (conf.smoothWind) {
			lastVWRSpeedValue = LPFFilter.getLPFReading(conf.windSmoothingFactor, lastVWRSpeedValue, wSpeed);
			wSpeed = lastVWRSpeedValue;
		}
		mwv_r.setSpeed(wSpeed);

		mwv_r.setStatus(DataStatus.ACTIVE);
		mwv_r.setSpeedUnit(Units.KNOT);
		mwv_r.setTrue(false);
		onProcSentence(mwv_r, time);
		windCalc.setWind(mwv_r, time);
		if (shallCalcMWV_T(time)) {
			windCalc.calcMWVSentence(time);
			onProcSentence(windCalc.getTrueWind(), time);
		}
		if (shallCalcMWD(time)) {
			windCalc.calcMWDSentence(time);
			onProcSentence(windCalc.getWind(), time);
		}
	}

	protected void onProcSentence(Sentence s, long time) {
		if (s!=null) {
			System.out.println(time + " " + s.toSentence());
		}
	}
	
	public static void main(String[] args) {
		try {
			WindStream s = new WindStream(TalkerId.II);
			FileReader r = new FileReader("petter.log");
			BufferedReader br = new BufferedReader(r);
			String line;
			while ((line = br.readLine())!=null) {
				NMEASentenceItem itm = new NMEASentenceItem(line);
				s.onSentence(itm.getSentence(), itm.getTimestamp());
			}
			r.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
