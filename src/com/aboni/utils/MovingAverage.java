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

/**
 * Calculate the moving average over a time window.
 * Usage:
 * Set the desired period (the size of the time window) in milliseconds.
 * Add the samples (pairs of time and value) incrementally.
 * If no samples are available the shift of the window can be triggered independently (@see setTime)
 */
public interface MovingAverage {

    void setPeriod(long period);

    long getPeriod();

    double setTime(long ts);

    double setSample(long ts, double value);

    double getAvg();
}