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

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.marineapi.nmea.parser.DataNotAvailableException;
import net.sf.marineapi.nmea.sentence.HDGSentence;
import net.sf.marineapi.nmea.sentence.HeadingSentence;
import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.sentence.VHWSentence;
import net.sf.marineapi.nmea.util.CompassPoint;
import net.sf.marineapi.nmea.util.Position;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressWarnings("unused")
public class Utils {

    public static final Calendar UTC_CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

	private static final SimpleDateFormat ISO_TIMESTAMP_UTC_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	static {
		ISO_TIMESTAMP_UTC_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

    private Utils() {
	}

	public static Date getISODateUTC(String sDate) throws ParseException {
		return ISO_TIMESTAMP_UTC_FORMATTER.parse(sDate);
	}

	public static String formatISOTimestampUTC(Date date) {
		return ISO_TIMESTAMP_UTC_FORMATTER.format(date);
	}

	/**
	 * Checks is a timestamp is older than a given age.
	 *
	 * @param ts  The timestamp to be checked in ms (unix time)
	 * @param now The current timestamp (unix time). If -1 the system time will be used instead.
	 * @param age The age in ms
	 * @return true is the given timestamp is older than the age or it's 0, false otherwise.
	 */
	public static boolean isOlderThan(long ts, long now, long age) {
		if (now == -1) now = System.currentTimeMillis();
		return (now - ts) > age;
	}

	/**
	 * Checks is a timestamp is not newer than a given age.
	 *
	 * @param ts  The timestamp to be checked in ms (unix time)
	 * @param now The current timestamp (unix time). If -1 the system time will be used instead.
	 * @param age The age in ms
	 * @return true is the given timestamp is not newer than the age or it's 0, false otherwise.
	 */
	public static boolean isNotNewerThan(long ts, long now, long age) {
		if (now == -1) now = System.currentTimeMillis();
		return (now - ts) >= age;
	}

	/**
	 * Convert wind speed in knots
	 *
	 * @param s The wind sentence to extract the wind speed from
	 * @return The wind speed in knots
	 */
	public static double getSpeedKnots(MWVSentence s) {
		if (s != null) {
			double speed;
			try {
				switch (s.getSpeedUnit()) {
					case METER:
						speed = Utils.round(s.getSpeed() / 0.5144444, 2);
						break;
					case KMH:
						speed = Utils.round(s.getSpeed() / 1.852, 2);
						break;
					default:
						speed = s.getSpeed();
				}
			} catch (DataNotAvailableException e) {
				speed = 0.0;
			}
			return speed;
		} else {
			return 0.0;
		}
	}

	/**
	 * Extracts the true heading from a heding sentence.
	 * If the heading sentence is a HDG the provided variation & deviation will be taken into account.
	 * This method does not automatically calculate the variation if not given.
	 * @param h The heading sentence to extract the true heading from.
	 * @return The true heading if possible or just the heading provided by the sentence.
	 */
	public static double getTrueHeading(HeadingSentence h) {
		double deviation = 0.0;
		double variation = 0.0;
		try { if (h instanceof HDGSentence) deviation = ((HDGSentence)h).getDeviation(); } catch (Exception ignored) { deviation = 0.0; }
		try { if (h instanceof HDGSentence) variation = ((HDGSentence)h).getVariation(); } catch (Exception ignored) { variation = 0.0; }
		try {
			return h.getHeading() + variation + deviation;
		} catch (DataNotAvailableException e) {
			return Double.NaN;
		}
	}

	/**
	 * Extracts the true magnetic from a heading sentence.
	 * If the heading sentence is a HDG the provided deviation will be taken into account.
	 * @param h The heading sentence toe xtract the true heading from.
	 * @return The magnetic heading if possible or just the heading provided by the sentence.
	 */
	public static double getMagHeading(HeadingSentence h) {
		double dev = 0.0;
		try { if (h instanceof HDGSentence) dev = ((HDGSentence)h).getDeviation(); } catch (Exception ignored) { /* optional */ }
		try {
			if (h instanceof VHWSentence) {
				return ((VHWSentence)h).getMagneticHeading();
			} else {
				return h.getHeading() + dev;
			}
		} catch (DataNotAvailableException e) {
			return Double.NaN;
		}
	}

	/**
	 * Pauses the current threads.
	 * @param mseconds The length of the pause in milliseconds.
	 */
	public static void pause(int mseconds) {
		if (mseconds!=0) {
			try {
				Thread.sleep(mseconds);
			} catch (InterruptedException ignored) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Normalizes an angle value in degrees to an equivalent value in the range [0,360].
	 * @param degrees The value of an angle to be normalized.
	 * @return An equivalent angle in the range [0,360].
	 */
	public static double normalizeDegrees0To360(double degrees) {
        if (degrees>360.0) {
            return degrees - (360*((int)(degrees/360)));
        } else if (degrees<0.0) {
            return degrees + (360*((int)(-degrees/360))) + 360;
        } else {
        	return degrees;
        }
    }

    public static double round(double d, int decimals) {
        double p = Math.pow(10, decimals);
	    long i = Math.round(d * p);
        return i / p;
    }

	/**
	 * Normalizes an angle value in degrees to an equivalent value in the range [-180,180].
	 * @param degrees The value of an angle to be normalized.
	 * @return An equivalent angle in the range [-180,180].
	 */
	public static double normalizeDegrees180To180(double degrees) {
        degrees = normalizeDegrees0To360(degrees);
        if (degrees>180.0) {
        	degrees -= 360.0;
        }
        return degrees;
    }
	
	/**
	 * Human readable cardinal assuming that positive latitude is North and negative is South.
	 * @param d The latitude.
	 * @return "N" or "S" accordingl to the sign convention of the latitude.
	 */
	public static String getLatitudeEmisphere(double d) {
		return d>0?"N":"S";
	}
	
	/**
	 * Human readable cardinal assuming that positive longitude is East and negative is West.
	 * @param d The longitude.
	 * @return "E" or "W" accordingl to the sign convention of the longitude.
	 */
	public static String getLongitudeEmisphere(double d) {
		return d>0?"E":"W";
	}
	
	/**
	 * Converts a latitude value with N or S indication to signed (+ for North a - for South)
	 * @param lat The unsigned latitude
	 * @param northSouth North or South. Accepted values are 'N', 'n', 'S' & 's'
	 * @return The signed latitude.
	 */
	public static double getSignedLatitude(double lat, char northSouth) {
		if (northSouth=='N' || northSouth=='n')
			return lat;
		else if (northSouth=='S' || northSouth=='s')
			return -lat;
		else
			return 0.0;
	}

	/**
	 * Converts a longitude value with W or E indication to signed (+ for East a - for West)
	 * @param lon The unsigned longitude
	 * @param westEast East or West. Accepted values are 'N', 'n', 'S' & 's'
	 * @return The signed longitude.
	 */
	public static double getSignedLongitude(double lon, char westEast) {
		if (westEast=='E' || westEast=='e')
			return lon;
		else  if (westEast=='W' || westEast=='w')
			return -lon;
		else
			return 0.0;
	}
	
	public static double getNormal180(double ref, double angle) {
        boolean ref180 = ref < 0.0 && ref >= -180.0;
        double res = Utils.getNormal(ref, angle);
		if (ref180 && res>180.0) res -=360;
		return res;
    }

	public static double getNormal(double ref, double a) {
		ref = Utils.normalizeDegrees0To360(ref);
		a = Utils.normalizeDegrees0To360(a);

		double l = a - ref;
		if (l>180) {
			a = a - 360;
		} else if (l<-180) {
			a = a + 360;
		}
		
		return a;
	}

	private static final String[] CARDINALS = new String[] {
			"N", "NNE", "NE", "ENE", 
			"E", "ESE", "SE", "SSE",
			"S", "SSW", "SW", "WSW",
			"W", "WNW", "NW", "NNW"
	};
	
	public static String getCardinal(double deg) {
		double d = normalizeDegrees0To360(deg);
		d = d / 22.5;
		int dd = (int) Math.round(d);
		return CARDINALS[dd % 16];
	}

	public static Position calcNewLL(Position p0, double heading, double dist) {
		GeodesicData d = Geodesic.WGS84.Direct(p0.getLatitude(), p0.getLongitude(), heading, dist * 1852);
		return new Position(d.lat2, d.lon2);
	}
	
	public static double tack(double heading, double trueWind) {
		double tack = heading + 2 * trueWind;
		return Utils.normalizeDegrees0To360(tack);
	}

	public static String formatLL(double d, CompassPoint p) {
		int deg = (int) Math.floor(d);
		double min = (d-deg)*60.0;
		String pp;
		switch (p) {
			case EAST: pp = "E"; break;
			case WEST: pp = "W"; break;
			case NORTH: pp = "N"; break;
			case SOUTH: pp = "S"; break;
			default: pp = "?";
		}
		return String.format("%03d %06.3f %s", deg, min, pp);
	}

	public static String formatLatitude(double d) {
		double dAbs = Math.abs(d);
		int deg = (int) Math.floor(dAbs);
		double min = (dAbs - deg) * 60.0;
		return String.format("%02d %06.3f %s", deg, min, (d > 0) ? "N" : "S");
	}

	public static String formatLongitude(double d) {
		double dAbs = Math.abs(d);
		int deg = (int) Math.floor(dAbs);
		double min = (dAbs - deg) * 60.0;
		return String.format("%03d %06.3f %s", deg, min, (d > 0) ? "E" : "W");
	}

	public static long[] printGCStats() {
		long totalGarbageCollections = 0;
		long garbageCollectionTime = 0;

		for (GarbageCollectorMXBean gc :
				ManagementFactory.getGarbageCollectorMXBeans()) {

			long count = gc.getCollectionCount();

			if (count >= 0) {
				totalGarbageCollections += count;
			}

			long time = gc.getCollectionTime();

			if (time >= 0) {
				garbageCollectionTime += time;
			}
		}

		return new long[]{totalGarbageCollections, garbageCollectionTime};
	}
}
