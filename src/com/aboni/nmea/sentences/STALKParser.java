package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.parser.SentenceParser;
import net.sf.marineapi.nmea.sentence.TalkerId;

public class STALKParser extends SentenceParser implements STALKSentence{

	public static final String NMEA_SENTENCE_TYPE = "ALK";
	
    private static final int COMMAND = 0;
    private static final int PARAMS = 1;
    
	public STALKParser(String nmea) {
		super(nmea);
	}
	
	public STALKParser(TalkerId ttid) {
		super(TalkerId., NMEA_SENTENCE_TYPE, 1);
	}
	
	/* (non-Javadoc)
	 * @see com.aboni.nmea.sentences.STALKSentence#getCommand()
	 */
	@Override
	public String getCommand() {
		return getStringValue(COMMAND);
	}
	
	/* (non-Javadoc)
	 * @see com.aboni.nmea.sentences.STALKSentence#setCommand(java.lang.String)
	 */
	@Override
	public void setCommand(String cmd) {
		setStringValue(COMMAND, cmd);
	}
	
	/* (non-Javadoc)
	 * @see com.aboni.nmea.sentences.STALKSentence#getParams()
	 */
	@Override
	public int[] getParams() {
		int pCount = (getFieldCount()-1);
		if (pCount>0) {
			int[] ret = new int[pCount]; 
			for (int i = 0; i<pCount; i++) {
				ret[i] = Integer.parseInt(getStringValue(i + PARAMS), 16);
			}
			return ret;
		} else {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.aboni.nmea.sentences.STALKSentence#setParams(int[])
	 */
	@Override
	public void setParams(int[] params) {
		setFieldCount(params.length + 1);
		for (int i = 0; i<params.length; i++) {
			int p = params[i];
			setStringValue(PARAMS + i, Integer.toHexString(p));
		}
	}
}
