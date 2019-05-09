package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.sentence.Checksum;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.util.CompassPoint;
import net.sf.marineapi.nmea.util.Position;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class XMCParser implements XMCSentence {

	private TalkerId tid;
	private Position average;
	private Position median;
	private boolean anchor;
	private final DecimalFormat nf;
	
	public XMCParser(TalkerId tlk) {
		tid = tlk;
		anchor = false;
		nf = new DecimalFormat("00.000");
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		nf.setDecimalFormatSymbols(dfs);
	}
	
	@Override
	public Position getMedianPosition() {
		return median;
	}

	@Override
	public void setMedianPosition(Position p) {
		median = p;
	}

	@Override
	public Position getAveragePosition() {
		return average;
	}

	@Override
	public void setAveragePosition(Position p) {
		average = p;
	}
	
	@Override
	public void setAnchor(boolean b) {
		anchor = b;
	}
	
	@Override
	public boolean isAnchor() {
		return anchor;
	}

	@Override
	public char getBeginChar() {
		return 0;
	}

	@Override
	public int getFieldCount() {
		return 9;
	}

	@Override
	public String getSentenceId() {
		return "XMC";
	}

	@Override
	public TalkerId getTalkerId() {
		return tid;
	}

	@Override
	public boolean isAISSentence() {
		return false;
	}

	@Override
	public boolean isProprietary() {
		return true;
	}

	@Override
	public boolean isValid() {
		return average!=null && median!=null;
	}

	@Override
	public void reset() {
		average = null;
		median = null;
		anchor = false;
	}

	@Override
	public void setBeginChar(char ch) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setTalkerId(TalkerId id) {
		tid = id;
	}

	private String getLatitude(double x) {
		int deg = (int) Math.floor(x);
		double min = (x - deg) * 60;
		return String.format("%02d%s", deg, nf.format(min));
	}
	
	private String getLongitude(double x) {
		int deg = (int) Math.floor(x);
		double min = (x - deg) * 60;
		return String.format("%03d%s", deg, nf.format(min));
	}
	
	@Override
	public String toSentence() {
		
		
		String medLat = "";
		String avgLat = "";
		String medLon = "";
		String avgLon = "";
		String medNS = "";
		String medEW = "";
		String avgNS = "";
		String avgEW = "";
		
		if (getAveragePosition()!=null) {
			avgLat = getLatitude(getAveragePosition().getLatitude());
			avgNS = (getAveragePosition().getLatitudeHemisphere()==CompassPoint.NORTH)?"N":"S";
			avgLon = getLongitude(getAveragePosition().getLongitude());
			avgEW = (getAveragePosition().getLongitudeHemisphere()==CompassPoint.EAST)?"E":"W";
		}
		
		if (getMedianPosition()!=null) {
			medLat = getLatitude(getMedianPosition().getLatitude());
			medNS = getMedianPosition().getLatitudeHemisphere()==CompassPoint.NORTH?"N":"S";
			medLon = getLongitude(getMedianPosition().getLongitude());
			medEW = getMedianPosition().getLongitudeHemisphere()==CompassPoint.EAST?"E":"W";
		}
		
		String s = String.format("$%sXMC,%s,%s,%s,%s,%s,%s,%s,%s,%d", 
					tid.toString(),
					medLat,
					medNS,
					medLon,
					medEW,
					avgLat,
					avgNS,
					avgLon,
					avgEW,
					anchor?1:0
				);

		return s + "*" + Checksum.xor(s);
	}

	@Override
	public String toSentence(int maxLength) {
		String s = toSentence();
		s = toString().substring(0, Math.min(maxLength, s.length()));
		return s;
	}
	
	@Override
	public String toString() {
		return toSentence();
	}

}
