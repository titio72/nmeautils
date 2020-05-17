package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.GLLSentence;
import net.sf.marineapi.nmea.sentence.PositionSentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Position;

import java.util.Calendar;

public class NMEAUtils {

	private NMEAUtils() {}

    public static Position getPosition(PositionSentence posSentence) {
        if (posSentence.isValid()) {
            if (posSentence instanceof RMCSentence) {
                RMCSentence rmc = (RMCSentence)posSentence;
                return rmc.getStatus() == DataStatus.ACTIVE ? rmc.getPosition() : null;
            } else if (posSentence instanceof GLLSentence) {
                GLLSentence gll = (GLLSentence) posSentence;
                return gll.getStatus() == DataStatus.ACTIVE ? gll.getPosition() : null;
            } else {
                return posSentence.getPosition();
            }
        }
        return null;
    }

    /**
     * Extract the position form a RMC sentence if and anly if the sentence is valid and it status is ACTIVE.
     *
     * @param rmc The sentence the position must be extracted from.
     * @return The position.
     */
    public static Position getPosition(RMCSentence rmc) {
        if (rmc != null && rmc.isValid() && rmc.getStatus() == DataStatus.ACTIVE) {
            return rmc.getPosition();
        } else {
            return null;
        }
    }

    /**
     * Get distance in NM between two positions.
     *
     * @param p1 The first position.
     * @param p2 The second position.
     * @return The distance between p1 and p2 in nautical miles.
     */
    public static double getDistance(Position p1, Position p2) {
        return p1.distanceTo(p2);
    }

    /**
     * Extract a Calendar object from a RMCSentence
     *
     * @param s The RMC sentence
     * @return The timestamp or null in case or malformed sentence or data not available
     */
    public static Calendar getTimestamp(RMCSentence s) {
        try {
            return NMEATimestampExtractor.getTimestamp(s);
        } catch (NMEATimestampExtractor.GPSTimeException e) {
            return null;
        }
    }

    /**
     * Extract a Calendar object from a RMCSentence. In case of error or not it gives "now"
     *
     * @param s The RMC sentence
     * @return The timestamp or "now"" in case or malformed sentence or data not available
     */
    public static Calendar getTimestampOptimistic(RMCSentence s) {
        try {
            Calendar c = NMEATimestampExtractor.getTimestamp(s);
            return (c == null) ? Calendar.getInstance() : c;
        } catch (NMEATimestampExtractor.GPSTimeException e) {
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
