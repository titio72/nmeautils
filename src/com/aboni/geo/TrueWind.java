package com.aboni.geo;

public class TrueWind {
    
    private final double trueWindSpeed;
    private final double trueWindDeg;

    /**
     * The true wind speed.
     * @return The true wind speed.
     */
    public Double getTrueWindSpeed() {
        return trueWindSpeed;
    }

    /**
     * The true wind direction in degrees in the range [-180, 180].
     * @return The true wind direction.
     */
    public Double getTrueWindDeg() {
        return trueWindDeg;
    }

    /**
     * Initializes the true wind calculation.
     * @param speed The speed of the boat in knots.
     * @param appWindDeg The direction of the apparent wind in degrees [-180, 180].
     * @param appWindSpeed The speed of the apparent wind.
     */
    public TrueWind(double speed, double appWindDeg, double appWindSpeed) {
        double wA =  Math.toRadians(appWindDeg);
        
        double wAx = appWindSpeed * Math.sin(wA);
        double wAy = appWindSpeed * Math.cos(wA);

        double wTy = wAy - speed;
        
        trueWindDeg = Math.toDegrees((Math.PI / 2) - Math.atan2(wTy, wAx));
        trueWindSpeed = Math.sqrt(wAx * wAx + wTy*wTy);
    }

}
