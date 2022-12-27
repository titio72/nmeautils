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

import net.sf.marineapi.nmea.util.CompassPoint;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilsTest {

    @Test
    public void testRoundingDown() {
		double d = 5.7222223443;
		double d1 = Utils.round(d, 3);
		assertEquals(5.722, d1, 0.0005);
	}

	@Test
	public void testRoundingUp() {
		double d = 5.7272258;
		double d1 = Utils.round(d, 2);
		assertEquals(5.73, d1, 0.005);
	}

	@Test
	public void testFormatLL() {
		assertEquals("001 30.000 W", Utils.formatLL(1.5, CompassPoint.WEST));
		assertEquals("001 30.000 E", Utils.formatLL(1.5, CompassPoint.EAST));
		assertEquals("001 30.000 N", Utils.formatLL(1.5, CompassPoint.NORTH));
		assertEquals("001 30.000 S", Utils.formatLL(1.5, CompassPoint.SOUTH));
	}

	@Test
	public void testNormalizeDegrees0_360() {
		assertEquals(30.0, Utils.normalizeDegrees0To360(30.0), 0.0001);
		assertEquals(330.0, Utils.normalizeDegrees0To360(-30.0), 0.0001);
		assertEquals(180.0, Utils.normalizeDegrees0To360(180.0), 0.0001);
		assertEquals(180.0, Utils.normalizeDegrees0To360(-180.0), 0.0001);
		assertEquals(270.0, Utils.normalizeDegrees0To360(270.0), 0.0001);
		assertEquals(270.0, Utils.normalizeDegrees0To360(-90.0), 0.0001);
	}

	@Test
	public void testNormalizeDegrees180_180() {
		assertEquals( 30.0, Utils.normalizeDegrees180To180(30.0), 0.0001);
		assertEquals(-30.0, Utils.normalizeDegrees180To180(330.0), 0.0001);
	}

	@Test
	public void testGetLatitudeEmisphere() {
		assertEquals("E", Utils.getLongitudeEmisphere(11.0));
		assertEquals("W", Utils.getLongitudeEmisphere(-11.0));
	}

	@Test
	public void testGetLongitudeEmisphere() {
		assertEquals("N", Utils.getLatitudeEmisphere(43.0));
		assertEquals("S", Utils.getLatitudeEmisphere(-43.0));
	}

	@Test
	public void testGetSignedLatitude() {
		assertEquals( 43.0, Utils.getSignedLatitude(43.0, 'N'), 0.00001);
		assertEquals(-43.0, Utils.getSignedLatitude(43.0, 'S'), 0.00001);
	}

	@Test
	public void testGetSignedLongitude() {
		assertEquals( 11.0,  Utils.getSignedLongitude(11.0, 'E'), 0.00001);
		assertEquals(-11.0,  Utils.getSignedLongitude(11.0, 'W'), 0.00001);
	}

	@Test
	public void testGetNormal360Ref() {
		assertEquals(31.0, Utils.getNormal180(30.0, 31.0), 0.00001);
		assertEquals( 1.0, Utils.getNormal180(0.0,  1.0), 0.00001);
		assertEquals(-1.0, Utils.getNormal180(0.0, -1.0), 0.00001);
		assertEquals(-1.0, Utils.getNormal180(0.0, 359.0), 0.00001);
		assertEquals(-1.0, Utils.getNormal180(10.0, 359.0), 0.00001);
		assertEquals(-150.0, Utils.getNormal180(0.0, 210.0), 0.00001);
		assertEquals(-90.0, Utils.getNormal180(-90.0, 270.0), 0.00001);
		assertEquals(2.0, Utils.getNormal180(-1.0, 2.0), 0.00001);
		assertEquals(392.0, Utils.getNormal180(359.0, 32.0), 0.00001);
	}
	
	@Test
	public void testTack() {
		assertEquals(270, Utils.tack(90, -90), 0.001);
		assertEquals(315, Utils.tack(135, -90), 0.001);
		assertEquals(225, Utils.tack(135, 45), 0.001);
	}
}
