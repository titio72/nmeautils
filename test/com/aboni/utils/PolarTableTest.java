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

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class PolarTableTest {
    private static final String dufour385 =
            "angle,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20\n" +
                    "32,,1,,1.99,,03.03,,3.95,,4.68,,05.21,,05.56,,5.79,,,,5.98\n" +
                    "36,,01.16,,02.39,,03.06,,04.57,,05.03,,5.81,,06.14,,06.34,,,,06.52\n" +
                    "40,,01.32,,2.72,,04.05,,05.07,,05.08,,06.27,,06.54,,6.69,,,,6.85\n" +
                    "45,,01.49,,03.08,,04.49,,05.54,,06.28,,6.66,,6.86,,6.98,,,,07.13\n" +
                    "52,,1.69,,03.48,,4.96,,06.03,,6.69,,7,,07.16,,07.26,,,,07.42\n" +
                    "60,,1.86,,03.08,,05.31,,06.37,,6.95,,07.26,,07.04,,07.51,,,,7.67\n" +
                    "70,,1.99,,04.04,,05.56,,06.57,,07.11,,07.45,,7.64,,7.75,,,,7.93\n" +
                    "80,,02.13,,04.27,,5.89,,6.76,,07.15,,07.51,,7.77,,7.94,,,,08.15\n" +
                    "90,,02.27,,04.51,,06.14,,6.95,,07.26,,07.47,,7.74,,7.98,,,,08.33\n" +
                    "100,,02.31,,04.59,,06.22,,07.05,,07.41,,7.62,,7.78,,7.93,,,,08.27\n" +
                    "110,,02.25,,04.49,,06.11,,6.99,,07.47,,7.73,,7.92,,08.01,,,,08.41\n" +
                    "120,,02.08,,04.19,,5.78,,06.08,,07.38,,7.79,,08.08,,08.29,,,,8.65\n" +
                    "130,,1.83,,3.74,,05.32,,06.49,,07.17,,7.63,,08.02,,08.39,,,,8.92\n" +
                    "135,,01.07,,03.05,,05.05,,06.26,,07.01,,07.05,,07.09,,08.28,,,,9\n" +
                    "140,,01.58,,03.24,,4.76,,5.97,,6.81,,07.34,,7.75,,08.13,,,,08.09\n" +
                    "150,,01.33,,2.73,,04.01,,05.27,,06.27,,6.94,,07.42,,7.81,,,,08.58\n" +
                    "160,,01.16,,02.37,,03.58,,04.07,,05.07,,06.53,,07.11,,07.54,,,,08.28\n" +
                    "170,,01.05,,02.14,,03.24,,04.03,,05.26,,06.12,,6.79,,07.27,,,,8\n" +
                    "180,,1,,1.97,,2.99,,3.98,,04.09,,5.74,,06.47,,7,,,,7.75";

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


    @Test
    public void testInterpolateSpeed() throws IOException {
        PolarTable t = new PolarTableImpl();
        t.load(new StringReader(dufour385));

        float speedRun3Kn = t.getSpeed(180, 3);
        assert (speedRun3Kn > 1);
        assert (speedRun3Kn < 1.97);

        // less than the first available
        float speedRun1Kn = t.getSpeed(180, 1);
        assert (speedRun1Kn >= 0);
        assert (speedRun1Kn < 1);

        // more than the last available
        float speedRun25Kn = t.getSpeed(180, 25);
        assert (speedRun25Kn >= 7.75);
    }
	
	@Test
	public void testLoad() throws IOException {
        PolarTable t = new PolarTableImpl();
        t.load(new StringReader(dufour385));

        assertEquals(1, t.getSpeed(180, 2), 0.0001);
        assertEquals(4.09, t.getSpeed(180, 10), 0.0001);
        assertEquals(7.75, t.getSpeed(180, 20), 0.0001);
        assertEquals(7.26, t.getSpeed(90, 10), 0.0001);

    }

    @Test
    public void testLoadAndDump() throws IOException {
        PolarTable t = new PolarTableImpl();
        t.load(new StringReader(dufour385));

        Writer w = new FileWriter("dufour385i.csv");
        t.dump(w);
        w.close();
    }

    @Test
    public void testBeatAngle() throws IOException {
        PolarTable t = new PolarTableImpl();
        t.load(new StringReader(dufour385));

        System.out.println(t.getBeatAngle());
    }

    @Test
    public void testLoadORCStd() throws IOException {
        PolarTableImpl t = new PolarTableImpl();
        t.loadAlternative(new StringReader(dufour40_ORC_STD));
        StringWriter w = new StringWriter();
        t.dump(w);
        System.out.println(w.toString());
        assertEquals(5.5, t.getSpeed(60, 6), 0.0001);
    }

    @Test
    public void testRunAngle() throws IOException {
        PolarTableImpl t = new PolarTableImpl();
        t.loadAlternative(new StringReader(dufour40_ORC_STD));
        assertEquals(0.0, t.getSpeed(170, 12), 0.0001);
    }
}
