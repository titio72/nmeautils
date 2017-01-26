package com.aboni.misc;

public class Sample {
	private	double value;
	private	long ts;
		
	public Sample(long ts, double angle) {
		this.setValue(angle);
		this.setTs(ts);
	}

	public double getValue() {
		return value;
	}

	private void setValue(double value) {
		this.value = value;
	}

	public long getTs() {
		return ts;
	}

	private void setTs(long ts) {
		this.ts = ts;
	}
	
	public long getAge(long now) {
		return now - ts;
	}
}
