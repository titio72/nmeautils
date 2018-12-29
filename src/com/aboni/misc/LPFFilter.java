package com.aboni.misc;

@SuppressWarnings("unused")
public class LPFFilter {

	private LPFFilter() {
	}

    public static double getLPFReading(double alpha, double prevOutput, double input) {
        return prevOutput + alpha * (input - prevOutput);
    }

    public static double getLPFReading(double alpha, double prevOutput, long tsPrev, double input, long ts) {
        return prevOutput + alpha * (input - prevOutput) * ((double)(ts-tsPrev)/1000.0);
    }
}
