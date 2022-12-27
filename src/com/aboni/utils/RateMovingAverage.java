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

public class RateMovingAverage implements MovingAverage {

    private long period = 60L * 1000L;
    private double mAvg = Double.NaN;
    private final List<Sample> samples = new LinkedList<>();

	public RateMovingAverage() {
	}
	
	public RateMovingAverage(long period) {
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
    public double setSample(long ts, double value) {
        synchronized (this) {
            setTime(ts);
            samples.add(new Sample(ts, value));
            if (Double.isNaN(mAvg)) {
                mAvg = value;
            } else {
                mAvg += value;
            }
            return mAvg;
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
					mAvg -= s.getValue();
					it.remove();
					if (samples.isEmpty()) {
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
