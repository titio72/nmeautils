package com.aboni.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    private final DateFormat df;

    public LogFormatter() {
        df = new SimpleDateFormat("HH:mm:ss.SSS");
    }

    @Override
    public String format(LogRecord payload) {
        Date d = new Date(payload.getMillis());
        String s = df.format(d) + " " + payload.getLevel() + " " + payload.getMessage() + "\n";
        if (payload.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            payload.getThrown().printStackTrace(pw);
            s += sw + "\n";
        }
        return s;
    }
}
