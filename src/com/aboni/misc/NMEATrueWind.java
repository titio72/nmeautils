package com.aboni.misc;

import com.aboni.geo.TrueWind;
import net.sf.marineapi.nmea.parser.DataNotAvailableException;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.*;
import net.sf.marineapi.nmea.util.Units;

@SuppressWarnings("unused")
public class NMEATrueWind {
	
	private final Event<HDMSentence> eHeadingM = new Event<>(null, 0);
	private final Event<HDGSentence> eHeadingG = new Event<>(null, 0);
	private final Event<VHWSentence> eSpeed = new Event<>(null, 0);
	private final Event<MWVSentence> eAWind = new Event<>(null, 0);
	private final Event<MWVSentence> eTWind = new Event<>(null, 0);
	private final Event<MWDSentence> eWind = new Event<>(null, 0);
	
	private final TalkerId id;
	
	public NMEATrueWind(TalkerId id) {
		this.id = id;
	}
	
	public void calcMWDSentence(long threshold, long time) {
		if (Math.abs(eHeadingG.timestamp - eTWind.timestamp)<threshold/*ms*/) {
			calcMWDSentence(time, eHeadingG.event);
		} else if (Math.abs(eHeadingM.timestamp - eTWind.timestamp)<threshold/*ms*/) {
			calcMWDSentence(time, eHeadingM.event);
		} else if (Math.abs(eSpeed.timestamp - eTWind.timestamp)<threshold/*ms*/) {
			calcMWDSentence(time, eSpeed.event);
		}
	}

	public void calcMWDSentence(long time) {
		if (eHeadingG!=null && eHeadingG.event!=null) {
			calcMWDSentence(time, eHeadingG.event);
		} else if (eHeadingM!=null && eHeadingM.event!=null) {
			calcMWDSentence(time, eHeadingM.event);
		} else if (eSpeed!=null && eSpeed.event!=null) {
			calcMWDSentence(time, eSpeed.event);
		}
	}
	
	private void calcMWDSentence(long time, HeadingSentence hs) {
		if (hs!=null && eTWind.event!=null) {
			MWDSentence s = (MWDSentence) SentenceFactory.getInstance().createParser(id, SentenceId.MWD);

			double td = getTrueHeading(hs) + eTWind.event.getAngle();
			if (!Double.isNaN(td)) {
				td = Utils.normalizeDegrees0_360(td);
				s.setTrueWindDirection(Utils.round(td, 2));
			}

			double md = getMagHeading(hs) + eTWind.event.getAngle();
			if (!Double.isNaN(md)) {
				md = Utils.normalizeDegrees0_360(md);
				s.setMagneticWindDirection(Utils.round(md, 2));
			}
			
			s.setWindSpeedKnots(eTWind.event.getSpeed());
			s.setWindSpeed(Utils.round(eTWind.event.getSpeed()*0.51444444444, 2));
			
			eWind.setEvent(s, time);
		}
	}

    private static double getTrueHeading(HeadingSentence h) {
		double dev = 0.0, var = 0.0;
		try { if (h instanceof HDGSentence) dev = ((HDGSentence)h).getDeviation(); } catch (Exception ignored) {}
		try { if (h instanceof HDGSentence) dev = ((HDGSentence)h).getVariation(); } catch (Exception ignored) {}
		try {
			return h.getHeading() + var + dev;
		} catch (DataNotAvailableException e) {
			return Double.NaN;
		}
    }
	
    private static double getMagHeading(HeadingSentence h) {
		double dev = 0.0;
		try { if (h instanceof HDGSentence) dev = ((HDGSentence)h).getDeviation(); } catch (Exception ignored) {}
		try {
			if (h instanceof VHWSentence) {
				return ((VHWSentence)h).getMagneticHeading();
			} else {
				return h.getHeading() + dev;
			}
		} catch (DataNotAvailableException e) {
			return Double.NaN;
		}
    }
	
	public void calcMWVSentence(long threshold, long time) {
		if (Math.abs(eSpeed.timestamp - eAWind.timestamp)<threshold/*ms*/) {
			calcMWVSentence(time);
		} else {
			eTWind.setEvent(null, 0);
		}
	}
	
	public void calcMWVSentence(long time) {
		/*
		 * Check if the wind and heading are close enough to make sense summing them up.
		 */
		if (eSpeed.event!=null && eAWind.event!=null) {
			MWVSentence mwvt = (MWVSentence) SentenceFactory.getInstance().createParser(id, SentenceId.MWV);
			double s = eSpeed.event.getSpeedKnots();
			double wdm = eAWind.event.getAngle();
			double ws = eAWind.event.getSpeed();
			TrueWind tw = new TrueWind(s, wdm, ws);
			mwvt.setAngle(Utils.normalizeDegrees0_360(tw.getTrueWindDeg()));
			mwvt.setSpeed(tw.getTrueWindSpeed());
			mwvt.setSpeedUnit(Units.KNOT);
			mwvt.setTrue(true);
			eTWind.setEvent(mwvt, time);
		}
	}

	/**
	 * Set the heading (necessary to calculate the direction of the wind from north).
	 * @param s		The heading
	 * @param time	The time of the heading sample
	 */
	public void setHeading(HDGSentence s, long time) {
		eHeadingG.setEvent(s, time);
	}
	
	public void setHeading(HDMSentence s, long time) {
		eHeadingM.setEvent(s, time);
	}
	
	public HDGSentence getHeading() {
		return eHeadingG.event;
	}

	public long getHeadingAge(long now) {
		return eHeadingG.getAge(now);
	}
	
	public void setWind(MWDSentence s, long time) {
		eWind.setEvent(s, time);
	}
	
	public MWDSentence getWind() {
		return eWind.event;
	}
	
	public long getWindAge(long now) {
		return eWind.getAge(now);
	}

	public void setSpeed(VHWSentence s, long time) {
		eSpeed.setEvent(s, time);
	}
	
	public VHWSentence getSpeed() {
		return eSpeed.event;
	}
	
	public long getSpeedAge(long now) {
		return eSpeed.getAge(now);
	}
	
	/**
	 * Set the wind information.
	 * @param s		The wind info
	 * @param time	The timestamp of the wind sample
	 */
	public void setWind(MWVSentence s, long time) {
		if (s!=null) {
			if (s.isTrue()) {
				eTWind.setEvent(s, time);
			} else {
				eAWind.setEvent(s, time);
			}
		}
	}
	
	public MWVSentence getTrueWind() {
		return eTWind.event;
	}
	
	public long getTrueWindAge(long now) {
		return eTWind.getAge(now);
	}
	
	public MWVSentence getApparentWind() {
		return eAWind.event;
	}
	
	public long getAppWindAge(long now) {
		return eAWind.getAge(now);
	}
}
