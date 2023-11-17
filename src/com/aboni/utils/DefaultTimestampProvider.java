package com.aboni.utils;

public class DefaultTimestampProvider extends TimestampProvider {

    @Override
    public long getNow() {
        return System.currentTimeMillis();
    }
}
