package com.aboni.nmea.sentences;

public interface STALKSentence {

	String getCommand();

	void setCommand(String cmd);

	int[] getParams();

	void setParams(int[] params);

}