package com.aboni.nmea.sentences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import com.aboni.nmea.sentences.VWRSentence;
import com.aboni.nmea.sentences.XDPSentence;
import com.aboni.nmea.sentences.XXXPSentence;

import net.sf.marineapi.nmea.parser.DataNotAvailableException;
import net.sf.marineapi.nmea.sentence.DBTSentence;
import net.sf.marineapi.nmea.sentence.DPTSentence;
import net.sf.marineapi.nmea.sentence.HDGSentence;
import net.sf.marineapi.nmea.sentence.HDMSentence;
import net.sf.marineapi.nmea.sentence.HDTSentence;
import net.sf.marineapi.nmea.sentence.MMBSentence;
import net.sf.marineapi.nmea.sentence.MTASentence;
import net.sf.marineapi.nmea.sentence.MTWSentence;
import net.sf.marineapi.nmea.sentence.MWDSentence;
import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.VHWSentence;
import net.sf.marineapi.nmea.sentence.VTGSentence;
import net.sf.marineapi.nmea.sentence.XDRSentence;
import net.sf.marineapi.nmea.util.CompassPoint;
import net.sf.marineapi.nmea.util.Date;
import net.sf.marineapi.nmea.util.Measurement;
import net.sf.marineapi.nmea.util.Time;

public class NMEA2JSONb {
	
	private DateFormat fISO; 
	
