package com.aboni.nmea.sentences;

import java.util.Calendar;
import java.util.TimeZone;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.DateSentence;
import net.sf.marineapi.nmea.sentence.PositionSentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.TimeSentence;
import net.sf.marineapi.nmea.util.Date;
import net.sf.marineapi.nmea.util.Position;
import net.sf.marineapi.nmea.util.Time;

public class NMEAUtils {

	private NMEAUtils() {}


    public static Position getPosition(Sentence posSentence) {
        if (posSentence instanceof PositionSentence) {
            return ((PositionSentence)posSentence).getPosition();
        } else { 
            return null;
        }
    }

    public static Time getTime(Sentence timeSentence) {
        if (timeSentence instanceof TimeSentence) {
            return ((TimeSentence)timeSentence).getTime();
        } else { 
            return null;
        }
    }

    public static Date getDate(Sentence dateSentence) {
        if (dateSentence instanceof DateSentence) {
            return ((DateSentence)dateSentence).getDate();
        } else { 
            Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            return new Date(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH));
        }
    }
	
	/**
	 * Get distance in NM
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static double getDistance(Position p1, Position p2) {
		double d = p1.distanceTo(p2);
		return d;
	}
	
	/**
	 * Get distance in meters
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static double getDistanceMeters(Position p1, Position p2) {
		double d = p1.distanceTo(p2);
		return d * 1852.0;
	}
	

    public static Calendar getTimestamp(RMCSentence s) {
    	return getTimestamp(s.getTime(), s.getDate());
    }
    
    public static Calendar getTimestamp(Time time, Date date) { 
       	int h = time.getOffsetHours();
    	int m = time.getOffsetMinutes();
    	Calendar c = null;
    	if (h==0 && m==0) {
	    	c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    	} else {
	    	String s = String.format("+%02d:%02d", h, m);
	    	c = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT" + s));
    	}
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, (int)time.getSeconds());
        c.set(Calendar.MINUTE, time.getMinutes());
        c.set(Calendar.HOUR_OF_DAY, time.getHour());
        c.set(Calendar.YEAR, date.getYear());
        c.set(Calendar.MONTH, date.getMonth()-1);
        c.set(Calendar.DAY_OF_MONTH, date.getDay());
        return c;
    }

    private static boolean registered = false;
    
    public synchronized static void registerExtraSentences() {
        if (!registered) {
            SentenceFactory.getInstance().registerParser(VWRParser.NMEA_SENTENCE_TYPE, VWRParser.class);
            SentenceFactory.getInstance().registerParser(XXXPParser.NMEA_SENTENCE_TYPE, XXXPParser.class);
            SentenceFactory.getInstance().registerParser(XDPParser.NMEA_SENTENCE_TYPE, XDPParser.class);
            SentenceFactory.getInstance().registerParser(STALKParser.NMEA_SENTENCE_TYPE, STALKParser.class);
            registered = true;
        }
    }	
}
