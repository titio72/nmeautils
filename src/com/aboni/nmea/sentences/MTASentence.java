package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.sentence.Sentence;

public interface MTASentence extends Sentence {

	double getTemperature();

	void setTemperature(double t);

}