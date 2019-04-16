package com.aboni.misc;

public class DataFilter {

	private DataFilter() {
	}

    public static double getLPFReading(double alpha, double prevOutput, double input) {
        return prevOutput + alpha * (input - prevOutput);
    }
}
