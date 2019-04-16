package com.aboni.misc;

import net.sf.marineapi.nmea.parser.DataNotAvailableException;
import net.sf.marineapi.nmea.sentence.*;
import net.sf.marineapi.nmea.util.Position;

public class HeadingCacheImpl implements HeadingCache {

    private final Event<Position> ePos = new Event<>(null, 0);
    private final Event<Double> eHeadingMagnetic = new Event<>(null, 0);
    private final Event<Double> eHeadingTrue = new Event<>(null, 0);
    private final Event<Double> eHeadingCompass = new Event<>(null, 0);
    private final Event<Double> eSpeed = new Event<>(null, 0);

    @Override
    public Event<Double> getMagneticHeading() {
        return null;
    }

    @Override
    public Event<Double> getTrueHeading() {
        return null;
    }

    @Override
    public Event<Double> getCompassHeading() {
        return null;
    }

    @Override
    public Event<Double> getSpeed() {
        return null;
    }

    @Override
    public void feed(final Sentence s) {
        try {
        if (s!=null) {
                if (s instanceof PositionSentence) {
                    ePos.event = ((PositionSentence) s).getPosition();
                    ePos.timestamp = System.currentTimeMillis();
                } else if (s instanceof VHWSentence) {
                    eSpeed.event = ((VHWSentence)s).getSpeedKnots();
                    eSpeed.timestamp = System.currentTimeMillis();
                } else if (s instanceof HDMSentence) {
                    eHeadingCompass.event = ((HDMSentence) s).getHeading();
                    eHeadingCompass.timestamp = System.currentTimeMillis();
                } else if (s instanceof HDTSentence) {
                    eHeadingTrue.event = ((HDTSentence) s).getHeading();
                    eHeadingTrue.timestamp = System.currentTimeMillis();
                } else if (s instanceof HDGSentence) {
                }
            }
        } catch (DataNotAvailableException ignore) {}
    }
}
