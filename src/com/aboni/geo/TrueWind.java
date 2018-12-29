package com.aboni.geo;

public class TrueWind {
    
    private final double trueWindSpeed;
    private final double trueWindDeg;
    
    public Double getTrueWindSpeed() {
        return trueWindSpeed;
    }
    
    public Double getTrueWindDeg() {
        return trueWindDeg;
    }
    
    public TrueWind(double speed, double appWindDeg, double appWindSpeed) {
        double wA =  Math.toRadians(appWindDeg);
        
        double wAx = appWindSpeed * Math.sin(wA);
        double wAy = appWindSpeed * Math.cos(wA);

        double wTy = wAy - speed;
        
        trueWindDeg = Math.toDegrees((Math.PI / 2) - Math.atan2(wTy, wAx));
        trueWindSpeed = Math.sqrt(wAx * wAx + wTy*wTy);
    }

}
