package com.aboni.misc;

public class LPFFilter {

	private LPFFilter() {
	}

    public static double getLPFReading(double alpha, double prevOutput, double input) {
        double newOutput = prevOutput + alpha * (input - prevOutput);
        return newOutput;
    }

    public static double getLPFReading(double alpha, double prevOutput, long tsPrev, double input, long ts) {
        double newOutput = prevOutput + alpha * (input - prevOutput) * ((double)(ts-tsPrev)/1000.0);
        return newOutput;
    }
}
