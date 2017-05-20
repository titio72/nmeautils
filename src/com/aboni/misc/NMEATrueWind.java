package com.aboni.misc;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.HDGSentence;
import net.sf.marineapi.nmea.sentence.HeadingSentence;
import net.sf.marineapi.nmea.sentence.MWDSentence;
import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.sentence.VHWSentence;
import net.sf.marineapi.nmea.util.Units;

public class NMEATrueWind {
	private HeadingSentence heading;
	private VHWSentence speed;
	private MWVSentence awind;
	private MWVSentence twind;
	private MWDSentence wind;
	
	long tsHeading, tsSpeed, tsAWind, tsTWind, tsWind;
	
	private TalkerId id;
	private static final int THRESHOLD = 300 /*ms*/;
	
	public NMEATrueWind(TalkerId id) {
		this.id = id;
	}
    
    /**
     * Calculate the wind direction relative to north and return it in the form of MWDSentence message.
     * The calculation takes into account the age of the wind and heading sample and requires that they are not
     * farther the "threshold" milliseconds. 
     * @param threshold The consistency threshold of wind and heading 
     * @return The MWDSentence containing the wind speed and direction respect to north 
     */
	public MWVSentence getMWVSentence(long threshold) {
		/*
		 * Check if the wind and heading are close enough to make sense summing them up.
		 */
		if (speed!=null && awind!=null && Math.abs(tsSpeed-tsAWind)<threshold/*ms*/) {
			MWVSentence mwvt = (MWVSentence) SentenceFactory.getInstance().createParser(id, SentenceId.MWV);
			double s = speed.getSpeedKnots();
			double wdm = awind.getAngle();
			double ws = awind.getSpeed();
			TrueWind tw = new TrueWind(s, wdm, ws);
			mwvt.setAngle(tw.getTrueWindDeg());
			mwvt.setSpeed(tw.getTrueWindSpeed());
			mwvt.setSpeedUnit(Units.KNOT);	
			twind = mwvt;
			tsTWind = System.currentTimeMillis();
			return mwvt;
		} else {
			return null;
		}
	}
	
	/**
	 * Set the heading (necessary to calculate the direction of the wind from north).
	 * The sample will be tagged with a timestamp set to now. 
	 * @param s
	 */
	public void setHeading(HDGSentence s) {
		setHeading(s, System.currentTimeMillis());
	}
	
	/**
	 * Set the heading (necessary to calculate the direction of the wind from north).
	 * @param s		The heading
	 * @param time	The time of the heading sample
	 */
	public void setHeading(HDGSentence s, long time) {
		heading = s;
		tsHeading = (s==null)?0:time;
	}
	
	/**
	 * Set the wind information. Only "true" wind will be taken into account while "relative" will be discarded.
	 * The timestamp is as of now.
	 * @param s
	 */
	public void setWind(MWVSentence s) {
		setWind(s, System.currentTimeMillis());
	}
	
	/**
	 * Set the wind information.
	 * @param s		The wind info
	 * @param time	The timestamp of the wind sample
	 */
	public void setWind(MWVSentence s, long time) {
		if (s!=null) {
			if (s.isTrue()) {
				tsTWind = time;
				twind = s;
			} else {
				tsAWind = time;
				awind = s;
			}
		}
	}
	
	public long getWindAge() {
		return getWindAge(System.currentTimeMillis());
	}

	public long getWindAge(long now) {
		if (wind!=null) {
			return now - tsWind;
		} else {
			return -1;
		}
	}
	
	public long getTruwWindAge() {
		return getTruwWindAge(System.currentTimeMillis());
	}

	public long getTruwWindAge(long now) {
		if (twind!=null) {
			return now - tsTWind;
		} else {
			return -1;
		}
	}
}
