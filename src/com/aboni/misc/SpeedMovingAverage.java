package com.aboni.misc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SpeedMovingAverage implements MovingAverage {

	private long period = 60L * 1000L;
	private double mAvg = Double.NaN;
	private final List<Sample> samples = new LinkedList<>();

	public SpeedMovingAverage() {
		// nothing to do here
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
			setTime(ts);
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

	@Override
	public double setTime(long ts) {
		synchronized (this) {
			Iterator<Sample> it = samples.iterator();
			while (it.hasNext()) {
				Sample s = it.next();
				if (s.getAge(ts)>period) {
					double v = ( mAvg * samples.size() - s.getValue() );
					it.remove();
					if (!samples.isEmpty()) {
						mAvg = v / samples.size();
					} else {
						mAvg = Double.NaN;
					}
				} else {
					break;
				}
			}
			return mAvg;
		}
				
	}

}
