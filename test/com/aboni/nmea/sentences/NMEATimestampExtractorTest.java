package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.Sentence;
import org.junit.Test;

import java.time.OffsetDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NMEATimestampExtractorTest {

    @Test
    public void extractTimestampRMC() throws NMEATimestampExtractor.GPSTimeException {
        String s = "$IIRMC,133754.00,A,5046.305,N,00132.959,W,5.30,107.3,151118,0.9,W,A";
        Sentence sentence = SentenceFactory.getInstance().createParser(s);
        OffsetDateTime dt = NMEATimestampExtractor.extractTimestamp(sentence);
        assertEquals(OffsetDateTime.parse("2018-11-15T13:37:54Z"), dt);
    }

    @Test
    public void extractTimestampZDA() throws NMEATimestampExtractor.GPSTimeException {
        String s = "$GPZDA,160012.71,11,03,2004,1,0";
        Sentence sentence = SentenceFactory.getInstance().createParser(s);
        OffsetDateTime dt = NMEATimestampExtractor.extractTimestamp(sentence);
        assertEquals(OffsetDateTime.parse("2004-03-11T16:00:12+01:00"), dt);
    }

    @Test
    public void extractTimestampNoTimeInfo() throws NMEATimestampExtractor.GPSTimeException {
        String s = "$WIMWV,301.2,R,11.7,N,A";
        Sentence sentence = SentenceFactory.getInstance().createParser(s);
        OffsetDateTime dt = NMEATimestampExtractor.extractTimestamp(sentence);
        assertNull(dt);
    }

    @Test(expected = NMEATimestampExtractor.GPSTimeException.class)
    public void extractMalformedTimestamp() throws NMEATimestampExtractor.GPSTimeException {
        // the mistake is the month 13
        String s = "$IIRMC,133754.00,A,5046.305,N,00132.959,W,5.30,107.3,151318,0.9,W,A";
        Sentence sentence = SentenceFactory.getInstance().createParser(s);
        OffsetDateTime dt = NMEATimestampExtractor.extractTimestamp(sentence);
        assertEquals(OffsetDateTime.parse("2018-11-15T13:37:54Z"), dt);
    }
}