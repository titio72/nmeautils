package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.ZDASentence;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Date;
import net.sf.marineapi.nmea.util.Time;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;

public class NMEATimestampExtractor {

	private NMEATimestampExtractor() {

	}

	private static class NMEADateAndTime {
		Time t = null;
		Date d = null;
	}

	public static class GPSTimeException extends Exception {
		public GPSTimeException(String msg) {
			super(msg);
		}

		private static final long serialVersionUID = 1L;
	}

	private static NMEADateAndTime extractInfo(Sentence s) throws GPSTimeException {
		Date d = null;
		Time t = null;
		try {
			if (s instanceof ZDASentence && s.isValid()) {
				ZDASentence zda = (ZDASentence) s;
				d = zda.getDate();
				t = zda.getTime();
			} else if (s instanceof RMCSentence && s.isValid()) {
				RMCSentence r = (RMCSentence) s;
				if (r.getStatus() == DataStatus.ACTIVE) {
					d = r.getDate();
					t = r.getTime();
				}
			}
		} catch (Exception e) {
            throw new GPSTimeException("Error extracting GPS time {" + s + "} e {" + e + "}");
		}
		if (d != null && t != null) {
			NMEADateAndTime tStamp = new NMEADateAndTime();
			tStamp.t = t;
			tStamp.d = d;
			return tStamp;
		} else {
			return null;
		}
	}

	private static OffsetDateTime convert(NMEADateAndTime dt) {
		int hh = dt.t.getOffsetHours();
		int hm = dt.t.getOffsetMinutes();
		return OffsetDateTime.of(
				dt.d.getYear(), dt.d.getMonth(), dt.d.getDay(),
				dt.t.getHour(), dt.t.getMinutes(), (int) dt.t.getSeconds(), 0,
				ZoneOffset.ofHoursMinutes(hh, hm));
	}

	/**
	 * Extract a valid OffsetDateTime object from a sentence.
	 *
	 * @param s The time or date/time sentence
	 * @return A valid Calendar object or null when no date/time info is contained in the sentence
	 * @throws GPSTimeException In case of malformed sentence date/time information
	 */
	public static OffsetDateTime extractTimestamp(Sentence s) throws GPSTimeException {
		NMEADateAndTime dt = extractInfo(s);
		if (dt != null) {
			return convert(dt);
		} else {
			return null;
		}
	}

	/**
	 * Extract a valid Instant object from a sentence.
	 *
	 * @param s The time or date/time sentence
	 * @return A valid Calendar object or null when no date/time info is contained in the sentence
	 * @throws GPSTimeException In case of malformed sentence date/time information
	 */
	public static Instant extractInstant(Sentence s) throws GPSTimeException {
		NMEADateAndTime dt = extractInfo(s);
		if (dt != null) {
			return convert(dt).toInstant();
		} else {
			return null;
		}
	}

	/**
	 * Extract a valid Calendar object from a sentence.
	 *
	 * @param s The time or date/time sentence
	 * @return A valid Calendar object or null when no date/time info is contained in the sentence
	 * @throws GPSTimeException In case of malformed sentence date/time information
	 * @deprecated Use extractTimestamp instead
	 */
	@Deprecated
	public static Calendar getTimestamp(Sentence s) throws GPSTimeException {
		OffsetDateTime d = extractTimestamp(s);
		if (d != null) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(d.toInstant().toEpochMilli());
			return c;
		} else {
			return null;
		}
	}
}
