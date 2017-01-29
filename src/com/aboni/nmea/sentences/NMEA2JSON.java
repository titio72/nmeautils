package com.aboni.nmea.sentences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

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

public class NMEA2JSON {
	
    private NumberFormat f0;
    private NumberFormat f1;
    private NumberFormat f2;
    private NumberFormat f3;
    private NumberFormat[] f;
	private DateFormat fISO; 
	private static final String COMMA = ", ";
	
    public NMEA2JSON() {
        f0 = new DecimalFormat("0");
        f1 = new DecimalFormat("0.0");
        f2 = new DecimalFormat("0.00");
        f3 = new DecimalFormat("0.000");
        f = new NumberFormat[] { f0, f1, f2, f3 };
        
		TimeZone tz = TimeZone.getTimeZone("UTC");
		fISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
		fISO.setTimeZone(tz);
	}
	
	private String getPair(String name, String value) {
		String res = "\"" + name + "\": \"" + value + "\"";
		return res;
	}
    
    private String getPair(String name, double value, int dec) {
        if (dec<0 || dec>3) dec = 0;
        String res = "\"" + name + "\": \"" + f[dec].format(value) + "\"";
        return res;
    }
    
    private String getPair(String name, Sentence s, String attribute, int dec) {
        if (dec<0 || dec>3) dec = 0;
        Method m;
        try {
            m = s.getClass().getMethod(attribute);
        } catch (Exception e1) {
            return null;
        }
        double value = Double.NaN;
        if (m!=null) {
            try {
                value = (Double) m.invoke(s, new Object[0]);
            } catch (InvocationTargetException e) {
                return null;
            } catch (Exception e) {
                return null;
            }
        }
        String res = "\"" + name + "\": \"" + f[dec].format(value) + "\"";
        return res;
    }
	
