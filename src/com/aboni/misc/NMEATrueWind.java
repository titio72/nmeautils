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
			calcMWDSentence(time, eHeadingG.ev);
		} else if (Math.abs(eHeadingM.timestamp - eTWind.timestamp)<threshold/*ms*/) {
			calcMWDSentence(time, eHeadingM.ev);
		} else if (Math.abs(eSpeed.timestamp - eTWind.timestamp)<threshold/*ms*/) {
			calcMWDSentence(time, eSpeed.ev);
		}
	}

	public void calcMWDSentence(long time) {
		if (eHeadingG.ev!=null) {
			calcMWDSentence(time, eHeadingG.ev);
		} else if (eHeadingM.ev!=null) {
			calcMWDSentence(time, eHeadingM.ev);
		} else if (eSpeed.ev!=null) {
			calcMWDSentence(time, eSpeed.ev);
		}
	}
	
	private void calcMWDSentence(long time, HeadingSentence hs) {
		if (hs!=null && eTWind.ev!=null) {
			MWDSentence s = (MWDSentence) SentenceFactory.getInstance().createParser(id, SentenceId.MWD);

			double td = Utils.getTrueHeading(hs) + eTWind.ev.getAngle();
			if (!Double.isNaN(td)) {
				td = Utils.normalizeDegrees0To360(td);
				s.setTrueWindDirection(Utils.round(td, 2));
			}

			double md = Utils.getMagHeading(hs) + eTWind.ev.getAngle();
			if (!Double.isNaN(md)) {
				md = Utils.normalizeDegrees0To360(md);
				s.setMagneticWindDirection(Utils.round(md, 2));
			}

			double speed = Utils.getSpeedKnots(eTWind.ev);
			s.setWindSpeedKnots(speed);
			s.setWindSpeed(Utils.round(speed * 0.51444444444, 2));
			
			eWind.setEvent(s, time);
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
		if (eSpeed.ev!=null && eAWind.ev!=null) {
			MWVSentence mwvt = (MWVSentence) SentenceFactory.getInstance().createParser(id, SentenceId.MWV);
			double s = eSpeed.ev.getSpeedKnots();
			double wdm = eAWind.ev.getAngle();
			double ws = Utils.getSpeedKnots(eAWind.ev);
			TrueWind tw = new TrueWind(s, wdm, ws);
			mwvt.setAngle(Utils.normalizeDegrees0To360(tw.getTrueWindDeg()));
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
		return eHeadingG.ev;
	}

	public long getHeadingAge(long now) {
		return eHeadingG.getAge(now);
	}
	
	public void setWind(MWDSentence s, long time) {
		eWind.setEvent(s, time);
	}
	
	public MWDSentence getWind() {
		return eWind.ev;
	}
	
	public long getWindAge(long now) {
		return eWind.getAge(now);
	}

	public void setSpeed(VHWSentence s, long time) {
		eSpeed.setEvent(s, time);
	}
	
	public VHWSentence getSpeed() {
		return eSpeed.ev;
	}
	
	public long getSpeedAge(long now) {
		return eSpeed.getAge(now);
	}
	
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
		return eTWind.ev;
	}
	
	public long getTrueWindAge(long now) {
		return eTWind.getAge(now);
	}
	
	public MWVSentence getApparentWind() {
		return eAWind.ev;
	}
	
	public long getAppWindAge(long now) {
		return eAWind.getAge(now);
	}
}
