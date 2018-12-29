package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.sentence.Sentence;

public interface NMEASentenceFilter {
	
	boolean match(Sentence s, String src);
	
}
