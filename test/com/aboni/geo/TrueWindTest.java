package com.aboni.geo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TrueWindTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testRun() {
		TrueWind a = new TrueWind(6.4, 180, 5.2);
		assertEquals(11.6, a.getTrueWindSpeed(), 0.0001);
		assertEquals(180, a.getTrueWindDeg(), 0.00001);
	}

	@Test
	public void testHead() {
		TrueWind a = new TrueWind(6.4, 0, 5.2);
		assertEquals(1.2, a.getTrueWindSpeed(), 0.0001);
		assertEquals(180, a.getTrueWindDeg(), 0.00001);
	}

	@Test
	public void testStartboard() {
		TrueWind a = new TrueWind(5.0, 60, 10.0);
		assertEquals(10.0*Math.sqrt(3.0)/2.0, a.getTrueWindSpeed(), 0.0001);
		assertEquals(90, a.getTrueWindDeg(), 0.00001);
	}

	@Test
	public void testPort() {
		TrueWind a = new TrueWind(5.0, -60, 10.0);
		assertEquals(10.0*Math.sqrt(3.0)/2.0, a.getTrueWindSpeed(), 0.0001);
		assertEquals(-90, a.getTrueWindDeg(), 0.00001);
	}

}
