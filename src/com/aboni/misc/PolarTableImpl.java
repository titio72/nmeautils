package com.aboni.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

public class PolarTableImpl implements PolarTable {

	/*
	angle	1	2	3	4	5	6	7	8	9	10	11	12	13	14	15	16	17	18	19	20
	52	0	0	0	0	0	4.96	0	6	0	6.6	0	6.93	0	7.12	0	7.23	0	0	0	7.33
	60	0	0	0	0	0	5.32	0	6.32	0	6.88	0	7.25	0	7.4	0	7.49	0	0	0	7.58
	75	0	0	0	0	0	5.68	0	6.58	0	7.18	0	7.48	0	7.71	0	7.84	0	0	0	7.99
	90	0	0	0	0	0	5.85	0	6.8	0	7.38	0	7.68	0	7.82	0	7.96	0	0	0	8.36
	110	0	0	0	0	0	5.65	0	6.63	0	7.24	0	7.62	0	7.99	0	8.34	0	0	0	8.69
	120	0	0	0	0	0	5.29	0	6.39	0	7.04	0	7.49	0	7.83	0	8.23	0	0	0	8.98
	135	0	0	0	0	0	4.62	0	5.77	0	6.58	0	7.15	0	7.53	0	7.86	0	0	0	8.62
	150	0	0	0	0	0	3.87	0	4.93	0	5.9	0	6.6	0	7.13	0	7.5	0	0	0	8.15
	 */

    private float[] maxSpeeds;
    private float[][] speeds;
    private int beatAngle = 180;
    private int maxWindSpeed = 20;


    public PolarTableImpl() {
        // no init required
    }

    @Override
    public int getMaxWindSpeed() {
        return maxWindSpeed;
    }

    @Override
    public float getMaxSpeed(float tws) {
        if (tws > maxWindSpeed)
            return getRawMaxSpeed(maxWindSpeed);
        else if (tws < 1)
            return getRawMaxSpeed(1);
        else {
            int s0 = (int) Math.floor(tws);
            int s1 = (int) Math.ceil(tws);
            if (s0 == s1)
                return getRawMaxSpeed(s0);
            else {
                float speed0 = getRawMaxSpeed(s0);
                float speed1 = getRawMaxSpeed(s1);
                return speed0 + (speed1 - speed0) * (tws - s0) / (s1 - s0);
            }
        }
    }

    private float getRawMaxSpeed(int tws) {
        if (maxSpeeds != null) {
            if (tws >= 1 && tws <= maxWindSpeed) {
                return maxSpeeds[tws - 1];
            }
            return -1;
        } else {
            return 0.0f;
        }
    }

    private float getRawSpeed(int twd, int tws) {
        if (speeds != null) {
            if (tws >= 1 && tws <= maxWindSpeed) {
                twd = twd % 360;
                if (twd > 180) twd = 360 - twd;
                return speeds[twd][tws - 1];
            }
            return -1;
        } else {
            return 0.0f;
        }
    }

    @Override
    public float getSpeed(int twd, float tws) {
        if (tws > maxWindSpeed)
            return getRawSpeed(twd, maxWindSpeed);
        else if (tws < 1)
            return getRawSpeed(twd, 1);
        else {
            int s0 = (int) Math.floor(tws);
            int s1 = (int) Math.ceil(tws);
            if (s0 == s1)
                return getRawSpeed(twd, s0);
            else {
                float speed0 = getRawSpeed(twd, s0);
                float speed1 = getRawSpeed(twd, s1);
                return speed0 + (speed1 - speed0) * (tws - s0) / (s1 - s0);
            }
        }
    }

    @Override
    public void load(Reader r) throws IOException {
        BufferedReader br = new BufferedReader(r);
        int[] windSpeeds;

        String line = br.readLine();
        while (line != null && !line.startsWith("angle")) {
            line = br.readLine();
        }

        if (line != null && line.startsWith("angle")) {
            windSpeeds = loadWindSpeeds(line, ",");
            maxWindSpeed = windSpeeds[windSpeeds.length - 1];
            speeds = new float[181][maxWindSpeed];
            maxSpeeds = new float[maxWindSpeed];
            int lastValidAngle = loadSpeeds(br, windSpeeds, ",");
            fillRunSpeeds(windSpeeds, lastValidAngle);
            interpolateColumns();
            interpolateRows();
            fillMaxSpeeds();
        }
    }

    public void loadAlternative(Reader r) throws IOException {
        BufferedReader br = new BufferedReader(r);
        int[] windSpeeds;

		/*
		twa/tws;6;8;10;12;14;16;20
		0;0;0;0;0;0;0;0
		43.5;4.62;0;0;0;0;0;0
		41.6;0;5.43;0;0;0;0;0
		41.4;0;0;6.19;0;0;0;0
		*/

        String line = br.readLine();
        while (line != null && !line.startsWith("twa/tws")) {
            line = br.readLine();
        }

        if (line != null && line.startsWith("twa/tws")) {
            windSpeeds = loadWindSpeeds(line, ";");
            maxWindSpeed = windSpeeds[windSpeeds.length - 1];
            speeds = new float[181][maxWindSpeed];
            maxSpeeds = new float[maxWindSpeed];
            int lastValidAngle = loadSpeeds(br, windSpeeds, ";");
            //fillRunSpeeds(windSpeeds, lastValidAngle);
            interpolateColumns();
            interpolateRows();
            fillMaxSpeeds();
        }
    }

