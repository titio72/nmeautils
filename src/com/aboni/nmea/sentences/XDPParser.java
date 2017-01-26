package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.parser.SentenceParser;
import net.sf.marineapi.nmea.sentence.TalkerId;

public class XDPParser extends SentenceParser implements XDPSentence {

    public static final String NMEA_SENTENCE_TYPE = "XDP"; 
    
    public static final int DEPTH=0;
    public static final int MAXDEPTH=1;
    public static final int MINDEPTH=2;
    
    
    public XDPParser(String nmea) {
        super(nmea);
    }
    
    public XDPParser(TalkerId talker) {
        super(talker, NMEA_SENTENCE_TYPE, 3);
    }
    
    @Override
    public double getDepth() {
        return getDoubleValue(DEPTH);
    }

    @Override
    public double getMaxDepth1h() {
        return getDoubleValue(MAXDEPTH);
    }

    @Override
    public double getMinDepth1h() {
        return getDoubleValue(MINDEPTH);
    }

    @Override
    public void setDepth(double v) {
        setDoubleValue(DEPTH, v, 1, 1);
    }

    @Override
    public void setMaxDepth1h(double v) {
        setDoubleValue(MAXDEPTH, v, 1, 1);
    }

    @Override
    public void setMinDepth1h(double v) {
        setDoubleValue(MINDEPTH, v, 1, 1);
    }   
}
