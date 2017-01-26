package com.aboni.misc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SpeedMovingAverage implements MovingAverage {

	private long period = 60 * 1000;
	private double mAvg = Double.NaN;
	private List<Sample> samples = new LinkedList<Sample>();

	public SpeedMovingAverage() {
	}
	
	public SpeedMovingAverage(long period) {
		this.period = period;
	}
	
	@Override
	public void setPeriod(long period) {
		this.period = period;
	}

	@Override
	public long getPeriod() {
		return period;
	}

	@Override
	public void setSample(long ts, double value) {
		synchronized (this) {
			Iterator<Sample> it = samples.iterator();
			while (it.hasNext()) {
				Sample s = it.next();
				if (s.getAge(ts)>period) {
					double V = ( mAvg * samples.size() - value );
					it.remove();
					if (samples.size()>0) {
						mAvg = V / samples.size();
					} else {
						mAvg = Double.NaN;
					}
				} else {
					break;
				}
			}
	
			samples.add(new Sample(ts, value));
			if (Double.isNaN(mAvg)) {
				mAvg = value;
			} else {
				mAvg = ( mAvg * (samples.size()-1) + value ) / samples.size();
			}
		}
	}

	@Override
	public double getAvg() {
		synchronized (this) {
			return mAvg;
		}
	}

}
