package com.aboni.geo;

import net.sf.marineapi.nmea.util.Position;

@SuppressWarnings("unused")
public class Track {
    private final Position start;
    private final Position end;
    private Position pos;
    private double vmg;
    private double xte;
    private Course toDest;
    private final Course trip;
    
    public Track(Position start, Position end) {
        this.start = start;
        this.end = end;
        trip = new Course(start, end);
    }
    
    public void setPos(Position pos, double vmg) {
        this.pos = pos;
        this.vmg = vmg;
        toDest = new Course(pos, end);
        xte = crossTrackError(pos, start, end);
    }
    
    public double getBearing() {
        return trip.getCOG();
    }
    
    public double getTotalDistance() {
        return trip.getDistance();
    }
    
    public double getDistanceToDest() {
        return toDest.getDistance();
    }
    
    public double getBRG() {
        return toDest.getCOG();
    }
    
    public double getXTE() {
        return Math.abs(xte);
    }
    
    public Position getStart() {
        return start;
    }
    
    public Position getEnd() {
        return end;
    }
    
    public Position getPosTart() {
        return pos;
    }
    
    public double getVMG() {
        return vmg;
    }

    public static final int PORT =         -1;
    public static final int STARBOARD =  1;
    public static final int LEFT =         -1;
    public static final int RIGHT =      1;
    
    public int getTurn() {
        return (xte>0)?PORT:STARBOARD;
    }
    
    public static double crossTrackError(Position pCurrent, Position pStart, Position pEnd) {
        double R = 6371000; // average earth radius - should be ok at our latitudes
        Course cSE = new Course(pStart, pEnd);
        Course cSP = new Course(pStart, pCurrent);
        double a13 = cSP.getDistance() / R;
        double b13 = Math.toRadians(cSP.getCOG()); //pathStart.bearingTo(this).toRadians();
        double b12 = Math.toRadians(cSE.getCOG()); //pathStart.bearingTo(pathEnd).toRadians();
        double axt = Math.asin(Math.sin(a13) * Math.sin(b13-b12));
        return axt * R;
    }
}
