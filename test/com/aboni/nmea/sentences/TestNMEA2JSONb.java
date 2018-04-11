package com.aboni.nmea.sentences;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.RMCSentence;

public class TestNMEA2JSONb {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testRMC() {
		//yyyy-MM-dd'T'HH:mm:ssZ
		//$GNRMC,042109.00,A,4337.80741,N,01017.60146,E,0.037,,100418,,,D*66

		String nmea = "$GNRMC,052357.00,A,4337.80507,N,01017.60207,E,0.050,,090418,,,D*61";
		RMCSentence s = (RMCSentence) SentenceFactory.getInstance().createParser(nmea);
		JSONObject o = new NMEA2JSONb().convert(s);
		assertEquals("2018-04-09T05:23:57+0000", o.getString("UTC"));
		
	}

	@Test
	public void testRMC_ublox() {
		//yyyy-MM-dd'T'HH:mm:ssZ
		String nmea = "$GNRMC,042109.00,A,4337.80741,N,01017.60146,E,0.037,,100418,,,D*66";
		RMCSentence s = (RMCSentence) SentenceFactory.getInstance().createParser(nmea);
		JSONObject o = new NMEA2JSONb().convert(s);
		assertEquals("2018-04-10T04:21:09+0000", o.getString("UTC"));
		
	}

}
