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

import com.aboni.data.SpeedMovingAverage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SpeedMovingAverageTest {

    @Test
    public void testEmpty() {
        SpeedMovingAverage sma = new SpeedMovingAverage(1000);
		assertTrue(Double.isNaN(sma.getAvg()));
	}
	
	@Test
	public void testSamplesDoNotSlide() {
		SpeedMovingAverage sma = new SpeedMovingAverage(1000);
		sma.setSample(100, 10.5);
		sma.setSample(200, 11.5);
		assertEquals(11.0,  sma.getAvg(), 0.00001);
	}
	
	@Test
	public void testSamplesSlide() {
		SpeedMovingAverage sma = new SpeedMovingAverage(1000);
		sma.setSample( 100, 10.5);
		sma.setSample( 700, 11.5);
		sma.setSample(1300, 12.5);
		assertEquals(12.0,  sma.getAvg(), 0.00001);
	}
	
	@Test
	public void testSlideWIthNoSamples0() {
		SpeedMovingAverage sma = new SpeedMovingAverage(1000);
		sma.setSample( 100, 10.5);
		sma.setSample( 700, 11.5);
		sma.setTime(3000);
		assertTrue(Double.isNaN(sma.getAvg()));
	}
	
	@Test
	public void testSlideWIthNoSamples1() {
		SpeedMovingAverage sma = new SpeedMovingAverage(1000);
		sma.setSample( 100, 10.5);
		sma.setSample( 700, 11.5);
		sma.setTime(1600);
		assertEquals(11.5,  sma.getAvg(), 0.00001);
	}
	
}
