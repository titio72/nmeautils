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

import com.aboni.data.LPFFilter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LFPFilterTest {

    @Test
    public void getLPFReading() {
        assertEquals(1, LPFFilter.getLPFReading(0.5, 1, 1), 0.0001);
        assertEquals(0.75, LPFFilter.getLPFReading(0.5, 0.5, 1), 0.0001);
        assertEquals(1, LPFFilter.getLPFReading(1.0, 0.5, 1), 0.0001);
        assertEquals(0.5, LPFFilter.getLPFReading(0.0, 0.5, 1), 0.0001);
    }
}