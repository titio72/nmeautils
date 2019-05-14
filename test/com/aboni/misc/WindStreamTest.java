package com.aboni.misc;

import com.aboni.nmea.sentences.NMEASentenceFilter;
import com.aboni.nmea.sentences.VWRSentence;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.*;
import net.sf.marineapi.nmea.util.Side;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class WindStreamTest {

	private static class WS extends WindStream {

		List<Event<Sentence>> output = new ArrayList<>();
		
		WS(boolean useVWR, boolean forceCalcTrue) {
			// use P to distinguish internally generated events
			super(TalkerId.P, getConf(useVWR, forceCalcTrue));
		}

		static WindStream.Conf getConf(boolean useVWR, boolean forceCalcTrue) {
			WindStream.Conf c = new Conf();
			c.setUseVWR(useVWR);
			c.setForceCalcTrue(forceCalcTrue);
			c.setSkipFirstCalculation(false);
			return c;
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
			for (Event<Sentence> s: output) System.out.println(s.ev);
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
				if (e!=null && e.ev!=null && type.equals(e.ev.getSentenceId())) {
					if (filter==null || filter.match(e.ev, null)) {
						if (count == 0) return e.ev;
						else count--;
					}
				}
			}
			return null;
		}

		/**
		 * All the occurrences of a type of sentences.
		 * @param type The type of the senteces to find.
		 * @param filter Additional optional filter.
		 * @return A collection of all the sentences of the ghiven type.
		 */
		Collection<Sentence> findAll(final String type, NMEASentenceFilter filter) {
			List<Sentence> res = new ArrayList<>();
			for (Event<Sentence> e: output) {
				if (e!=null && e.ev!=null && type.equals(e.ev.getSentenceId())) {
					if (filter==null || filter.match(e.ev, null)) {
						res.add(e.ev);
					}
				}
			}
			return res;
		}

		MWVSentence sendMWV(boolean trueWind, double speed, double angle, long time) {
			MWVSentence s = (MWVSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.MWV);
			s.setAngle(angle);
			s.setSpeed(speed);
			s.setTrue(trueWind);
			this.onSentence(s, time);
			return s;
		}

		MWDSentence sendMWD(double speed, double angle, long time) {
			MWDSentence s = (MWDSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.MWD);
			s.setMagneticWindDirection(angle);
			s.setWindSpeedKnots(speed);
			this.onSentence(s, time);
			return s;
		}

		VHWSentence sendVHW(double speed, double angle, long time) {
			VHWSentence s = (VHWSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.VHW);
			//s.setHeading(angle);
			s.setMagneticHeading(angle);
			s.setSpeedKnots(speed);
			this.onSentence(s, time);
			return s;
		}

		HDGSentence sendHDG(double heading, long time) {
			HDGSentence s = (HDGSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.HDG);
			//s.setDeviation(0.0);
			//s.setVariation(0.0);
			s.setHeading(heading);
			this.onSentence(s, time);
			return s;
		}

		VWRSentence sendVWR(double speed, double angle, long time) {
			VWRSentence s = (VWRSentence)SentenceFactory.getInstance().createParser(TalkerId.II, SentenceId.VWR);
			angle = Utils.normalizeDegrees180To180(angle);
			s.setAngle(Math.abs(angle));
			s.setSide(angle<0?Side.PORT:Side.STARBOARD);
			s.setSpeed(speed);
			this.onSentence(s, time);
			return s;
		}

	}

	private long t0;

	@Before
	public void setUp() {
		t0 = (System.currentTimeMillis()/86400)*86400;
	}

	private long incT(long step) {
		t0 += step;
		return t0;
	}

	@Test
	public void test_CalcMWVT_MWD() {
		// MWV_R & VHW are given annd valid
		// MWV_T & MWD are not give

		WS ws = new WS(false, false);

		ws.sendVHW(5.0, 0.0, incT(50));
		ws.sendMWV(false, 5 * Math.sqrt(2), 45.0, incT(100));
		ws.dump();

		MWVSentence mwv_t = (MWVSentence)ws.find("MWV", 0, (Sentence s, String src)-> ((MWVSentence)s).isTrue());
		assertNotNull(mwv_t);
		assertEquals(TalkerId.P, mwv_t.getTalkerId());
		assertEquals(5.0, mwv_t.getSpeed(),0.05 /* allow 0.05 knots of difference*/ );
		assertEquals(90.0, mwv_t.getAngle(), 0.5 /* allow 0.5 degree for rounding issues*/);

		MWDSentence mwd = (MWDSentence)ws.find("MWD", 0, null);
		assertEquals(TalkerId.P, mwd.getTalkerId());
		assertEquals(5, mwd.getWindSpeedKnots(), 0.05);
		assertEquals(90, mwd.getMagneticWindDirection(), 0.5);

		//check only one calc-ed sentence is produced
		assertNull(ws.find("MWD", 1, null));
		assertNull(ws.find("MWV", 1, (Sentence s, String src)-> ((MWVSentence)s).isTrue()));
	}

	@Test
	public void test_CalcMWVT_MWD_when_MWVT_Is_Too_Old() {
		// MWV_R & VHW are given annd valid
		// MWV_T is given but old

		WS ws = new WS(false, false);

		// send the "old" MWV_T and reset the output
		ws.sendMWV(true, 5.0, 0.0, incT(50));
		ws.reset();

		ws.sendVHW(5.0, 0.0, incT(10050) /*add 10 seconds*/);
		ws.sendMWV(false, 5 * Math.sqrt(2), 45.0, incT(100));
		ws.dump();

		MWVSentence mwv_t = (MWVSentence)ws.find("MWV", 0, (Sentence s, String src)-> ((MWVSentence)s).isTrue());
		assertNotNull(mwv_t);
		assertEquals(TalkerId.P, mwv_t.getTalkerId());
		assertEquals(5.0, mwv_t.getSpeed(),0.05 /* allow 0.05 knots of difference*/ );
		assertEquals(90.0, mwv_t.getAngle(), 0.5 /* allow 0.5 degree for rounding issues*/);

		MWDSentence mwd = (MWDSentence)ws.find("MWD", 0, null);
		assertEquals(TalkerId.P, mwd.getTalkerId());
		assertEquals(5, mwd.getWindSpeedKnots(), 0.05);
		assertEquals(90, mwd.getMagneticWindDirection(), 0.5);

		//check only one calc-ed sentence is produced
		assertNull(ws.find("MWD", 1, null));
		assertNull(ws.find("MWV", 1, (Sentence s, String src)-> ((MWVSentence)s).isTrue()));
	}

	@Test
	public void test_Do_Not_Calc_MWVT_When_Given() {
		// MWV_R, MWV_T & VHW are given annd valid

		WS ws = new WS(false, false);
		ws.sendVHW(5.0, 0.0, incT(100));
		ws.sendMWV(true,5.0, 90.0, incT(100));
		ws.sendMWV(false,5.0 * Math.sqrt(2), 45.0, incT(50));

		MWVSentence mwv_t = (MWVSentence)ws.find("MWV", 0, (Sentence s, String src)->((MWVSentence)s).isTrue());
		assertNotNull(mwv_t);
		assertEquals(TalkerId.II, mwv_t.getTalkerId());

		assertNull(ws.find("MWV", 1, (Sentence s, String src)->((MWVSentence)s).isTrue()));
	}

	@Test
	public void test_Calc_MWD_When_MWVT_Is_Given() {
		// MWV_R, MWV_T & VHW are given annd valid
		// MWD is not given

		WS ws = new WS(false, false);
		ws.sendVHW(5.0, 0.0, incT(100));
		ws.sendMWV(true,5.0, 90.0, incT(100));

		MWDSentence mwd = (MWDSentence)ws.find("MWD", 0, null);
		assertNotNull(mwd);
		assertEquals(TalkerId.P, mwd.getTalkerId());
		assertEquals(5, mwd.getWindSpeedKnots(), 0.05);
		assertEquals(90, mwd.getMagneticWindDirection(), 0.5);
	}

	@Test
	public void test_Calc_MWD_When_MWVT_Is_Given_And_use_HDG() {
		// MWV_T, HDG are given annd valid
		// MWD is not given

		WS ws = new WS(false, false);
		ws.sendHDG(0.0,incT(100));
		ws.sendMWV(true,5.0, 90.0, incT(100));

		MWDSentence mwd = (MWDSentence)ws.find("MWD", 0, null);
		assertNotNull(mwd);
		assertEquals(TalkerId.P, mwd.getTalkerId());
		assertEquals(5, mwd.getWindSpeedKnots(), 0.05);
		assertEquals(90, mwd.getMagneticWindDirection(), 0.5);
	}

	@Test
	public void test_cannot_calc_MWVT_MWD_Missing_VHW() {
		// MWV_R
		// MWV_T & VHW are nbot given is not given

		WS ws = new WS(false, false);
		ws.sendHDG(0.0,incT(100)); // may be redundant
		ws.sendMWV(false,5.0 * Math.sqrt(2), 45.0, incT(100));

		assertNull(ws.find("MWD", 0, null));
		assertNull(ws.find("MWW", 0, (Sentence s, String src)->((MWVSentence)s).isTrue()));
	}
}
