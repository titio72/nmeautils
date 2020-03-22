package com.aboni.geo;

import com.aboni.misc.Utils;
import net.sf.marineapi.nmea.util.Position;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

@SuppressWarnings("unused")
public class GeoPositionT extends Position {

	public GeoPositionT(long timestamp, Position p) {
		super(p.getLatitude(), p.getLongitude());
		this.timestamp = timestamp;
	}

	public GeoPositionT(long timestamp, double lat, double lon) {
		super(lat, lon);
		this.timestamp = timestamp;
	}

	public GeoPositionT(Position p) {
		this(System.currentTimeMillis(), p);
	}

	private long timestamp;

	public long getTimestamp() {
		return timestamp;
	}

	public Instant getInstant() {
		return Instant.ofEpochMilli(timestamp);
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		DateFormat f = new SimpleDateFormat("HH:mm:ss");
		return f.format(new Date(timestamp)) +
				String.format(" %8.4f %s %8.4f %s", getLatitude(), 
						Utils.getLatitudeEmisphere(getLatitude()),
						getLongitude(), 
						Utils.getLongitudeEmisphere(getLongitude()));
	}
}
