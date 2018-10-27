package com.aboni.nmea.sentences;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import net.sf.marineapi.nmea.sentence.Checksum;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.util.Position;

public class XMCParser implements XMCSentence {

	private TalkerId tid;
	private Position average;
	private Position median;
	private DecimalFormat nf;
	
	public XMCParser(TalkerId tlk) {
		tid = tlk;
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
	public char getBeginChar() {
		return 0;
	}

	@Override
	public int getFieldCount() {
		return 8;
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
		return true;
	}

	@Override
	public void reset() {
	}

	@Override
	public void setBeginChar(char ch) {
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
		
		
		String med_lat = "";
		String avg_lat = "";
		String med_lon = "";
		String avg_lon = "";
		String med_NS = "";
		String med_EW = "";
		String avg_NS = "";
		String avg_EW = "";
		
		if (getAveragePosition()!=null) {
			avg_lat = getLatitude(getAveragePosition().getLatitude());
			avg_NS = getAveragePosition().getLatitudeHemisphere().toString();
			avg_lon = getLongitude(getAveragePosition().getLongitude());
			avg_NS = getAveragePosition().getLongitudeHemisphere().toString();
		}
		
		if (getMedianPosition()!=null) {
			med_lat = getLatitude(getMedianPosition().getLatitude());
			med_NS = getMedianPosition().getLatitudeHemisphere().toString();
			med_lon = getLongitude(getMedianPosition().getLongitude());
			med_NS = getMedianPosition().getLongitudeHemisphere().toString();
		}
		
		String s = String.format("$%sXMC,%s,%s,%s,%s,%s,%s,%s,%s", 
					tid.toString(),
					med_lat,
					med_NS,
					med_lon,
					med_EW,
					avg_lat,
					avg_NS,
					avg_lon,
					avg_EW
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
