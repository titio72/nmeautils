package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.parser.SentenceParser;
import net.sf.marineapi.nmea.sentence.Checksum;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceValidator;
import net.sf.marineapi.nmea.sentence.TalkerId;

public class STALKParser extends SentenceParser implements STALKSentence{

	public static final String NMEA_SENTENCE_TYPE = "STALK";
	
    private static final int COMMAND = 0;
    private static final int PARAMS = 1;
    
	public STALKParser(String nmea) {
		super(TalkerId.AG, NMEA_SENTENCE_TYPE, 1);
		if (!SentenceValidator.isValid(nmea)) {
			String msg = String.format("Invalid data [%s]", nmea);
			throw new IllegalArgumentException(msg);
		}

		setBeginChar(nmea.charAt(0));
		
		int begin = nmea.indexOf(Sentence.FIELD_DELIMITER) + 1;
		int end = Checksum.index(nmea);
		
		String csv = nmea.substring(begin, end);
		String[] values = csv.split(String.valueOf(FIELD_DELIMITER), -1);
		
		setFieldCount(values.length);
		setStringValues(0, values);
	}
	
	public STALKParser(TalkerId ttid) {
		super(TalkerId.AG, NMEA_SENTENCE_TYPE, 1);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder(MAX_LENGTH);

		sb.append(NMEA_SENTENCE_TYPE);
		
		for (int i = 0; i<getFieldCount(); i++) {
			String field = getStringValue(i);
			sb.append(FIELD_DELIMITER);
			sb.append(field == null ? "" : field);
		}
		
		final String checksum = Checksum.xor(sb.toString());
		sb.append(CHECKSUM_DELIMITER);
		sb.append(checksum);
		sb.insert(0, getBeginChar());

		return sb.toString();
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
