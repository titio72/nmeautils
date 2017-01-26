package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.sentence.Sentence;

public interface XDPSentence extends Sentence {
    double getDepth();
    double getMaxDepth1h();
    double getMinDepth1h();
    void setDepth(double v);
    void setMaxDepth1h(double v);
    void setMinDepth1h(double v);
    
}
