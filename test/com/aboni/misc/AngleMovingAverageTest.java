package com.aboni.misc;

import static org.junit.Assert.*;

import org.junit.Test;

public class AngleMovingAverageTest {

	@Test
	public void testNoPurgeNoNorthCross() {
		Sample[] samples = new Sample[] {
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
		double a = 330;
		for (long i = 0; i<1000; i++) {
			a = Utils.normalizeDegrees0_360(330.0 + 60.0*((double)i/1000.0));
			m.setSample(i * 100, a);
		}
		assertEquals(30.0, m.getAvg(), 5.0);
	}
}
