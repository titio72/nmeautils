package com.aboni.geo;

import com.aboni.utils.Utils;

public class ApparentWind {
    
    private final double appWindSpeed;
    private double appWindDeg;
    
    public Double getApparentWindSpeed() {
        return appWindSpeed;
    }
    
    public Double getApparentWindDeg() {
        return appWindDeg;
    }
    
    public ApparentWind(double speed, double trueWindDeg, double trueWindSpeed) {
    	trueWindDeg = Utils.normalizeDegrees180To180(trueWindDeg);
    	double sign = (trueWindDeg<0)?-1:1;
    	trueWindDeg = Math.abs(trueWindDeg);
    	appWindSpeed = Math.sqrt(speed * speed + trueWindSpeed * trueWindSpeed + 2d * Math.cos(Math.toRadians(trueWindDeg)) * speed * trueWindSpeed);
    	appWindDeg = Math.toDegrees(
    			Math.acos((trueWindSpeed * Math.cos(Math.toRadians(trueWindDeg)) + speed) / appWindSpeed) 
    			);
    	appWindDeg = Utils.normalizeDegrees0To360(appWindDeg * sign);
    }

}