    public NMEA2JSONb() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		fISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
		fISO.setTimeZone(tz);
	}
    
	private String formatLL(double d, CompassPoint p) {
		int deg = (int) Math.floor(d);
		double min = (d-deg)*60.0;
		String pp = (p==CompassPoint.EAST)?"E":(p==CompassPoint.WEST)?"W":(p==CompassPoint.NORTH)?"N":"S";
		return  String.format("%03d", deg) + " " + String.format("%06.3f", min) +   " " + pp;
	}
	
	public JSONObject convert(Sentence s) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("topic", s.getSentenceId());
		
		if (s.getSentenceId().equals(SentenceId.RMC.toString())) {
			RMCSentence _s = (RMCSentence)s;
			Time t = _s.getTime();
			Date d = _s.getDate();
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			c.set(d.getYear(), d.getMonth(), d.getDay(), t.getHour(), t.getMinutes(), (int)t.getSeconds());
			double dec_lon = (_s.getPosition().getLongitudeHemisphere()==CompassPoint.WEST)?-_s.getPosition().getLongitude():_s.getPosition().getLongitude();
			double dec_lat = (_s.getPosition().getLatitudeHemisphere()==CompassPoint.SOUTH)?-_s.getPosition().getLatitude():_s.getPosition().getLatitude();
			json.put("UTC", fISO.format(c.getTime()));
			try { json.put("COG", _s.getCourse()); } catch (DataNotAvailableException e) { json.put("COG", 0.0); }
			try { json.put("SOG", _s.getSpeed()); } catch (DataNotAvailableException e) { json.put("SOG", 0.0); }
			json.put("latitude", formatLL(_s.getPosition().getLatitude(), _s.getPosition().getLatitudeHemisphere()) );
			json.put("longitude", formatLL(_s.getPosition().getLongitude(), _s.getPosition().getLongitudeHemisphere()) );
			json.put("dec_longitude", dec_lon);
			json.put("dec_latitude",  dec_lat);
		} else if (s.getSentenceId().equals(SentenceId.DPT.toString())) {
			// depth
			DPTSentence _s = (DPTSentence)s;
			json.put("depth", _s.getDepth());
			try { json.put("offset", _s.getOffset()); } catch (Exception e) { json.put("offset", 0.0); }
		} else if (s.getSentenceId().equals(SentenceId.DBT.toString())) {
			// depth
			DBTSentence _s = (DBTSentence)s;
			json.put("depth", _s.getDepth());
		} else if (s.getSentenceId().equals(SentenceId.HDG.toString())) { /* OK */
            // vessel heading with deviation and variation
            HDGSentence _s = (HDGSentence)s;
            json.put("angle", _s.getHeading());
            try { json.put("variation", _s.getVariation()); } catch (Exception e) {}
            try { json.put("deviation", _s.getDeviation()); } catch (Exception e) {}
        } else if (s.getSentenceId().equals(SentenceId.HDM.toString())) { /* OK */
            HDMSentence _s = (HDMSentence)s;
            json.put("angle", _s.getHeading());
        } else if (s.getSentenceId().equals(SentenceId.HDT.toString())) { /* OK */
            HDTSentence _s = (HDTSentence)s;
            json.put("angle", _s.getHeading());
		} else if (s.getSentenceId().equals(SentenceId.MWV.toString())) { /* OK */
			MWVSentence _s = (MWVSentence)s;
			json.put("topic", (_s.isTrue()?"MWV_T":"MWV_R")); // override topic
			json.put("angle", _s.getAngle());
			json.put("speed", _s.getSpeed());
			json.put("unit", _s.getSpeedUnit().toString());
		} /*else if (s.getSentenceId().equals(SentenceId.MWV.toString())) { 
			MWVSentence _s = (MWVSentence)s;
			json.put("angle", _s.getAngle());
			json.put("reference", (_s.isTrue()?"T":"R"));
			json.put("speed", _s.getSpeed());
			json.put("unit", _s.getSpeedUnit().toString());
		} */else if (s.getSentenceId().equals(SentenceId.MWD.toString())) { /* OK */
			MWDSentence _s = (MWDSentence)s;
			json.put("mag_angle", _s.getMagneticWindDirection());
			json.put("speed", _s.getWindSpeed()/0.51444444444 );
			//json.put("speed", _s.getWindSpeedKnots());
			try { json.put("true_angle", _s.getTrueWindDirection()); } catch (Exception e) { json.put("true_angle", 0.0);}
		} else if (s.getSentenceId().equals(SentenceId.VHW.toString())) { /* OK */
			VHWSentence _s = (VHWSentence)s;
			json.put("mag_angle", _s.getMagneticHeading());
			json.put("speed", _s.getSpeedKnots());
			try { json.put("true_angle", _s.getHeading()); } catch (Exception e) { json.put("true_angle", 0.0); }
		} else if (s.getSentenceId().equals(SentenceId.MTW.toString())) {
			MTWSentence _s = (MTWSentence)s;
			json.put("temperature", _s.getTemperature());
		} else if (s.getSentenceId().equals("MTA")) {
			MTASentence _s = (MTASentence)s;
			json.put("temperature", _s.getTemperature());
		} else if (s.getSentenceId().equals("MMB")) {
			MMBSentence _s = (MMBSentence)s;
			double p = _s.getBars();
			if (p>100)
				json.put("pressure", _s.getBars());
			else
				json.put("pressure", _s.getBars() * 1000.0);
		} else if (s.getSentenceId().equals("VWR")) {
			VWRSentence _s = (VWRSentence)s;
			json.put("angle", _s.getAngle());
			json.put("speed", _s.getSpeed());
		} else if (s.getSentenceId().equals(SentenceId.VTG.toString())) {
			VTGSentence _s = (VTGSentence)s;
			json.put("course", _s.getMagneticCourse());
			json.put("speed", _s.getSpeedKnots());
			try { json.put("trueCourse", _s.getTrueCourse()); 	} catch (Exception e) {}
        } else if (s.getSentenceId().equals("XDP")) {
            XDPSentence _s = (XDPSentence)s;
            json.put("depth", _s.getDepth());
            try { json.put("maxDepth", _s.getMaxDepth1h()); } catch (Exception e) {}
            try { json.put("minDepth", _s.getMinDepth1h()); } catch (Exception e) {}
        } else if (s.getSentenceId().equals("XXP")) {
		    XXXPSentence _s = (XXXPSentence)s;
            try { json.put("pressure", 		_s.getPressure());	 	} catch (Exception e) {}
            try { json.put("temperature", 	_s.getTemperature());	} catch (Exception e) {}
            try { json.put("rotX", 			_s.getRotationX()); 	} catch (Exception e) {}
            try { json.put("rotY", 			_s.getRotationY()); 	} catch (Exception e) {}
            try { json.put("rotZ", 			_s.getRotationZ()); 	} catch (Exception e) {}
            try { json.put("magX", 			_s.getMagX()); 			} catch (Exception e) {}
            try { json.put("magY", 			_s.getMagY()); 			} catch (Exception e) {}
            try { json.put("magZ", 			_s.getMagZ()); 			} catch (Exception e) {}
            try { json.put("voltage1", 		_s.getVoltage()); 		} catch (Exception e) {}
            try { json.put("voltage2", 		_s.getVoltage1()); 		} catch (Exception e) {}
            try { json.put("heading", 		_s.getHeading()); 		} catch (Exception e) {}
		} else if (s.getSentenceId().equals("XDR")) {
            XDRSentence _s = (XDRSentence)s;
            List<Measurement> mm = _s.getMeasurements();
            for (Measurement m: mm) {
            	JSONObject mJ = new JSONObject();
                mJ.put("type", m.getType());
                mJ.put("value", m.getValue());
                mJ.put("unit", m.getUnits());
                json.put(m.getName(), mJ);
            }
		}
		return json;
	}
}
