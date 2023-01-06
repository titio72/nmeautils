package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class NMEAUtilsTest {

	@Before
	public void setUp() {
	}

	@Test
	public void testTimestampFromRMC() {
		RMCSentence s = (RMCSentence) SentenceFactory.getInstance().createParser("$GPRMC,124947,A,5907.4700,N,01014.1000,E,0.1000,358.000,160517,,*26");
		Calendar c = NMEAUtils.getTimestamp(s);

		Calendar cc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cc.set(Calendar.YEAR, 2017);
		cc.set(Calendar.MONTH, Calendar.MAY);
		cc.set(Calendar.DAY_OF_MONTH, 16);
		cc.set(Calendar.HOUR_OF_DAY, 12);
		cc.set(Calendar.MINUTE, 49);
		cc.set(Calendar.SECOND, 47);
		cc.set(Calendar.MILLISECOND, 0);

		assertNotNull(c);
		assertEquals(cc.getTimeInMillis(), c.getTimeInMillis());
	}

	@Test
	public void testTimestampOptimisticFromRMC() {
		RMCSentence s = (RMCSentence) SentenceFactory.getInstance().createParser("$GPRMC,,A,5907.4700,N,01014.1000,E,0.1000,358.000,160517,,");
		Calendar c = NMEAUtils.getTimestampOptimistic(s);
		System.out.println((System.currentTimeMillis() - c.getTimeInMillis()));
		assertTrue((System.currentTimeMillis() - c.getTimeInMillis())<10);
	}
}
