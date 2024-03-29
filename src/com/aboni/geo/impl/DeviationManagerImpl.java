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

/*
(C) 2020, Andrea Boni
This file is part of NMEARouter.
NMEARouter is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
NMEARouter is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with NMEARouter.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.aboni.geo.impl;

import com.aboni.geo.DeviationManager;
import com.aboni.utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeviationManagerImpl implements DeviationManager {

    public static final String DEVIATION_MANAGER_CATEGORY = "DeviationManager";

    static class Pair implements Comparable<Pair> {

        Pair() {
        }

        Pair(int r, double a) {
            input = r;
            output = a;
        }

        Pair(Pair p) {
            this(p.input, p.output);
        }

        int input;
        double output;

        @Override
        public int hashCode() {
            return input + (int) output;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Pair) {
                return (input == ((Pair) o).input);
            }
            return false;
        }

        @Override
        public int compareTo(Pair o) {
            return Integer.compare(input, o.input);
        }
    }

    private final List<Pair> deviationMap;
    private final List<Pair> reverseDeviationMap;

    public DeviationManagerImpl() {
        deviationMap = new ArrayList<>();
        reverseDeviationMap = new ArrayList<>();
    }

    /**
     * Load an existing deviation map.
     * Each line is a sample in the form compass,magnetic
     *
     * @param stream An InputStream for the deviation map.
     */
    @Override
    public boolean load(InputStream stream) {
        synchronized (this) {
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = r.readLine()) != null) {
                    String[] sample = line.split(",");
                    if (sample.length == 2) {
                        add(Integer.parseInt(sample[0]), Double.parseDouble(sample[1]));
                    }
                }
                r.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    /**
     * Dumps the deviation map (in the form of {@link #load(InputStream)})to the given output stream.
     *
     * @param stream The stream where to dump the deviation map to.
     * @throws IOException In case writing fails.
     */
    public void dump(OutputStream stream) throws IOException {
        synchronized (this) {
            Pair p;
            for (Pair pair : deviationMap) {
                p = pair;
                stream.write((p.input + "," + p.output + "\r\n").getBytes());
            }
        }
    }

    /**
     * Clear the deviation table.
     */
    public void reset() {
        synchronized (this) {
            deviationMap.clear();
            reverseDeviationMap.clear();
        }
    }

    public int size() {
        return deviationMap.size();
    }

    /**
     * Add a sample.
     *
     * @param reading  The compass reading in decimal degrees.
     * @param magnetic The magnetic reading (reading of a compensated compass).
     */
    public void add(double reading, double magnetic) {
        synchronized (this) {
            add(reading, magnetic, deviationMap);
            add(magnetic, reading, reverseDeviationMap);
        }
    }

    private static void add(double key, double value, List<Pair> l) {
        Pair sample = new Pair((int) normalize(key), normalize(value));
        int p = Collections.binarySearch(l, sample);
        if (p >= 0) {
            l.get(p).output = sample.output;
        } else {
            p = -(p + 1);
            l.add(p, sample);
        }
    }

    @Override
    public double getCompass(double magnetic) {
        synchronized (this) {
            return get(magnetic, reverseDeviationMap);
        }
    }

    @Override
    public double getMagnetic(double reading) {
        synchronized (this) {
            return get(reading, deviationMap);
        }
    }

    private static double get(double input, List<Pair> l) {
        input = normalize(input);

        double res = input;

        Pair sample = new Pair();
        sample.input = (int) input;
        sample.output = 0;

        int p = Collections.binarySearch(l, sample);
        if (p >= 0) {
            res = (input - (int) input) + l.get(p).output;
        } else if (l.size() > 1) {
            p = -(p + 1);
            Pair p0 = new Pair(l.get((p - 1) % l.size()));
            Pair p1 = new Pair(l.get(p % l.size()));

            double[] readings = spreadThem(p0.input, normalize(input), p1.input);
            double[] actual = spreadThem(p0.output, p1.output);

            double dSamples = readings[2] - readings[0];
            double dReading = readings[1] - readings[0];
            double dActual = actual[1] - actual[0];
            res = actual[0] + dActual * dReading / dSamples;
            if (res > 360.0) res -= 360;
        }

        return res;
    }

    private static double[] spreadThem(double low, double mid, double high) {
        if (low > mid) low = low - 360.0;
        if (high < mid) high = high + 360.0;

        return new double[]{low, mid, high};
    }

    private static double[] spreadThem(double low, double high) {
        if (low > high) low = low - 360.0;
        if (low < 0.0) {
            low += 360.0;
            high += 360.0;
        }

        return new double[]{low, high};
    }

    private static double normalize(double m) {
        return Utils.normalizeDegrees0To360(m);
    }
}
