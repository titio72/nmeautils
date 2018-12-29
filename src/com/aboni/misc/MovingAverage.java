package com.aboni.misc;

@SuppressWarnings("unused")
public interface MovingAverage {

	void setPeriod(long period);

	long getPeriod();

	void setSample(long ts, double angle);

	double getAvg();

	double setTime(long ts);

}