package com.aboni.nmea.sentences;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.util.Date;
import net.sf.marineapi.nmea.util.Time;

public class NMEAUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testTimestampFromNMEA() {
		Time t = new Time(16, 30, 0.0);
		Date d = new Date(2017, 5, 17);
		Calendar c = NMEAUtils.getTimestamp(t, d);
		
	    Calendar cc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	    cc.set(Calendar.YEAR, 2017);
	    cc.set(Calendar.MONTH, Calendar.MAY);
	    cc.set(Calendar.DAY_OF_MONTH, 17);
	    cc.set(Calendar.HOUR_OF_DAY, 16);
	    cc.set(Calendar.MINUTE, 30);
	    cc.set(Calendar.SECOND, 0);
	    cc.set(Calendar.MILLISECOND, 0);
	    
	    assertEquals(cc.getTimeInMillis(), c.getTimeInMillis());
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
	    
	    assertEquals(cc.getTimeInMillis(), c.getTimeInMillis());
	}
}
