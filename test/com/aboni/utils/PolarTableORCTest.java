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

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PolarTableORCTest {
    private static final String dufour40_ORC_STD =
            "twa/tws;6;8;10;12;14;16;20\n" +
                    "0;0;0;0;0;0;0;0\n" +
                    "52;5.17;6.15;6.93;7.36;7.56;7.64;7.68\n" +
                    "60;5.5;6.49;7.19;7.56;7.76;7.87;7.93\n" +
                    "75;5.75;6.78;7.39;7.73;7.97;8.16;8.38\n" +
                    "90;5.77;6.94;7.59;7.94;8.12;8.29;8.77\n" +
                    "110;5.77;6.99;7.66;8.06;8.44;8.83;9.27\n" +
                    "120;5.58;6.79;7.54;7.96;8.36;8.8;9.66\n" +
                    "135;4.99;6.14;7.1;7.67;8.05;8.45;9.32\n" +
                    "150;4.21;5.3;6.26;7.09;7.63;8;8.76";

    private PolarTable t;

    @Before
    public void setup() throws IOException {
        t = new PolarTableORC();
        t.load(new StringReader(dufour40_ORC_STD));
    }

    @Test
    public void testBeamExactWind() {
        float speed = t.getSpeed(90, 10);
        assertEquals(7.59, speed, 0.001);
    }

    @Test
    public void testBeamWindInRange() {
        float speed = t.getSpeed(90, 11);
        assertTrue(7.59 < speed);
        assertTrue(7.94 > speed);
    }

    @Test
    public void testBeamWindHighWind() {
        float speed = t.getSpeed(90, 35);
        assertTrue(8.77 <= speed);
    }

    @Test
    public void testBeamWindLowWind() {
        float speed = t.getSpeed(90, 3);
        assertTrue(5.77 > speed);
        assertTrue(0.00 <= speed);
    }

}