package com.aboni.misc;

import net.sf.marineapi.nmea.sentence.Sentence;

public interface HeadingCache {

    Event<Double> getMagneticHeading();
    Event<Double> getTrueHeading();
    Event<Double> getCompassHeading();
    Event<Double> getSpeed();


    void feed(Sentence s);

}
