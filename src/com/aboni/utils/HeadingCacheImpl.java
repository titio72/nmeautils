/*
 * Copyright (c) 2022,  Andrea Boni
 * This file is part of NMEARouter.
 * NMEARouter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * NMEARouter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with NMEARouter.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aboni.utils;

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
                    ePos.ev = ((PositionSentence) s).getPosition();
                    ePos.timestamp = System.currentTimeMillis();
                } else if (s instanceof VHWSentence) {
                    eSpeed.ev = ((VHWSentence)s).getSpeedKnots();
                    eSpeed.timestamp = System.currentTimeMillis();
                } else if (s instanceof HDMSentence) {
                    eHeadingCompass.ev = ((HDMSentence) s).getHeading();
                    eHeadingCompass.timestamp = System.currentTimeMillis();
                } else if (s instanceof HDTSentence) {
                    eHeadingTrue.ev = ((HDTSentence) s).getHeading();
                    eHeadingTrue.timestamp = System.currentTimeMillis();
                } else if (s instanceof HDGSentence) {
                    eHeadingMagnetic.ev = Utils.getMagHeading((HeadingSentence)s);
                    eHeadingTrue.ev = Utils.getMagHeading((HeadingSentence)s);
                    eHeadingCompass.ev = ((HeadingSentence)s).getHeading();
                    eHeadingMagnetic.timestamp = System.currentTimeMillis();
                }
            }
        } catch (DataNotAvailableException ignore) {
            // ignore this exception
        }
    }
}
