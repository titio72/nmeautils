package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.parser.SentenceParser;
import net.sf.marineapi.nmea.sentence.TalkerId;

public class MMBParser extends SentenceParser implements MMBSentence {

	public static final String NMEA_SENTENCE_TYPE = "MMB";  
	
	private static final int PRES_IHG = 0;
	private static final int PRES_IHG_TYPE = 1;
	private static final int PRES_BAR = 2;
	private static final int PRES_BAR_TYPE = 3;
	
	
	public MMBParser(String nmea) {
		super(nmea, NMEA_SENTENCE_TYPE);
	}
	
	public MMBParser(TalkerId talker) {
		super(talker, NMEA_SENTENCE_TYPE, 4);
		setStringValue(PRES_BAR_TYPE, "B");
		setStringValue(PRES_IHG_TYPE, "I");
	}

	/* (non-Javadoc)
	 * @see com.aboni.utils.MMBSentence#getPresBar()
	 */
	@Override
	public double getPresBar() {
		return getDoubleValue(PRES_BAR);
	}

	/* (non-Javadoc)
	 * @see com.aboni.utils.MMBSentence#setPresBar(double)
	 */
	@Override
	public void setPresBar(double presBar) {
		setDoubleValue(PRES_BAR, presBar, 1, 5);
		setDoubleValue(PRES_IHG, presBar * 29.529983071415973, 2, 4);
	}

	/* (non-Javadoc)
	 * @see com.aboni.utils.MMBSentence#getPresIHG()
	 */
	@Override
	public double getPresIHG() {
		return getDoubleValue(PRES_IHG);
	}

	/* (non-Javadoc)
	 * @see com.aboni.utils.MMBSentence#setPresIHG(double)
	 */
	@Override
	public void setPresIHG(double presIHG) {
		setDoubleValue(PRES_BAR, presIHG / 29.529983071415973, 2, 4);
		setDoubleValue(PRES_IHG, presIHG, 1, 5);
	}
}
