package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.parser.SentenceParser;

import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Side;

public class VWRParser extends SentenceParser implements VWRSentence {

	public static final String NMEA_SENTENCE_TYPE = "VWR";
	
    private static final int WIND_ANGLE = 0;
    private static final int SIDE = 1;
    private static final int WIND_SPEED_KN = 2;
    private static final int SPEED_UNITS_KN = 3;
    private static final int WIND_SPEED_M = 4;
    private static final int SPEED_UNITS_M = 5;
    private static final int WIND_SPEED_K = 6;
    private static final int SPEED_UNITS_K = 7;
    private static final int DATA_STATUS = 8;

    /**
     * Creates a new instance of MWVParser.
     * 
     * @param nmea MWV sentence String
     */
    public VWRParser(String nmea) {
        super(nmea, NMEA_SENTENCE_TYPE);
    }

    /**
     * Creates a new empty instance of MWVParser.
     * 
     * @param talker Talker id to set
     */
    public VWRParser(TalkerId talker) {
        super(talker, NMEA_SENTENCE_TYPE, 9);
        setCharValue(DATA_STATUS, DataStatus.VOID.toChar());
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.sentence.MWVSentence#getAngle()
     */
    public double getAngle() {
        return getDoubleValue(WIND_ANGLE);
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.sentence.MWVSentence#getSpeed()
     */
    public double getSpeed() {
        return getDoubleValue(WIND_SPEED_KN);
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.sentence.MWVSentence#getStatus()
     */
    public DataStatus getStatus() {
        return DataStatus.valueOf(getCharValue(DATA_STATUS));
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.sentence.MWVSentence#isTrue()
     */
    public Side getSide() {
        char s = getCharValue(SIDE);
        return ('S'==s)?Side.PORT:Side.STARBOARD;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.sentence.MWVSentence#setAngle(double)
     */
    public void setAngle(double angle) {
        setDegreesValue(WIND_ANGLE, angle);
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.sentence.MWVSentence#setSpeed(double)
     */
    public void setSpeed(double speed) {
        if (speed < 0) {
            throw new IllegalArgumentException("Speed must be positive");
        }
        setDoubleValue(WIND_SPEED_KN, speed, 1, 1);
        setCharValue(SPEED_UNITS_KN, 'N');
        setDoubleValue(WIND_SPEED_M, (speed * 1852.0)/3600.0, 1, 1);
        setCharValue(SPEED_UNITS_M, 'M');
        setDoubleValue(WIND_SPEED_K, speed * 1.852, 1, 1);
        setCharValue(SPEED_UNITS_K, 'K');
    }

    /*
     * (non-Javadoc)
     * @see
     * net.sf.marineapi.nmea.sentence.MWVSentence#setStatus(net.sf.marineapi
     * .nmea.util.DataStatus)
     */
    public void setStatus(DataStatus status) {
        setCharValue(DATA_STATUS, status.toChar());
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.sentence.VWRSentence#setSide(Side)
     */
    public void setSide(Side s) {
        setCharValue(SIDE, s.equals(Side.PORT)?'L':'R');
    }
}
