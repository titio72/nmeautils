package com.aboni.misc;

/**
 * Calculate the moving average over a time window.
 * Usage:
 * Set the desired period (the size of the time window) in milliseconds.
 * Add the samples (pairs of time and value) incrementally.
 * If no samples are available the shift of the window can be triggered independently (@see setTime)
 */
public interface MovingAverage {

    void setPeriod(long period);

    long getPeriod();

    double setTime(long ts);

    double setSample(long ts, double value);

    double getAvg();
}