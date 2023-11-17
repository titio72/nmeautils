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

import com.aboni.data.AngleMovingAverage;
import com.aboni.data.MovingAverage;
import com.aboni.data.Sample;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AngleMovingAverageTest {

    @Test
    public void testNoPurgeNoNorthCross() {
        Sample[] samples = new Sample[]{
				new Sample(      0, 3.0),
				new Sample(    500, 3.1),
				new Sample(   1000, 2.9),
				new Sample(   1500, 3.0)
		};
		AngleMovingAverage m = new AngleMovingAverage(5000);
		for (Sample s: samples) m.setSample(s.getTs(), s.getValue());
		assertEquals(3.0, m.getAvg(), 0.00001);
	}

	@Test
	public void testNoPurgeCrossNorth() {
		Sample[] samples = new Sample[] {
				new Sample(      0,   1.0),
				new Sample(    500, 359.0),
				new Sample(   1000,   3.0),
				new Sample(   1500,   1.0)
		};
		AngleMovingAverage m = new AngleMovingAverage(5000);
		for (Sample s: samples) m.setSample(s.getTs(), s.getValue());
		assertEquals(1.0, m.getAvg(), 0.00001);
	}

	@Test
	public void testNoPurgeCrossNorthNeg() {
		Sample[] samples = new Sample[] {
				new Sample(      0,   1.0),
				new Sample(    500,  -1.0),
				new Sample(   1000,   3.0),
				new Sample(   1500,   1.0)
		};
		AngleMovingAverage m = new AngleMovingAverage(5000);
		for (Sample s: samples) m.setSample(s.getTs(), s.getValue());
		assertEquals(1.0, m.getAvg(), 0.00001);
	}
	
	@Test
	public void testPurgeNonCrossingValue() {
		Sample[] samples = new Sample[] {
				new Sample(      0,   1.0),
				new Sample(    500,  -1.0),
				new Sample(   1000,   3.0),
				new Sample(   1500,   1.0),
				new Sample(  61000,   1.0)
		};
		AngleMovingAverage m = new AngleMovingAverage(5000);
		for (Sample s: samples) m.setSample(s.getTs(), s.getValue());
		assertEquals(1.0, m.getAvg(), 0.00001);
	}
	
	@Test
	public void testCrossNorth330_030() {
		MovingAverage m = new AngleMovingAverage(10000);
		double a;
		for (long i = 0; i<1000; i++) {
			a = Utils.normalizeDegrees0To360(330.0 + 60.0*((double)i/1000.0));
			m.setSample(i * 100, a);
		}
		assertEquals(30.0, m.getAvg(), 5.0);
	}
}
