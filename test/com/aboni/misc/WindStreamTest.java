package com.aboni.misc;

import static org.junit.Assert.*;

import java.awt.SecondaryLoop;

import org.junit.Before;
import org.junit.Test;

import com.aboni.misc.WindStream.Conf;
import com.aboni.nmea.sentences.VWRSentence;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.sentence.VHWSentence;
import net.sf.marineapi.nmea.util.Direction;
import net.sf.marineapi.nmea.util.Side;

public class WindStreamTest {

	@Before
	public void setUp() throws Exception {
	}

	private static Conf getConf(boolean useVWR, boolean useCOG, boolean forceCalcTrue) {
		Conf c = new Conf();
		c.useCOG = useCOG;
		c.useVWR = useVWR;
		c.forceCalcTrue = forceCalcTrue;
		return c;
	}
	
	private VHWSentence getVHW(double heading, double speed) {
		VHWSentence s = (VHWSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.VHW);
		s.setHeading(heading);
		s.setMagneticHeading(heading);
		s.setSpeedKnots(speed);
		return s;
	}
	
	private RMCSentence getRMC(double heading, double speed) {
		RMCSentence s = (RMCSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.RMC);
		s.setCourse(heading);
		s.setSpeed(speed);
		return s;
	}
	
	private VWRSentence getVWR(double direction, double speed) {
		VWRSentence s = (VWRSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.VWR);
		direction = Utils.normalizeDegrees180_180(direction);
		s.setAngle(Math.abs(direction));
		s.setSide(direction<0?Side.PORT:Side.STARBOARD);
		s.setSpeed(speed);
		return s;
	}
	
	private MWVSentence getMWV(double direction, double speed, boolean t) {
		MWVSentence s = (MWVSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.MWV);
		s.setAngle(direction);
		s.setSpeed(speed);
		s.setTrue(t);
		return s;
	}
	
	@Test
	public void test() {
		Conf c = getConf(false, false, false);
		WindStream ws = new WindStream(TalkerId.II, c);
		
		

	
	
	
	
	}

	
	
}
