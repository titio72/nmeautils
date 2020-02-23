package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.*;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Date;
import net.sf.marineapi.nmea.util.Position;
import net.sf.marineapi.nmea.util.Time;

import java.util.Calendar;
import java.util.TimeZone;

public class NMEAUtils {

	private NMEAUtils() {}

    /**
     * Extract the position form a position sentence if and anly if the sentence is valid and it status is ACTIVE.
     * @param posSentence The sentence the position must be extracted from.
     * @return The position if valid, null otherwise
     */
    public static Position getPosition(PositionSentence posSentence) {
        if (posSentence.isValid()) {
            if (posSentence instanceof RMCSentence) {
                RMCSentence rmc = (RMCSentence)posSentence;
                return rmc.getStatus()==DataStatus.ACTIVE?rmc.getPosition():null;
            } else  if (posSentence instanceof GLLSentence) {
                GLLSentence gll = (GLLSentence)posSentence;
                return gll.getStatus()==DataStatus.ACTIVE?gll.getPosition():null;
            } else {
                return posSentence.getPosition();
            }
        }
        return null;
    }

	/**
	 * Get distance in NM between two positions.
	 * @param p1 The first position.
	 * @param p2 The second position.
	 * @return The distance between p1 and p2 in nautical miles.
	 */
	public static double getDistance(Position p1, Position p2) {
        return p1.distanceTo(p2);
	}
	
	/**
	 * Get distance in meters between two positions.
     * @param p1 The first position.
     * @param p2 The second position.
     * @return The distance between p1 and p2 in meters.
	 */
	public static double getDistanceMeters(Position p1, Position p2) {
		double d = p1.distanceTo(p2);
		return d * 1852.0;
	}

    public static Calendar getTimestamp(RMCSentence s) {
        try {
            return NMEATimestampExtractor.getTimestamp(s);
        } catch (NMEATimestampExtractor.GPSTimeException e) {
            return null;
        }
    }

    public static Calendar getTimestampOptimistic(RMCSentence s) {
    	try {
    		return NMEATimestampExtractor.getTimestamp(s);
    	} catch (Exception e) {
    		return Calendar.getInstance();
    	}
    }
    
    private static boolean registered = false;
    
    public static synchronized void registerExtraSentences() {
        if (!registered) {
            SentenceFactory.getInstance().registerParser(VWRParser.NMEA_SENTENCE_TYPE, VWRParser.class);
            SentenceFactory.getInstance().registerParser(XDPParser.NMEA_SENTENCE_TYPE, XDPParser.class);
            registered = true;
        }
    }	
}
