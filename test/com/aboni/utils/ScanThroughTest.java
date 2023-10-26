package com.aboni.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;

public class ScanThroughTest {

    @Test
    public void testCountOddEven() {
        Collection<Integer> c = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        ScanThrough<Integer, Integer, Integer> s = new ScanThrough<>(i->(i%2), k->0, (i, k, a)->a+1);
        s.process(c);
        for (Map.Entry<Integer, Integer> e: s.getResults().entrySet()) {
            System.out.printf("%d->%d%n", e.getKey(), e.getValue());
        }
    }
}