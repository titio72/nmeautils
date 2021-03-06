package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.util.Position;

@SuppressWarnings("unused")
public interface XMCSentence extends Sentence {

	Position getMedianPosition();
	void setMedianPosition(Position p);
	
	Position getAveragePosition();
	void setAveragePosition(Position p);
	void setAnchor(boolean b);
	boolean isAnchor();
	
}
