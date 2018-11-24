package com.aboni.nmea.sentences;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.Test;

import net.sf.marineapi.nmea.parser.SentenceFactory;

public class NMEA2JSONbTest {

	@Test
	public void testRMC() {
		String s = "$IIRMC,133754.00,A,5046.305,N,00132.959,W,5.30,107.3,151118,0.9,W,A";
		JSONObject j = new NMEA2JSONb().convert(SentenceFactory.getInstance().createParser(s));
		assertNotNull(j);
		assertEquals("RMC", j.get("topic"));
		assertEquals("050 46.305 N", j.get("latitude"));
		assertEquals("001 32.959 W", j.get("longitude"));
		assertEquals(50.77175, j.getDouble("dec_latitude"), 0.00001);
		assertEquals(-1.54932, j.getDouble("dec_longitude"), 0.00001);
		assertEquals(107.3, j.getDouble("COG"), 0.00001);
		assertEquals(5.3, j.getDouble("SOG"), 0.00001);
		assertEquals("2018-11-15T13:37:54+0000", j.get("UTC"));
	}

}
