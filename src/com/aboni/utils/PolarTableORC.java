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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class PolarTableORC implements PolarTable {
    /*
twa/tws;6;8;10;12;14;16;20
0;0;0;0;0;0;0;0
43.5;4.62;0;0;0;0;0;0
41.6;0;5.43;0;0;0;0;0
41.4;0;0;6.19;0;0;0;0
40.5;0;0;0;6.68;0;0;0
39.6;0;0;0;0;6.85;0;0
39.2;0;0;0;0;0;6.92;0
39.5;0;0;0;0;0;0;7.01
52;5.17;6.15;6.93;7.36;7.56;7.64;7.68
60;5.5;6.49;7.19;7.56;7.76;7.87;7.93
75;5.75;6.78;7.39;7.73;7.97;8.16;8.38
90;5.77;6.94;7.59;7.94;8.12;8.29;8.77
110;5.77;6.99;7.66;8.06;8.44;8.83;9.27
120;5.58;6.79;7.54;7.96;8.36;8.8;9.66
135;4.99;6.14;7.1;7.67;8.05;8.45;9.32
150;4.21;5.3;6.26;7.09;7.63;8;8.76
144;4.5;0;0;0;0;0;0
147.9;0;5.42;0;0;0;0;0
151.4;0;0;6.17;0;0;0;0
154.8;0;0;0;6.85;0;0;0
168.8;0;0;0;0;6.99;0;0
180;0;0;0;0;0;7.44;0
180;0;0;0;0;0;0;8.17
     */
    private final List<Double> winds = new ArrayList<>();
    private final List<Integer> standardAngles = new ArrayList<>();
    private double[][] speeds;

    public PolarTableORC() {
        standardAngles.add(0);
        standardAngles.add(520);
        standardAngles.add(600);
        standardAngles.add(750);
        standardAngles.add(900);
        standardAngles.add(1100);
        standardAngles.add(1200);
        standardAngles.add(1350);
        standardAngles.add(1500);
    }

    @Override
    public int getMaxWindSpeed() {
        return (int) getWind(Integer.MAX_VALUE);
    }

    @Override
    public float getMaxSpeed(float tws) {
        int iW = getWindIndex(tws);
        iW = Math.min(iW + 1, winds.size() - 1);

        double res = 0.0;
        for (int i = 0; i < standardAngles.size(); i++) {
            res = Math.max(speeds[i][iW], res);
        }
        return (float) res;
    }

    @Override
    public float getSpeed(int twd, float tws) {
        return (float) interpolate(tws, twd);
    }

    @Override
    public void load(Reader r) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(r)) {
            String head = bufferedReader.readLine();
            if (head.startsWith("twa/tws;")) {
                readHead(head);
            }
            speeds = new double[standardAngles.size()][winds.size()];
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, ";");
                if (tokenizer.hasMoreTokens()) {
                    int angle = (int) Math.round(Double.parseDouble(tokenizer.nextToken()) * 10);
                    int row = insertionPoint(angle);
                    if (row != -1) {
                        int col = 0;
                        while (tokenizer.hasMoreTokens()) {
                            double speed = Double.parseDouble(tokenizer.nextToken());
                            speeds[row][col] = speed;
                            col++;
                        }
                    }
                }
            }
        }
    }

    private int getAngleIndex(double angle) {
        int i = 0;
        int iAngle = (int) Math.round(angle * 10);
        for (int a : standardAngles) {
            if (a > iAngle) return i - 1;
            i++;
        }
        return i - 1;
    }

    private int getWindIndex(double wind) {
        int i = 0;
        for (double w : winds) {
            if (w > wind) return i - 1;
            i++;
        }
        return i - 1;
    }

    private double getAngle(int ix) {
        ix = Math.min(ix, standardAngles.size() - 1);
        return standardAngles.get(ix) / 10.0;
    }

    private double getWind(int ix) {
        if (ix<0) return 0;
        ix = Math.min(ix, winds.size() - 1);
        return winds.get(ix);
    }

    private int insertionPoint(int angle) {
        return standardAngles.indexOf(angle);
    }

    private static final DecimalFormat fmt = new DecimalFormat("000.0");

    private String formatAngle(double d) {
        return fmt.format(d);
    }

    private void readHead(String head) {
        winds.clear();
        StringTokenizer tok = new StringTokenizer(head, ";");
        String token = tok.nextToken(); // skip the first
        while (tok.hasMoreTokens()) {
            token = tok.nextToken();
            winds.add(Double.parseDouble(token));
        }
    }

    @Override
    public void dump(Writer w) throws IOException {
    }

    @Override
    public int getBeatAngle() {
        return 0;
    }

    private static double interpolate(double p1, double v1, double p2, double v2, double p) {
        return (p - p1) / (p2 - p1) * (v2 - v1) + v1;
    }

    private double interpolate(double wind, double angle) {
        int iW1 = getWindIndex(wind);
        int iA2 = getAngleIndex(angle);
        double a1 = getAngle(iA2);
        double w1 = getWind(iW1);
        double a2 = getAngle(iA2 + 1);
        double w2 = getWind(iW1 + 1);
        double s11 = (iW1<0)?0:speeds[iA2][iW1];
        double s22 = speeds[iA2 >= standardAngles.size() ? iA2 : (iA2 + 1)][iW1 >= (winds.size()-1) ? iW1 : (iW1 + 1)];
        double x12 = Math.sqrt(Math.pow(a2 - a1, 2) + Math.pow(w2 - w1, 2));
        double x = Math.sqrt(Math.pow(angle - a1, 2) + Math.pow(Math.min(wind, w1) - w1, 2));
        return interpolate(0, s11, x12, s22, x);
    }
}
