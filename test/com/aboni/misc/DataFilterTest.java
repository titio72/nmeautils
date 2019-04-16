package com.aboni.misc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataFilterTest {

    @Test
    public void getLPFReading() {
        assertEquals(1, DataFilter.getLPFReading(0.5, 1, 1), 0.0001);
        assertEquals(0.75, DataFilter.getLPFReading(0.5, 0.5, 1), 0.0001);
        assertEquals(1, DataFilter.getLPFReading(1.0, 0.5, 1), 0.0001);
        assertEquals(0.5, DataFilter.getLPFReading(0.0, 0.5, 1), 0.0001);
    }
}