package com.aboni.misc;

import com.aboni.misc.WindStream.Conf;
import com.aboni.nmea.sentences.NMEASentenceFilter;
import com.aboni.nmea.sentences.VWRSentence;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.*;
import net.sf.marineapi.nmea.util.Side;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class WindStreamTest {

	private class WS extends WindStream {

		List<Event<Sentence>> output = new ArrayList<>();
		
		WS(boolean useVWR, boolean forceCalcTrue) {
			// use P to distinguish internally generated events
			super(TalkerId.P, WindStreamTest.getConf(useVWR, forceCalcTrue));
		}
		
		@Override
		protected void onProcSentence(Sentence s, long time) {
			assertNotNull(s);
			output.add(new Event<>(s, time));
		}

		
		List<Event<Sentence>> getOutput() {
			return output;
		}
		
		void reset() {
			output.clear();
		}

		void dump() {
			for (Event<Sentence> s: output) System.out.println(s.event);
		}

		/**
		 * Get the "count" instance of "type"
		 * @param type The type of instance to be found
		 * @param count Skip the first "count"
		 * @param  filter Optional filter
		 * @return The sentence in the stream of the request type
		 */
		Sentence find(final String type, int count, NMEASentenceFilter filter) {
			for (Event<Sentence> e: output) {
				if (e!=null && e.event!=null && type.equals(e.event.getSentenceId())) {
					if (filter==null || filter.match(e.event, null)) {
						if (count == 0) return e.event;
						else count--;
					}
				}
			}
			return null;
		}
	}
	
	@Before
	public void setUp() {
	}

	private static Conf getConf(boolean useVWR, boolean forceCalcTrue) {
		Conf c = new Conf();
		c.useVWR = useVWR;
		c.forceCalcTrue = forceCalcTrue;
		c.skipFirstCalculation = false;
		return c;
	}
	
	private static VHWSentence getVHW(double heading, double speed) {
		VHWSentence s = (VHWSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.VHW);
		s.setHeading(heading);
		s.setMagneticHeading(heading);
		s.setSpeedKnots(speed);
		return s;
	}
	
	private static VWRSentence getVWR(double direction, double speed) {
		VWRSentence s = (VWRSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.VWR);
		direction = Utils.normalizeDegrees180_180(direction);
		s.setAngle(Math.abs(direction));
		s.setSide(direction<0?Side.PORT:Side.STARBOARD);
		s.setSpeed(speed);
		return s;
	}
	
	private static MWVSentence getMWV(double direction, double speed, boolean t) {
		MWVSentence s = (MWVSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.MWV);
		s.setAngle(direction);
		s.setSpeed(speed);
		s.setTrue(t);
		return s;
	}
	
	private static MWDSentence getMWD(double direction, double speed) {
		MWDSentence s = (MWDSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.MWD);
		s.setMagneticWindDirection(direction);
		s.setTrueWindDirection(direction);
		s.setWindSpeedKnots(speed);
		return s;
	}

	private static HDGSentence getHDG(double heading) {
		HDGSentence s = (HDGSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.HDG);
		s.setDeviation(0.0);
		s.setVariation(0.0);
		s.setHeading(heading);
		return s;
	}
	
	
	@Test
	public void testCalcTrueAndAbsoluteWind() {
		WS ws = new WS(false, false);
		VHWSentence vhw = getVHW(0, 5);
		MWVSentence mwv_r = getMWV(45, 5 * Math.sqrt(2), false);
		HDGSentence hd = getHDG(0);
		
		long t0 = (System.currentTimeMillis()/86400)*86400;
		ws.onSentence(hd, t0 + 50);
		ws.onSentence(vhw, t0 + 50);
		ws.onSentence(mwv_r, t0 + 100);
		ws.dump();

		MWVSentence mwv_t = (MWVSentence)ws.find("MWV", 0, (Sentence s, String src)->{ return ((MWVSentence)s).isTrue(); });
		assertNotNull(mwv_t);
		assertEquals(TalkerId.P, mwv_t.getTalkerId());
		assertEquals(mwv_t.getSpeed(), 5, 0.05 /* allow 0.05 knots of difference*/ );
		assertEquals(mwv_t.getAngle(), 90, 0.5 /* allow 0.5 degree for rounding issues*/);

		MWDSentence mwd = (MWDSentence)ws.find("MWD", 0, null);
		assertEquals(TalkerId.P, mwd.getTalkerId());
		assertEquals(5, mwd.getWindSpeedKnots(), 0.05);
		assertEquals(90, mwd.getMagneticWindDirection(), 0.5);
	}

	
	
	@Test
	public void testNoMWD_VHW4Heading() {
		WS ws = new WS(false, false);
		VHWSentence vhw = getVHW(0, 5);
		MWVSentence mwv_r = getMWV(45, 5 * Math.sqrt(2), false);
		MWVSentence mwv_t = getMWV(90, 5, true);
		
		MWDSentence mwd = getMWD(90, 5);
		
		// run the stream for a few seconds
		long time = 0;
		for (int i = 0; i<10; i++) {
			time += 1000;
			ws.onSentence(vhw, time + 50);
			ws.onSentence(mwv_r, time + 100);
			ws.onSentence(mwv_t, time + 150);
		}

		time += 1000;
		ws.reset();
		ws.onSentence(vhw, time + 50);
		ws.onSentence(mwv_r, time + 100);
		ws.onSentence(mwv_t, time + 150);
	

		boolean ok = false;
		Iterator<Event<Sentence>> i = ws.getOutput().iterator();
		for (; i.hasNext(); ) {
			Sentence s = i.next().event;
			if (s instanceof MWDSentence) {
				assertEquals(TalkerId.P, s.getTalkerId());
				assertEquals(mwd.getWindSpeedKnots(), ((MWDSentence)s).getWindSpeedKnots(), 0.001);
				assertEquals(mwd.getMagneticWindDirection(), ((MWDSentence)s).getMagneticWindDirection(), 0.001);
				ok = true;
			}
		}
		assertTrue(ok);
	
	}
	
	@Test
	public void testNoMWD_InvalidVHW() {
		WS ws = new WS(false, false);
		VHWSentence vhw = getVHW(180, 0);
		MWVSentence mwv_r = getMWV(45, 5 * Math.sqrt(2), false);
		MWVSentence mwv_t = getMWV(90, 5, true);
		HDGSentence hd = getHDG(0);
		
		MWDSentence mwd = getMWD(90, 5);
		
		// run the stream for a few seconds
		long time = 0;
		for (int i = 0; i<10; i++) {
			time += 1000;
			ws.onSentence(hd, time + 50);
			ws.onSentence(vhw, time + 50);
			ws.onSentence(mwv_r, time + 100);
			ws.onSentence(mwv_t, time + 150);
		}

		time += 1000;
		ws.reset();
		ws.onSentence(hd, time + 50);
		ws.onSentence(vhw, time + 50);
		ws.onSentence(mwv_r, time + 100);
		ws.onSentence(mwv_t, time + 150);
	

		boolean ok = false;
		Iterator<Event<Sentence>> i = ws.getOutput().iterator();
		for (; i.hasNext(); ) {
			Sentence s = i.next().event;
			if (s instanceof MWDSentence) {
				assertEquals(TalkerId.P, s.getTalkerId());
				assertEquals(mwd.getWindSpeedKnots(), ((MWDSentence)s).getWindSpeedKnots(), 0.001);
				assertEquals(mwd.getMagneticWindDirection(), ((MWDSentence)s).getMagneticWindDirection(), 0.001);
				ok = true;
			}
		}
		assertTrue(ok);
	
	}

	@Test
	public void testBase() {
		WS ws = new WS(false, false);
		VHWSentence vhw = getVHW(0, 5);
		MWVSentence mwv_r = getMWV(45, 5 * Math.sqrt(2), false);
		MWVSentence mwv_t = getMWV(90, 5, true);
		MWDSentence mwd = getMWD(90, 5);
		
		long time = 1000;
		ws.onSentence(vhw, time + 50);
		ws.onSentence(mwv_r, time + 100);
		ws.onSentence(mwv_t, time + 150);
		ws.onSentence(mwd, time + 200);

		time += 1000;
		ws.onSentence(vhw, time + 50);
		ws.onSentence(mwv_r, time + 100);
		ws.onSentence(mwv_t, time + 150);
		ws.onSentence(mwd, time + 200);
	
		Iterator<Event<Sentence>> i = ws.getOutput().iterator();
		check(i.next(), vhw, 1050);
		check(i.next(), mwv_r, 1100);
		check(i.next(), mwv_t, 1150);
		check(i.next(), mwd, 1200);
		check(i.next(), vhw, 2050);
		check(i.next(), mwv_r, 2100);
		check(i.next(), mwv_t, 2150);
		check(i.next(), mwd, 2200);
	
	
	}
	
	private static void check(Event<Sentence> e, Sentence s, long t) {
		assertEquals(t, e.timestamp);
		assertEquals(s, e.event); 
	}
}