    /**
     * If last valid angle is not 180 copy the last valid speeds to 180.
     * This is to have the value for the run.
     *
     * @param windSpeeds     The wind speeds.
     * @param lastValidAngle The last valid angle (the closest to 180).
     */
    private void fillRunSpeeds(int[] windSpeeds, int lastValidAngle) {
        if (lastValidAngle >= 0 && lastValidAngle != 180) {
            for (int wind_speed : windSpeeds) {
                setRawSpeed(180, wind_speed, getRawSpeed(lastValidAngle, wind_speed));
            }
        }
    }

    private int loadSpeeds(BufferedReader br, int[] windSpeeds, String separator) throws IOException {
        String line;
        String[] v;
        int lastValidAngle = -1;
        int firstValidAngle = -1;
        while ((line = br.readLine()) != null) {
            v = line.split(separator);
            int angle = Integer.parseInt(v[0]);
            for (int i = 1; i < v.length; i++) {
                try {
                    float speed = Float.parseFloat(v[i]);
                    setRawSpeed(angle, windSpeeds[i - 1], speed);
                    if (speed > 0) lastValidAngle = angle;
                    if (speed > 0 && firstValidAngle == -1) firstValidAngle = angle;
                } catch (NumberFormatException ignored) {
                    // skip the invalid speed
                }
            }
        }
        return lastValidAngle;
    }

    private int[] loadWindSpeeds(String line, String separator) {
        int[] windSpeeds;
        String[] v = line.split(separator);
        windSpeeds = new int[v.length - 1];
        for (int i = 1; i < v.length; i++) {
            windSpeeds[i - 1] = (int) Float.parseFloat(v[i]);
        }
        return windSpeeds;
    }

    private void setRawSpeed(int angle, int windSpeed, float v) {
        speeds[angle][windSpeed - 1] = v;
    }

    private void interpolateColumns() {
        for (int i = 0; i < maxWindSpeed; i++) {
            List<Float> angles = new LinkedList<>();
            List<Float> bestSpeeds = new LinkedList<>();
            for (int angle = 0; angle <= 180; angle++) {
                float speed = this.speeds[angle][i];
                if (speed != 0.0f) {
                    angles.add((float) angle);
                    bestSpeeds.add(speed);
                }
            }
            if (!angles.isEmpty()) {
                interpolate(angles, bestSpeeds, this.speeds, i);
            }
        }
    }

    private void interpolateRows() {
        for (int angle = 0; angle <= 180; angle++) {
            List<Float> ws = new LinkedList<>();
            List<Float> bs = new LinkedList<>();
            ws.add(0f);
            bs.add(0f);
            boolean notZero = false;
            for (int i = 0; i < maxWindSpeed; i++) {
                if (speeds[angle][i] > 0) {
                    notZero = true;
                    ws.add((float) i + 1);
                    bs.add(speeds[angle][i]);
                }
            }
            if (notZero) {
                beatAngle = Math.min(angle, beatAngle);
                interpolateRow(ws, bs, angle);
            }
        }
    }

    private void fillMaxSpeeds() {
        for (int i = 1; i <= maxWindSpeed; i++) {
            for (int angle = 0; angle <= 180; angle++) {
                maxSpeeds[i - 1] = Math.max(maxSpeeds[i - 1], getRawSpeed(angle, i));
            }
        }
    }

    private void interpolateRow(List<Float> ws, List<Float> bs, int angle) {

        SplineInterpolation spline = SplineInterpolation.createMonotoneCubicSpline(ws, bs);

        for (int wind = 1; wind <= maxWindSpeed; wind++) {
            setRawSpeed(angle, wind, spline.interpolate(wind));
        }
    }

    private void interpolate(List<Float> a, List<Float> s, float[][] ss, int col) {

        SplineInterpolation spline = SplineInterpolation.createMonotoneCubicSpline(a, s);

        int fangle = (int) a.get(0).floatValue();
        int langle = (int) a.get(a.size() - 1).floatValue();
        for (int angle = fangle; angle <= langle; angle++) {
            ss[angle][col] = spline.interpolate((float) angle);
        }
    }

    @Override
    public void dump(Writer w) throws IOException {
        if (speeds != null) {
            w.write("TWD/TWS");
            for (int i = 0; i < maxWindSpeed; i++) {
                w.write("," + (i + 1));
            }
            w.write("\n");

            for (int angle = 0; angle <= 180; angle++) {
                StringBuilder bf = new StringBuilder();
                bf.append(angle);
                boolean notZero = false;
                for (int i = 0; i < maxWindSpeed; i++) {
                    float speed = speeds[angle][i];
                    notZero = notZero || speed > 0f;
                    bf.append(",").append(String.format("%5.2f", speed));
                }
                if (notZero) {
                    w.write(bf.toString() + "\n");
                }
            }
        }
    }

    @Override
    public int getBeatAngle() {
        return beatAngle;
    }

}
