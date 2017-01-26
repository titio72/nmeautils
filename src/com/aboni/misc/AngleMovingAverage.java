package com.aboni.misc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AngleMovingAverage implements MovingAverage {

	private List<Sample> samples;
	private double mAvg = Double.NaN; 
	
	private long period = 60 * 1000;
	
	public AngleMovingAverage() {
		samples = new LinkedList<Sample>();
	}
	
	public AngleMovingAverage(long period) {
		samples = new LinkedList<Sample>();
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
			Iterator<Sample> it = samples.iterator();
			while (it.hasNext()) {
				Sample s = it.next();
				if (s.getAge(ts)>period) {
					double a = Utils.getNormal(mAvg, s.getValue());
					double A = ( mAvg * samples.size() - a );
					it.remove();
					if (samples.size()>0) {
						mAvg = Utils.normalizeDegrees0_360(A / samples.size());
					} else {
						mAvg = Double.NaN;
					}
				} else {
					break;
				}
			}
	
			double a = Utils.normalizeDegrees0_360(angle);
			samples.add(new Sample(ts, a));
			if (Double.isNaN(mAvg)) {
				mAvg = a;
			} else {
				double a1 = Utils.getNormal(mAvg, a);
				//System.out.format("%d %5.1f %5.1f %5.1f%n", ts, angle, mAvg, a1);
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
}
