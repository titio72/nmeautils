package com.aboni.nmea.sentences;

import com.aboni.misc.NMEATrueWind;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.*;
import net.sf.marineapi.nmea.util.Date;
import net.sf.marineapi.nmea.util.Time;
import net.sf.marineapi.nmea.util.Units;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class NMEATrueWindTest {

	MWVSentence sendMWV(boolean trueWind, double speed, double angle) {
		MWVSentence s = (MWVSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.MWV);
		s.setAngle(angle);
		s.setSpeed(speed);
		s.setTrue(trueWind);
		s.setSpeedUnit(Units.KNOT);
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
	public void testHeadNorthWindFromStarboard() {
		NMEATrueWind trueWind = new NMEATrueWind(TalkerId.P);
		trueWind.setSpeed(sendVHW(5.0, 0.0), 1000000);
		trueWind.setWind(sendMWV(false, 5 * Math.sqrt(2), 45.0), 1000000);
		trueWind.calcMWVSentence(1000, 1000000);
		MWVSentence t = trueWind.getTrueWind();
		System.out.println(t);
	}


}
