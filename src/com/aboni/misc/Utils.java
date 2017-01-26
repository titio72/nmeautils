package com.aboni.misc;

public class Utils {
    
	public static double normalizeDegrees0_360(double m) {
        if (m>360.0) {
            return m - (360*((int)(m/360)));
        }
        else if (m<0.0) {
            return m + (360*((int)(-m/360))) + 360;
        }
        return m;
    }

	public static double normalizeDegrees180_180(double m) {
        m = normalizeDegrees0_360(m);
        if (m>180.0) {
        	m -= 360.0;
        }
        return m;
    }
	
	/**
	 * 
	 * @param d
	 * @return
	 */
	public static String getLatitudeEmisphere(double d) {
		return d>0?"N":"S";
	}
	
	/**
	 * 
	 * @param d
	 * @return
	 */
	public static String getLongitudeEmisphere(double d) {
		return d>0?"E":"W";
	}
	
	
	/**
	 * Converts a latitude value with N or S indication to signed (+ for North a - for South)
	 * @param lat The unsigned latitude
	 * @param NS North or South. Accepted values are 'N', 'n', 'S' & 's'
	 * @return The signed latitude.
	 */
	public static double getSignedLatitude(double lat, char NS) {
		if (NS=='N' || NS=='N' || NS=='N' || NS=='N') {
			if (NS=='S' || NS=='s') return -lat;
			else return lat;
		} else {
			return 0.0;
		}
	}
	
	
    public static String getCardinal(double i) {
        if (i<23) return "N";
        else if (i<68) return "NE";
        else if (i<113) return "E";
        else if (i<158) return "SE";
        else if (i<203) return "S";
        else if (i<248) return "SW";
        else if (i<293) return "W";
        else if (i<338) return "NW";
        else return "N";
    }
    
	
	public static double getNormal(double ref, double a) {
		ref = Utils.normalizeDegrees0_360(ref);
		a = Utils.normalizeDegrees0_360(a);

		double l = a - ref;
		if (l>180) {
			a = a - 360;
		} else if (l<-180) {
			a = a + 360;
		}
		
		return a;
	}
}
