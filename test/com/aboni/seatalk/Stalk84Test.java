package com.aboni.seatalk;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Before;
import org.junit.Test;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.STALKSentence;

public class Stalk84Test {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws IOException {
		STALKSentence s = (STALKSentence)SentenceFactory.getInstance().createParser("$STALK,84,06,C4,B0,42,00,F5,02,06*13");
		Stalk84 ss = new Stalk84(s);
		ss.dump(System.out);
	}

}
