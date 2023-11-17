package com.aboni.log;

public class SafeLog {

    private SafeLog() {}

    public static Log getSafeLog(Log log) {
        if (log==null) return new NullLog();
        else return log;
    }
}
