/*
 * Copyright (c) 2022,  Andrea Boni
 * This file is part of NMEARouter.
 * NMEARouter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * NMEARouter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with NMEARouter.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aboni.utils;

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
    public double setSample(long ts, double angle) {
        synchronized (this) {
            setTime(ts);

            double a = Utils.normalizeDegrees0To360(angle);
            samples.add(new Sample(ts, a));
            if (Double.isNaN(mAvg)) {
                mAvg = a;
            } else {
                double a1 = Utils.getNormal(mAvg, a);
                mAvg = (mAvg * (samples.size() - 1) + a1) / samples.size();
                mAvg = Utils.normalizeDegrees0To360(mAvg);
            }
            return mAvg;
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
						mAvg = Utils.normalizeDegrees0To360(aA / samples.size());
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
