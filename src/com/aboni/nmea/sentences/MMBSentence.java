package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.sentence.Sentence;

public interface MMBSentence extends Sentence {

	double getPresBar();

	void setPresBar(double presBar);

	double getPresIHG();

	void setPresIHG(double presIHG);

}