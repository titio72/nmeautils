package com.aboni.geo;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.sf.marineapi.nmea.util.Position;

public class TrackTest {

	Position marina = new Position(43.679416, 10.267679);
	Position capraia = new Position(43.051326, 9.839279);
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGorgonaWestOfCourse() {
		Position somewhere = new Position(43.421775, 9.960448);
		Track r = new Track(marina, capraia);
		r.setPos(somewhere, 4.0);
		assertEquals(5.115, r.getXTE(), 0.001);
		assertEquals(Track.LEFT, r.getTurn());

		/*
		System.out.println(r.getXTE());
		System.out.println(r.getBRG());
		System.out.println(r.getBearing());
		double b = r.getBearing() - r.getBRG();
		System.out.println(b);
		*/
				
	}

	@Test
	public void testGorgonaEastOfCourse() {
		Position somewhere = new Position(43.338195, 10.263917);
		Track r = new Track(marina, capraia);
		r.setPos(somewhere, 4.0);
		assertEquals(8.972, r.getXTE(), 0.001);
		assertEquals(Track.RIGHT, r.getTurn());

		/*
		System.out.println(r.getXTE());
		System.out.println(r.getBRG());
		System.out.println(r.getBearing());
		double b = r.getBearing() - r.getBRG();
		System.out.println(b);
		*/
				
	}

	
	
}
