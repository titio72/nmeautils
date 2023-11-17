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

package com.aboni.data;

@SuppressWarnings("unused")
public class LPFFilter {

    private LPFFilter() {
    }

    public static double getLPFReading(double alpha, double prevOutput, double input) {
        if (Double.isNaN(prevOutput)) return input;
        return prevOutput + alpha * (input - prevOutput);
    }

    public static double getLPFReading(double alpha, double prevOutput, long tsPrev, double input, long ts) {
        if (Double.isNaN(prevOutput)) return input;
        return prevOutput + alpha * (input - prevOutput) * ((ts - tsPrev) / 1000.0);
    }
}
