package com.aboni.misc;

import junit.framework.TestCase;

public class SpeedMovingAverageTest extends TestCase {

	public void testEmpty() {
		SpeedMovingAverage sma = new SpeedMovingAverage(1000);
		assertTrue(Double.isNaN(sma.getAvg()));
	}
	
	public void testSamplesDoNotSlide() {
		SpeedMovingAverage sma = new SpeedMovingAverage(1000);
		sma.setSample(100, 10.5);
		sma.setSample(200, 11.5);
		assertEquals(11.0,  sma.getAvg(), 0.00001);
	}
	
	public void testSamplesSlide() {
		SpeedMovingAverage sma = new SpeedMovingAverage(1000);
		sma.setSample( 100, 10.5);
		sma.setSample( 700, 11.5);
		sma.setSample(1300, 12.5);
		assertEquals(12.0,  sma.getAvg(), 0.00001);
	}
	
	public void testSlideWIthNoSamples0() {
		SpeedMovingAverage sma = new SpeedMovingAverage(1000);
		sma.setSample( 100, 10.5);
		sma.setSample( 700, 11.5);
		sma.setTime(3000);
		assertTrue(Double.isNaN(sma.getAvg()));
	}
	
	public void testSlideWIthNoSamples1() {
		SpeedMovingAverage sma = new SpeedMovingAverage(1000);
		sma.setSample( 100, 10.5);
		sma.setSample( 700, 11.5);
		sma.setTime(1600);
		assertEquals(11.5,  sma.getAvg(), 0.00001);
	}
	
}
