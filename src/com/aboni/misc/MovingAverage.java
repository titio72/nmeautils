package com.aboni.misc;

public interface MovingAverage {

	void setPeriod(long period);

	long getPeriod();

	void setSample(long ts, double angle);

	double getAvg();

}