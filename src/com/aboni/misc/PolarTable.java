package com.aboni.misc;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public interface PolarTable {
    int getMaxWindSpeed();

    float getMaxSpeed(float tws);

    float getSpeed(int twd, float tws);

    void load(Reader r) throws IOException;

    void dump(Writer w) throws IOException;

    int getBeatAngle();
}
