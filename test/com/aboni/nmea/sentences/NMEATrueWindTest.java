package com.aboni.nmea.sentences;

import com.aboni.utils.NMEATrueWind;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.sentence.VHWSentence;
import net.sf.marineapi.nmea.util.Units;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NMEATrueWindTest {

	MWVSentence sendMWV(boolean trueWind, double speed, double angle) {
		MWVSentence s = (MWVSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.MWV);
		s.setAngle(angle);
		s.setSpeed(speed);
		s.setTrue(trueWind);
		s.setSpeedUnit(Units.KNOT);
		return s;
	}

	MWVSentence sendMWV_MS(boolean trueWind, double speed, double angle) {
		MWVSentence s = (MWVSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.MWV);
		s.setAngle(angle);
		s.setSpeed(speed);
		s.setTrue(trueWind);
		s.setSpeedUnit(Units.METER);
		return s;
	}

	VHWSentence sendVHW(double speed, double angle) {
		VHWSentence s = (VHWSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.VHW);
		//s.setHeading(angle);
		s.setMagneticHeading(angle);
		s.setSpeedKnots(speed);
		return s;
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test1() {
		NMEATrueWind trueWind = new NMEATrueWind(TalkerId.P);
		trueWind.setSpeed(sendVHW(5.3, 17.0), 1000000);
		trueWind.setWind(sendMWV(false, 9.7, 46.0), 1000000);
		trueWind.calcMWVSentence(1000, 1000000);
		MWVSentence t = trueWind.getTrueWind();
		System.out.println(t);
	}

	@Test
	public void testHeadNorthWindFromStarboard() {
		NMEATrueWind trueWind = new NMEATrueWind(TalkerId.P);
		trueWind.setSpeed(sendVHW(5.0, 0.0), 1000000);
		trueWind.setWind(sendMWV(false, 5 * Math.sqrt(2), 45.0), 1000000);
		trueWind.calcMWVSentence(1000, 1000000);
		MWVSentence t = trueWind.getTrueWind();
		assertNotNull(t);
		assertEquals(5.0, t.getSpeed(), 0.1);
		assertEquals(90.0, t.getAngle(), 0.5);
		assertEquals(Units.KNOT, t.getSpeedUnit());
	}

	@Test
	public void testHeadNorthWindFromStarboard_MperS() {
		NMEATrueWind trueWind = new NMEATrueWind(TalkerId.P);
		trueWind.setSpeed(sendVHW(5.0, 0.0), 1000000);
		trueWind.setWind(sendMWV_MS(false, 5 * Math.sqrt(2) * 0.514444, 45.0), 1000000);
		trueWind.calcMWVSentence(1000, 1000000);
		MWVSentence t = trueWind.getTrueWind();
		System.out.println(t);
	}


}
