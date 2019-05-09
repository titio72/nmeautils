package com.aboni.misc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AngleMovingAverage implements MovingAverage {

	private final List<Sample> samples;
	private double mAvg = Double.NaN; 
	
	private long period = 60L * 1000L;
	
	public AngleMovingAverage() {
		samples = new LinkedList<>();
	}
	
	public AngleMovingAverage(long period) {
		samples = new LinkedList<>();
		this.period = period;
	}
	
	/* (non-Javadoc)
	 * @see com.aboni.misc.MovingAverage#setPeriod(long)
	 */
	@Override
	public void setPeriod(long period) {
		this.period = period; 
	}
	
	/* (non-Javadoc)
	 * @see com.aboni.misc.MovingAverage#getPeriod()
	 */
	@Override
	public long getPeriod() {
		return period;
	}
	
	
	/* (non-Javadoc)
	 * @see com.aboni.misc.MovingAverage#setSample(long, double)
	 */
	@Override
	public void setSample(long ts, double angle) {
		synchronized (this) {
			setTime(ts);
	
			double a = Utils.normalizeDegrees0_360(angle);
			samples.add(new Sample(ts, a));
			if (Double.isNaN(mAvg)) {
				mAvg = a;
			} else {
				double a1 = Utils.getNormal(mAvg, a);
				mAvg = ( mAvg * (samples.size()-1) + a1 ) / samples.size();
				mAvg = Utils.normalizeDegrees0_360(mAvg);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.aboni.misc.MovingAverage#getAvg()
	 */
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
					double a = Utils.getNormal(mAvg, s.getValue());
					double aA = ( mAvg * samples.size() - a );
					it.remove();
					if (!samples.isEmpty()) {
						mAvg = Utils.normalizeDegrees0_360(aA / samples.size());
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
