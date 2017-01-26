package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.parser.SentenceParser;
import net.sf.marineapi.nmea.sentence.TalkerId;

public class MTAParser extends SentenceParser implements MTASentence {
	
	public static final String NMEA_SENTENCE_TYPE = "MTA";
	
	private static final int TEMP_CELSIUS = 0;
	private static final int CELSIUS = 1;
	
	public MTAParser(String nmea) {
		super(nmea, NMEA_SENTENCE_TYPE);
	}
	
	public MTAParser(TalkerId talker) {
		super(talker, NMEA_SENTENCE_TYPE, 4);
		setStringValue(CELSIUS, "C");
	}
	
	/* (non-Javadoc)
	 * @see com.aboni.utils.MTASentence#getTemperature()
	 */
	@Override
	public double getTemperature() {
		return getDoubleValue(TEMP_CELSIUS);
	}
	
	/* (non-Javadoc)
	 * @see com.aboni.utils.MTASentence#setTemperature(double)
	 */
	@Override
	public void setTemperature(double t) {
		setDoubleValue(TEMP_CELSIUS, t, 3, 2);
	}
	
}
