package com.aboni.misc;

import com.aboni.geo.TrueWind;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.HDGSentence;
import net.sf.marineapi.nmea.sentence.HDMSentence;
import net.sf.marineapi.nmea.sentence.HeadingSentence;
import net.sf.marineapi.nmea.sentence.MWDSentence;
import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.sentence.VHWSentence;
import net.sf.marineapi.nmea.util.Units;

public class NMEATrueWind {
	
	private Event<HDMSentence> eHeadingM = new Event<HDMSentence>(null, 0);
	private Event<HDGSentence> eHeadingG = new Event<HDGSentence>(null, 0);
	private Event<VHWSentence> eSpeed = new Event<VHWSentence>(null, 0);
	private Event<MWVSentence> eAWind = new Event<MWVSentence>(null, 0);
	private Event<MWVSentence> eTWind = new Event<MWVSentence>(null, 0);
	private Event<MWDSentence> eWind = new Event<MWDSentence>(null, 0);
	
	private TalkerId id;
	
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
			td = Utils.normalizeDegrees0_360(td);
			s.setTrueWindDirection(td);

			double md = getMagHeading(hs) + eTWind.event.getAngle();
			md = Utils.normalizeDegrees0_360(md);
			s.setMagneticWindDirection(md);

			s.setWindSpeedKnots(eTWind.event.getSpeed());
			s.setWindSpeed(eTWind.event.getSpeed()*0.51444444444);
			
			eWind.setEvent(s, time);
		}
	}

    private static double getTrueHeading(HeadingSentence h) {
		double dev = 0.0, var = 0.0;
		try { if (h instanceof HDGSentence) dev = ((HDGSentence)h).getDeviation(); } catch (Exception e) {}
		try { if (h instanceof HDGSentence) dev = ((HDGSentence)h).getVariation(); } catch (Exception e) {}
		return h.getHeading() + var + dev;
    }
	
    private static double getMagHeading(HeadingSentence h) {
		double dev = 0.0;
		try { if (h instanceof HDGSentence) dev = ((HDGSentence)h).getDeviation(); } catch (Exception e) {}
		return h.getHeading() + dev;
    }
	
	public void calcMWVSentence(long threshold, long time) {
		if (Math.abs(eSpeed.timestamp - eAWind.timestamp)<threshold/*ms*/) {
			calcMWVSentence(time);
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
			mwvt.setAngle(tw.getTrueWindDeg());
			mwvt.setSpeed(tw.getTrueWindSpeed());
			mwvt.setSpeedUnit(Units.KNOT);
			mwvt.setTrue(true);
			eTWind.setEvent(mwvt, time);
		}
	}

	/**********************************************************/

	
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
	
	/**********************************************************/
	
	public void setWind(MWDSentence s, long time) {
		eWind.setEvent(s, time);
	}
	
	public MWDSentence getWind() {
		return eWind.event;
	}
	
	public long getWindAge(long now) {
		return eWind.getAge(now);
	}

	/**********************************************************/
	
	public void setSpeed(VHWSentence s, long time) {
		eSpeed.setEvent(s, time);
	}
	
	public VHWSentence getSpeed() {
		return eSpeed.event;
	}
	
	public long getSpeedAge(long now) {
		return eSpeed.getAge(now);
	}
	
	/**********************************************************/
	
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
