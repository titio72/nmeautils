package com.aboni.misc;

public class Sample {
	private	final double value;
	private	final long ts;
		
	public Sample(long ts, double value) {
		this.value = value;
		this.ts = ts;
	}

	public double getValue() {
		return value;
	}

	public long getTs() {
		return ts;
	}

	public long getAge(long now) {
		return now - ts;
	}
}
