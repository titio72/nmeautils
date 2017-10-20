package com.aboni.seatalk;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.STALKSentence;

public class Stalk84Test {

	String[] stream = new String[] {
	"$STALK,84,86,83,88,40,00,0D,02,06*10",
	"$STALK,84,06,83,88,40,00,0E,02,06*19",
	"$STALK,84,06,83,88,40,00,0E,02,06*19",
	"$STALK,84,06,03,0C,42,00,0E,02,06*60",
	"$STALK,84,06,03,0C,42,00,0E,02,06*60",
	"$STALK,84,06,03,0C,42,00,0E,02,06*60",
	"$STALK,84,06,C3,AC,42,00,0E,02,06*62",
	"$STALK,84,06,C3,AC,42,00,05,02,06*12",
	"$STALK,84,06,C3,AC,42,00,00,02,06*17",
	"$STALK,84,06,03,0C,42,00,00,02,06*15",
	"$STALK,84,06,03,0C,42,00,0A,02,06*64",
	"$STALK,84,86,02,0C,42,00,0D,02,06*68",
	"$STALK,84,86,02,0C,42,00,0C,02,06*6F",
	"$STALK,84,86,02,0E,42,00,09,02,06*13",
	"$STALK,84,06,03,0E,42,00,08,02,06*1B",
	"$STALK,84,06,03,0C,42,00,09,02,06*1C",
	"$STALK,84,06,03,0A,42,00,07,02,06*10",
	"$STALK,84,06,03,08,42,00,06,02,06*68",
	"$STALK,84,06,03,06,42,00,06,02,06*66",
	"$STALK,84,06,03,06,42,00,02,02,06*62",
	"$STALK,84,06,03,06,42,00,04,02,06*64",
	"$STALK,84,06,03,06,42,00,02,02,06*62",
	"$STALK,84,06,03,00,40,00,03,02,06*67",
	"$STALK,84,86,03,00,40,00,03,02,06*6F",
	"$STALK,84,86,03,00,40,00,03,02,06*6F",
	"$STALK,84,86,03,00,40,00,03,02,06*6F",
	"$STALK,84,06,03,00,40,00,03,02,06*67",
	"$STALK,84,06,03,00,40,00,03,02,06*67"};
	
	
	
	@Before
	public void setUp() throws Exception {
	}

	
	@Test
	public void test0() throws IOException {
		for (String s: stream) {
			STALKSentence nmea = (STALKSentence)SentenceFactory.getInstance().createParser(s);
			Stalk84 s84 = new Stalk84(nmea);
			System.out.format("%d %d %b %s%n", s84.getHeading(), s84.getAutoDeg(), s84.isAuto(), s84.getTurning().toString());
		}
	}
	
	
	@Test
	public void test() throws IOException {
		STALKSentence s = (STALKSentence)SentenceFactory.getInstance().createParser("$STALK,84,06,C4,B0,42,00,F5,02,06*13");
		Stalk84 ss = new Stalk84(s);
		//ss.dump(System.out);
		assertEquals(8, ss.getHeading());
		assertTrue(ss.isAuto());
		assertEquals(358, ss.getAutoDeg());
		assertFalse(ss.isWind());
		assertFalse(ss.isTrack());
		assertFalse(ss.isErr_off_course());
		assertFalse(ss.isErr_wind_shift());
		assertEquals(-11, ss.getRudder());
	}
	
	@Test
	public void test1() throws IOException {
		STALKSentence s = (STALKSentence)SentenceFactory.getInstance().createParser("$STALK,84,06,03,00,40,00,03,02,06*67");
		Stalk84 ss = new Stalk84(s);
		//ss.dump(System.out);
		assertEquals(6, ss.getHeading());
		assertFalse(ss.isAuto());
		assertFalse(ss.isWind());
		assertFalse(ss.isTrack());
		assertFalse(ss.isErr_off_course());
		assertEquals(3, ss.getRudder());
	}

	@Test 
	public void testReverse() {
		String nmea = "$STALK,84,06,03,00,40,00,03,02,06";
		Stalk84 s0 = Stalk84.parse(nmea);
		Stalk84 s1 = new Stalk84(s0.getHeading(), s0.getAutoDeg(), s0.getRudder(), 
				s0.getStatus(), s0.getError(), s0.getTurning());
		assertEquals(s0.getSTALKSentence(), s1.getSTALKSentence());
	}
	
}