	private String formatLL(double d, CompassPoint p) {
		int deg = (int) Math.floor(d);
		double min = (d-deg)*60.0;
		String pp = (p==CompassPoint.EAST)?"E":(p==CompassPoint.WEST)?"W":(p==CompassPoint.NORTH)?"N":"S";
		
		return  String.format("%03d", deg) + " " + String.format("%06.3f", min) +   " " + pp;
	}
	
	
	public String convert(Sentence s) {
		String json = "{ " + getPair("topic", s.getSentenceId()) + COMMA;
		
		if (s.getSentenceId().equals(SentenceId.RMC.toString())) {
			// UTC time, lat, long, SOG, COG
			RMCSentence _s = (RMCSentence)s;
			Time t = _s.getTime();
			Date d = _s.getDate();
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			c.set(d.getYear(), d.getMonth(), d.getDay(), t.getHour(), t.getMinutes(), (int)t.getSeconds());
			
			double dec_lon = (_s.getPosition().getLongitudeHemisphere()==CompassPoint.WEST)?-_s.getPosition().getLongitude():_s.getPosition().getLongitude();
			double dec_lat = (_s.getPosition().getLatitudeHemisphere()==CompassPoint.SOUTH)?-_s.getPosition().getLatitude():_s.getPosition().getLatitude();
			
			json += getPair("UTC", fISO.format(c.getTime()) ) + COMMA;
			try {
				json += getPair("COG", _s.getCourse(), 0) + COMMA;
			} catch (DataNotAvailableException e) {
				json += getPair("COG", 0.0, 0) + COMMA;
			}
			try {
				json += getPair("SOG", _s.getSpeed(), 1) + COMMA;
			} catch (DataNotAvailableException e) {
				json += getPair("SOG", 0.0, 1) + COMMA;
			}
			json +=
					getPair("latitude", formatLL(_s.getPosition().getLatitude(), _s.getPosition().getLatitudeHemisphere()) ) + COMMA +
					getPair("longitude", formatLL(_s.getPosition().getLongitude(), _s.getPosition().getLongitudeHemisphere()) ) + COMMA +
					"\"dec_longitude\":" + dec_lon + COMMA +
					"\"dec_latitude\":" + dec_lat;
		} else if (s.getSentenceId().equals(SentenceId.DBT.toString())) {
			// depth
			DBTSentence _s = (DBTSentence)s;
			json +=  
					getPair("depth", _s.getDepth(), 1);
		} else if (s.getSentenceId().equals(SentenceId.DPT.toString())) { /* OK */
			// depth in meters, fathoms and feet
			DPTSentence _s = (DPTSentence)s;
			json +=  
					getPair("depth", _s.getDepth(), 1);
        } else if (s.getSentenceId().equals(SentenceId.HDG.toString())) { /* OK */
            // vessel heading with deviation and variation
            HDGSentence _s = (HDGSentence)s;
            json +=  
                    getPair("angle", _s.getHeading(), 0);
            try { json += COMMA + getPair("variation", _s.getVariation(), 1); } catch (Exception e) {}
            try { json += COMMA + getPair("deviation", _s.getDeviation(), 1); } catch (Exception e) {}
        } else if (s.getSentenceId().equals(SentenceId.HDM.toString())) { /* OK */
            HDMSentence _s = (HDMSentence)s;
            json +=  
                    getPair("angle", _s.getHeading(), 0);
        } else if (s.getSentenceId().equals(SentenceId.HDT.toString())) { /* OK */
            HDTSentence _s = (HDTSentence)s;
            json +=  
                    getPair("angle", _s.getHeading(), 0);
		} else if (s.getSentenceId().equals(SentenceId.MWV.toString())) { /* OK */
			// wind speed and direction (true and apparent)
			MWVSentence _s = (MWVSentence)s;
			json +=  
					 getPair("angle", _s.getAngle(), 0) + COMMA +
					 getPair("reference", (_s.isTrue()?"T":"R")) + COMMA +
					 getPair("speed", _s.getSpeed(), 1) + COMMA +
					 getPair("unit", _s.getSpeedUnit().toString());
		} else if (s.getSentenceId().equals(SentenceId.MWD.toString())) { /* OK */
			// wind speed and direction (true and apparent)
			MWDSentence _s = (MWDSentence)s;
			json +=  
					 getPair("mag_angle", _s.getMagneticWindDirection(), 0) + COMMA +
					 getPair("true_angle", _s.getTrueWindDirection(), 0) + COMMA +
					 getPair("speed", _s.getWindSpeed()/0.51444444444, 1);
		} else if (s.getSentenceId().equals(SentenceId.VHW.toString())) { /* OK */
			// water speed and heading
			VHWSentence _s = (VHWSentence)s;
			try {
				json += getPair("true_angle", _s.getHeading(), 0) + COMMA;
			} catch (DataNotAvailableException e) {
				json += getPair("true_angle", 0.0, 0) + COMMA;
			}
			try {
				json += getPair("mag_angle", _s.getMagneticHeading(), 0) + COMMA;
			} catch (DataNotAvailableException e) {
				json += getPair("mag_angle", 0.0, 0) + COMMA;
			}
			json += getPair("speed", _s.getSpeedKnots(), 1);
		} else if (s.getSentenceId().equals(SentenceId.MTW.toString())) { /* OK */
			// temperature
			MTWSentence _s = (MTWSentence)s;
			json +=  
					 getPair("temperature", _s.getTemperature(), 1);
		} else if (s.getSentenceId().equals("MTA")) { /* OK */
			// temperature
			MTASentence _s = (MTASentence)s;
			json +=  
					 getPair("temperature", _s.getTemperature(), 1);
		} else if (s.getSentenceId().equals("MMB")) { /* OK */
			// temperature
			MMBSentence _s = (MMBSentence)s;
			json +=  
					 getPair("pressure", _s.getBars()*1000.0, 0);
		} else if (s.getSentenceId().equals("VWR")) { /* OK */
			// temperature
			VWRSentence _s = (VWRSentence)s;
			json +=  
					 getPair("angle", _s.getAngle(), 0) + COMMA +
			         getPair("speed", _s.getSpeed(), 1);
		} else if (s.getSentenceId().equals(SentenceId.VTG.toString())) { /* OK */
			// temperature
			VTGSentence _s = (VTGSentence)s;
			 
					try {
						json += getPair("course", _s.getMagneticCourse(), 0) + COMMA;
					} catch (Exception e) {
						json += getPair("course", 0.0, 0) + COMMA;
					}
					try {
						json += getPair("trueCourse", _s.getTrueCourse(), 0) + COMMA;
					} catch (Exception e) {}
					try {
						json += getPair("speed", _s.getSpeedKnots(), 1);
					} catch (Exception e) {
						json += getPair("speed", 0.0, 1);
					}
        } else if (s.getSentenceId().equals("XDP")) {
            XDPSentence _s = (XDPSentence)s;
            json += getPair("depth", _s.getDepth(), 1);
            try {
                json += COMMA + getPair("maxDepth", _s.getMaxDepth1h(), 1);
            } finally {}
            try {
                json += COMMA + getPair("minDepth", _s.getMinDepth1h(), 1);
            } finally {}
        } else if (s.getSentenceId().equals("XXP")) {
		    XXXPSentence _s = (XXXPSentence)s;
		    StringBuilder content = new StringBuilder("");
            addNodeToJSON(content, getPair("pressure", 	_s, "getPressure", 	1), true);
            addNodeToJSON(content, getPair("temperature", 	_s, "getTemperature", 1), true);
            addNodeToJSON(content, getPair("rotX", 		_s, "getRotationX", 2), true);
            addNodeToJSON(content, getPair("rotY", 		_s, "getRotationY", 2), true);
            addNodeToJSON(content, getPair("rotZ", 		_s, "getRotationZ", 2), true);
            addNodeToJSON(content, getPair("magX", 		_s, "getMagX", 		2), true);
            addNodeToJSON(content, getPair("magY", 		_s, "getMagY", 		2), true);
            addNodeToJSON(content, getPair("magZ", 		_s, "getMagZ", 		2), true);
            addNodeToJSON(content, getPair("voltage1", 	_s, "getVoltage", 	2), true); 
            addNodeToJSON(content, getPair("voltage2", 	_s, "getVoltage1", 	2), true); 
            addNodeToJSON(content, getPair("heading", 		_s, "getHeading", 	2), false);
            json += content.toString();
		} else if (s.getSentenceId().equals("XDR")) {
            XDRSentence _s = (XDRSentence)s;
            StringBuilder content = new StringBuilder("");

            boolean first = true;
            List<Measurement> mm = _s.getMeasurements();
            for (Measurement m: mm) {
                if (!first) content.append(COMMA);
                content.append("\"" + m.getName() + "\":");
                content.append("{");
                content.append("\"type\":\"" + m.getType() + "\"" + COMMA);
                content.append("\"value\":" + f2.format(m.getValue()) + COMMA);
                content.append("\"unit\":\"" + m.getUnits() + "\"");
                content.append("}");
                first = false;
            }
            json += content.toString();
		}
		json += " }";
		return json;
	}
	
	private void addNodeToJSON(StringBuilder j, String node, boolean more) {
	    if (node!=null) {
	        j.append(node);
	        if (more) {
	            j.append(COMMA);
	        }
	    }
	}
}
