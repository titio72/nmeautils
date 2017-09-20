package com.aboni.nmea.sentences;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.sf.marineapi.nmea.parser.SentenceFactory;

public class STALKSentenceTest {

	@Before
	public void setUp() throws Exception {
		NMEAUtils.registerExtraSentences();
	}

	
	
	@Test
	public void testCreateFromString() {
		String nmea = "$STALK,53,20,21";
		STALKSentence s = (STALKSentence) SentenceFactory.getInstance().createParser(nmea);
		System.out.println(s);
	}

}
