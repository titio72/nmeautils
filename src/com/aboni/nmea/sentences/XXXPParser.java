package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.parser.SentenceParser;
import net.sf.marineapi.nmea.sentence.TalkerId;

public class XXXPParser extends SentenceParser implements XXXPSentence {

	public static final String NMEA_SENTENCE_TYPE = "XXP";
	
    private static final int MAG_X      = 0;
    private static final int MAG_Y      = 1;
    private static final int MAG_Z      = 2;
    private static final int TEMP       = 3;
    private static final int PRES       = 4;
    private static final int HEADING    = 5;
    private static final int ROT_X      = 6;
    private static final int ROT_Y      = 7;
    private static final int ROT_Z      = 8;
    private static final int VOLTAGE    = 9;
    private static final int VOLTAGE1   = 10;
    private static final int RPM        = 11;

    public XXXPParser(TalkerId id) {
        super(id, NMEA_SENTENCE_TYPE, 12);
    }

    public XXXPParser(String nmea) {
        super(nmea, NMEA_SENTENCE_TYPE);
    }

    public XXXPParser(String nmea, String type) {
        super(nmea, type);
    }

    public XXXPParser(TalkerId talker, String type, int size) {
        super(talker, type, size);
    }

    public XXXPParser(char begin, TalkerId talker, String type, int size) {
        super(begin, talker, type, size);
    }
    
    /* (non-Javadoc)
     * @see XXXPSentence#getMagX()
     */
    @Override
    public double getMagX() {
        return getDoubleValue(MAG_X);
    }

    /* (non-Javadoc)
     * @see XXXPSentence#getMagZ()
     */
    @Override
    public double getMagZ() {
        return getDoubleValue(MAG_Z);
    }

    /* (non-Javadoc)
     * @see XXXPSentence#getMagY()
     */
    @Override
    public double getMagY() {
        return getDoubleValue(MAG_Y);
    }
    
    /* (non-Javadoc)
     * @see XXXPSentence#getHeading()
     */
    @Override
    public double getHeading() {
        return getDoubleValue(HEADING);
    }
    
    /* (non-Javadoc)
     * @see XXXPSentence#getRotationX()
     */
    @Override
    public double getRotationX() {
        return getDoubleValue(ROT_X);
    }

    /* (non-Javadoc)
     * @see XXXPSentence#getRotationY()
     */
    @Override
    public double getRotationY() {
        return getDoubleValue(ROT_Y);
    }

    /* (non-Javadoc)
     * @see XXXPSentence#getRotationZ()
     */
    @Override
    public double getRotationZ() {
        return getDoubleValue(ROT_Z);
    }

    /* (non-Javadoc)
     * @see XXXPSentence#getPressure()
     */
    @Override
    public double getPressure() {
        return getDoubleValue(PRES);
    }
    
    /* (non-Javadoc)
     * @see XXXPSentence#getTemperature()
     */
    @Override
    public double getTemperature() {
        return getDoubleValue(TEMP);
    }

    @Override
    public void setMagX(double x) {
        setDoubleValue(MAG_X, x);
        
    }

    @Override
    public void setMagY(double y) {
        setDoubleValue(MAG_Y, y);
    }

    @Override
    public void setMagZ(double z) {
        setDoubleValue(MAG_Z, z);
    }

    @Override
    public void setHeading(double h) {
        setDegreesValue(HEADING, h);
    }

    @Override
    public void setRotationX(double x) {
        setDoubleValue(ROT_X, x);
    }

    @Override
    public void setRotationY(double y) {
        setDoubleValue(ROT_Y, y);
    }

    @Override
    public void setRotationZ(double z) {
        setDoubleValue(ROT_Z, z);
    }

    @Override
    public void setPressure(double p) {
        setDoubleValue(PRES, p, 5, 0);        
    }

    @Override
    public void setTemperature(double t) {
        setDoubleValue(TEMP, t, 3, 2);
    }

    @Override
    public double getVoltage() {
        return getDoubleValue(VOLTAGE);
    }

    @Override
    public void setVoltage(double v) {
        setDoubleValue(VOLTAGE, v, 3, 3);
    }
    @Override
    public double getVoltage1() {
        return getDoubleValue(VOLTAGE1);
    }

    @Override
    public void setVoltage1(double v) {
        setDoubleValue(VOLTAGE1, v, 3, 3);
    }

    @Override
    public double getRPM() {
        return getDoubleValue(RPM);
    }

    @Override
    public void setRPM(double rpm) {
        setDoubleValue(RPM, rpm, 5, 0);
    }
}
