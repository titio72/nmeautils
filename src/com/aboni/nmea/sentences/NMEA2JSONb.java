package com.aboni.nmea.sentences;

import com.aboni.misc.Utils;
import com.aboni.seatalk.Stalk84;
import net.sf.marineapi.nmea.parser.DataNotAvailableException;
import net.sf.marineapi.nmea.sentence.*;
import net.sf.marineapi.nmea.util.*;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

@SuppressWarnings("MagicConstant")
public class NMEA2JSONb {

	private static final String TOPIC = "topic";
	private static final String ANGLE = "angle";
	private static final String PRESSURE = "pressure";
	private static final String DEPTH = "depth";
	private static final String SPEED = "speed";
	private final DateFormat fISO;
	
    public NMEA2JSONb() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		fISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		fISO.setTimeZone(tz);
	}

	private static JSONObject create(Sentence s) {
		JSONObject json = new JSONObject();
		json.put(TOPIC, s.getSentenceId());
		return json;
	}

	public JSONObject convert(Sentence s) {
		JSONObject json;
		switch (s.getSentenceId()) {
			case "RMC":
				json = convertRMC((RMCSentence)s); break;
			case "VLW":
				json = convertVLW((VLWSentence)s); break;
			case "DPT":
				json = convertDPT((DPTSentence)s); break;
			case "DBT":
				json = convertDBT((DBTSentence)s); break;
			case "HDG":
				json = convertHDG((HDGSentence)s); break;
			case "HDM":
				json = convertHDM((HDMSentence)s); break;
			case "HDT":
				json = convertHDT((HDTSentence)s); break;
			case "MWV":
				json = convertMWV((MWVSentence)s); break;
			case "MWD":
				json = convertMWD((MWDSentence)s); break;
			case "VHW":
				json = convertVHW((VHWSentence)s); break;
			case "MTW":
				json = convertMTW((MTWSentence)s); break;
			case "MTA":
				json = convertMTA((MTASentence)s); break;
			case "MMB":
				json = convertMMB((MMBSentence)s); break;
			case "VWR":
				json = convertVWR((VWRSentence)s); break;
			case "VTG":
				json = convertVTG((VTGSentence)s); break;
			case "XDP":
				json = convertXDP((XDPSentence)s); break;
			case "MDA":
				json = convertMDA((MDASentence)s); break;
			case "RSA":
				json = convertRSA((RSASentence)s); break;
        	case "XMC":
				json = convertXMC((XMCSentence)s); break;
			case "XDR":
				json = convertXDR((XDRSentence)s); break;
			case "ALK":
				json = convertSTALK((STALKSentence)s); break;
			default:
				json = null;
				break;
		}
		return json;
	}

	private JSONObject convertRSA(RSASentence s) {
		JSONObject json = create(s);
		try {
			json.put(ANGLE, s.getRudderAngle(Side.STARBOARD));
			return json;
		} catch (Exception ignored) {
			// abort
			return null;
		}
	}

	private JSONObject convertMDA(MDASentence s) {
		JSONObject json = create(s);
		try { json.put("airTemp", s.getAirTemperature()); } catch (Exception ignored) { /* optional data */ }
		try { json.put("waterTemp", s.getWaterTemperature()); } catch (Exception ignored) { /* optional data */ }
		try { json.put(PRESSURE, s.getPrimaryBarometricPressure()); } catch (Exception ignored) { /* optional data */ }
		try { json.put("humidity", s.getRelativeHumidity()); } catch (Exception ignored) { /* optional data */ }
		return json;
	}

	private JSONObject convertXDP(XDPSentence s) {
		JSONObject json = create(s);
		json.put(DEPTH, s.getDepth());
		try { json.put("maxDepth", s.getMaxDepth1h()); } catch (Exception ignored) { /* optional data */ }
		try { json.put("minDepth", s.getMinDepth1h()); } catch (Exception ignored) { /* optional data */ }
		return json;
	}

	private JSONObject convertVTG(VTGSentence s) {
		JSONObject json = create(s);
		int count = 0;
		try { json.put("course", s.getMagneticCourse()); count++; }catch (Exception ignored) { /* optional data */ }
		try { json.put(SPEED, s.getSpeedKnots()); count++; } catch (Exception ignored) { /* optional data */ }
		try { json.put("trueCourse", s.getTrueCourse()); count++; } catch (Exception ignored) { /* optional data */ }
		return (count>0)?json:null;
	}

	private JSONObject convertVWR(VWRSentence s) {
		JSONObject json = create(s);
		json.put(ANGLE, s.getAngle());
		json.put(SPEED, s.getSpeed());
		return json;
	}

	private JSONObject convertMMB(MMBSentence s) {
		JSONObject json = create(s);
		double p = s.getBars();
		if (p>100)
			json.put(PRESSURE, s.getBars());
		else
			json.put(PRESSURE, s.getBars() * 1000.0);
		return json;
	}

	private JSONObject convertXMC(XMCSentence s) {
		JSONObject json = create(s);
		int count = 0;
        try { json.put("avg_lat",
				Utils.formatLL(
						s.getAveragePosition().getLatitude(),
                		s.getAveragePosition().getLatitudeHemisphere())); count++; } catch (Exception ignored) { /* optional data */ }
        try { json.put("avg_lon",
				Utils.formatLL(
						s.getAveragePosition().getLongitude(),
                		s.getAveragePosition().getLongitudeHemisphere())); count++; } catch (Exception ignored) { /* optional data */ }
        try { json.put("anchor", s.isAnchor()); count++; } catch (Exception ignored) { /* optional data */ }
		return (count>0)?json:null;
    }

    private JSONObject convertXDR(XDRSentence s) {
		JSONObject json = create(s);
		List<Measurement> mm = s.getMeasurements();
		for (Measurement m: mm) {
			JSONObject mJ = new JSONObject();
			mJ.put("type", m.getType());
			mJ.put("value", m.getValue());
			mJ.put("unit", m.getUnits());
			json.put(m.getName(), mJ);
		}
		return json;
	}

	private JSONObject convertSTALK(STALKSentence s) {
		if ("84".equals(s.getCommand())) {
			JSONObject json = create(s);
			Stalk84 s84 = new Stalk84(s);
			json.put(TOPIC, "auto");
			json.put("heading", s84.getHeading());
			json.put("headingAuto", s84.getAutoDeg());
			json.put("rudder", s84.getRudder());
			String status = "StandBy";
			if (s84.isAuto()) status = "Auto";
			if (s84.isWind()) status = "WindVane";
			if (s84.isTrack()) status = "Track";
			json.put("status", status);
			json.put("offCourse", s84.isErrOffCourse());
			json.put("windShift", s84.isErrWindShift());
			return json;
		} else {
			return null;
		}
	}

	private JSONObject convertMTA(MTASentence s) {
		JSONObject json = create(s);
		json.put("temperature", s.getTemperature());
		return json;
	}

	private JSONObject convertMTW(MTWSentence s) {
		JSONObject json = create(s);
		json.put("temperature", s.getTemperature());
		return json;
	}

	private JSONObject convertVHW(VHWSentence s) {
		JSONObject json = create(s);
		int count = 0;
		try { json.put("mag_angle", s.getMagneticHeading()); count++; } catch (Exception ignored) { /* optional data */ }
		try { json.put("true_angle", s.getHeading()); count++; } catch (Exception ignored) { /* optional data */ }
		try { json.put(SPEED, s.getSpeedKnots()); count++; } catch (Exception e) {
			try { json.put(SPEED, s.getSpeedKmh() / 1.852); count++; } catch (Exception ignored) { /* optional data */ }
		}
		return (count>0)?json:null;
	}

	private JSONObject convertMWD(MWDSentence s) {
		JSONObject json = create(s);
		try {
			json.put("true_angle", s.getTrueWindDirection());
		} catch (Exception ignored) { /* optional data */ }
		try {
			json.put("mag_angle", s.getMagneticWindDirection());
		} catch (Exception ignored) { /* optional data */ }
		json.put(SPEED, s.getWindSpeed()/0.51444444444 );
		return json;
	}

	private JSONObject convertMWV(MWVSentence s) {
		JSONObject json = create(s);
		json.put(TOPIC, (s.isTrue()?"MWV_T":"MWV_R")); // override topic
		json.put(ANGLE, s.getAngle());

		double speed;
		switch (s.getSpeedUnit()) {
			case METER:
				speed = s.getSpeed() * 1.94384;
				break;
			case KMH:
				speed = s.getSpeed() / 1.852;
				break;
			default:
				speed = s.getSpeed();
				break;
		}
		json.put(SPEED, speed);
		json.put("unit", "K");
		return json;
	}

	private JSONObject convertHDT(HDTSentence s) {
		JSONObject json = create(s);
		json.put(ANGLE, s.getHeading());
		return json;
	}

	private JSONObject convertHDM(HDMSentence s) {
		JSONObject json = create(s);
		json.put(ANGLE, s.getHeading());
		return json;
	}

	private JSONObject convertHDG(HDGSentence s) {
		// vessel heading with deviation and variation
		JSONObject json = create(s);
		json.put(ANGLE, s.getHeading());
		try { json.put("variation", s.getVariation()); } catch (Exception ignored) { /* optional data */ }
		try { json.put("deviation", s.getDeviation()); } catch (Exception ignored) { /* optional data */ }
		return json;
	}

	private JSONObject convertDBT(DBTSentence s) {
		JSONObject json = create(s);
		json.put(DEPTH, s.getDepth());
		return json;
	}

	private JSONObject convertDPT(DPTSentence s) {
		JSONObject json = create(s);
		double d = s.getDepth();
		double o;
		try { o = s.getOffset(); } catch (Exception ignored) { o = 0.0; }
		json.put("raw_depth", d);
		json.put("offset", o);
		json.put(DEPTH, d + o);
		return json;
	}

	private JSONObject convertVLW(VLWSentence s) {
		JSONObject json = create(s);
		double tot = s.getTotal();
		double trip = s.getTrip();
		json.put("total", tot);
		json.put("trip", trip);
		return json;
	}

	private JSONObject convertRMC(RMCSentence s) {
		JSONObject json = create(s);
		if (s.isValid()) {
			Time t = s.getTime();
			Date d = s.getDate();
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			c.set(d.getYear(), d.getMonth() - 1, d.getDay(), t.getHour(), t.getMinutes(), (int)t.getSeconds());
			double decLon = s.getPosition().getLongitude();
			double decLat = s.getPosition().getLatitude();
			json.put("UTC", fISO.format(c.getTime()));
			try { json.put("COG", s.getCourse()); } catch (DataNotAvailableException e) { json.put("COG", 0.0); }
			try { json.put("SOG", s.getSpeed()); } catch (DataNotAvailableException e) { json.put("SOG", 0.0); }
			json.put("latitude", Utils.formatLL(Math.abs(decLat), s.getPosition().getLatitudeHemisphere()) );
			json.put("longitude", Utils.formatLL(Math.abs(decLon), s.getPosition().getLongitudeHemisphere()) );
			json.put("dec_longitude", decLon);
			json.put("dec_latitude",  decLat);
			return json;
		} else {
			return null;
		}
	}
}
