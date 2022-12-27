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
