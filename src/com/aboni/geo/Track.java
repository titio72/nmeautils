package com.aboni.geo;

import net.sf.marineapi.nmea.util.Position;

public class Track {
	private Position start;
	private Position end;
	private Position pos;
	private double vmg;
	private double xte;
	private Course toDest;
	private Course trip; 
	
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

	public static final int PORT = -1;
	public static final int STARBOARD = 1;
	public static final int LEFT = -1;
	public static final int RIGHT = 1;
	
	public int getTurn() {
		return (xte>0)?PORT:STARBOARD;
	}
	
	/**
	 * Returns (signed) distance from ‘this’ point to great circle defined by start-point and end-point.
	 *
	 * @param   {LatLon} pathStart - Start point of great circle path.
	 * @param   {LatLon} pathEnd - End point of great circle path.
	 * @param   {number} [radius=6371e3] - (Mean) radius of earth (defaults to radius in metres).
	 * @returns {number} Distance to great circle (-ve if to left, +ve if to right of path).
	 *
	 * @example
	 *   var pCurrent = new LatLon(53.2611, -0.7972);
	 *   var p1 = new LatLon(53.3206, -1.7297);
	 *   var p2 = new LatLon(53.1887,  0.1334);
	 *   var d = pCurrent.crossTrackDistanceTo(p1, p2);  // -307.5 m
	 */
	double crossTrackError(Position pCurrent, Position pStart, Position pEnd) {
		
		//double rr = Geodesic.WGS84.MajorRadius();
		
	    double R = 6371000;

	    Course cSE = new Course(pStart, pEnd);
	    //Course cPE = new Course(pCurrent, pEnd);
	    Course cSP = new Course(pStart, pCurrent);
	    
	    double δ13 = cSP.getDistance() / R;
	    
	    double θ13 = Math.toRadians(cSP.getCOG()); //pathStart.bearingTo(this).toRadians();
	    double θ12 = Math.toRadians(cSE.getCOG()); //pathStart.bearingTo(pathEnd).toRadians();

	    double δxt = Math.asin(Math.sin(δ13) * Math.sin(θ13-θ12));

	    return δxt * R;
	};

	
	
	
}
