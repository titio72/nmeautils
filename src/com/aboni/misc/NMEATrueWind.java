package com.aboni.misc;

import com.aboni.geo.TrueWind;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.HDGSentence;
import net.sf.marineapi.nmea.sentence.MWDSentence;
import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.sentence.VHWSentence;
import net.sf.marineapi.nmea.util.Units;

public class NMEATrueWind {
	
	private class Event<C> {
		C event;
		long timestamp;
		
		Event(C sentence, long t) {
			timestamp = t;
			event = sentence;
		}
		
		long getAge(long now) {
			if (event!=null) {
				return now - timestamp;
			} else {
				return -1;
			}
		}
		/*
		long getElapsed(long now) {
			return now - timestamp;
		}
		*/
		void setEvent(C e, long time) {
			event = e;
			timestamp = time;
		}
	}
	
	private Event<HDGSentence> eHeading = new Event<HDGSentence>(null, 0);
	private Event<VHWSentence> eSpeed = new Event<VHWSentence>(null, 0);
	private Event<MWVSentence> eAWind = new Event<MWVSentence>(null, 0);
	private Event<MWVSentence> eTWind = new Event<MWVSentence>(null, 0);
	private Event<MWDSentence> eWind = new Event<MWDSentence>(null, 0);
	
	private TalkerId id;
	
	public NMEATrueWind(TalkerId id) {
		this.id = id;
	}
	
	public void calcMWDSentence(long threshold, long time) {
		if (Math.abs(eHeading.timestamp - eTWind.timestamp)<threshold/*ms*/) {
			calcMWDSentence(time);
		}
	}
	
	public void calcMWDSentence(long time) {
		if (eHeading.event!=null && eTWind.event!=null) {
			MWDSentence s = (MWDSentence) SentenceFactory.getInstance().createParser(id, SentenceId.MWD);

			double td = getTrueHeading(eHeading.event) + eTWind.event.getAngle();
			td = Utils.normalizeDegrees0_360(td);
			s.setTrueWindDirection(td);

			double md = getMagHeading(eHeading.event) + eTWind.event.getAngle();
			md = Utils.normalizeDegrees0_360(md);
			s.setMagneticWindDirection(md);

			s.setWindSpeedKnots(eTWind.event.getSpeed());
			s.setWindSpeed(eTWind.event.getSpeed()*0.51444444444);
			
			eWind.setEvent(s, time);
		}
	}

    private static double getTrueHeading(HDGSentence h) {
		double dev = 0.0, var = 0.0;
		try { dev = h.getDeviation(); } catch (Exception e) {}
		try { var = h.getVariation(); } catch (Exception e) {}
		return h.getHeading() + var + dev;
    }
	
    private static double getMagHeading(HDGSentence h) {
		double dev = 0.0;
		try { dev = h.getDeviation(); } catch (Exception e) {}
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
		eHeading.setEvent(s, time);
	}
	
	public HDGSentence getHeading() {
		return eHeading.event;
	}

	public long getHeadingAge(long now) {
		return eHeading.getAge(now);
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
