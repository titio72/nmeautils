package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.Sentence;

import java.util.StringTokenizer;

public class NMEASentenceItem {

	private Sentence sentence;
	private final String sentenceString;
	private long timestamp;
	private String data;
	
	@Override
	public String toString() {
		return "[" + timestamp + "][" + data + "] " + sentenceString;
	}

	public NMEASentenceItem(Sentence sentence, long timestamp, String data) {
		this.sentence = sentence;
		this.timestamp = timestamp;
		this.data = data;
		this.sentenceString = sentence.toSentence();
		
	}
	
	public NMEASentenceItem(String line) {
		StringTokenizer tkz = new StringTokenizer(line, "]");
		String sT = tkz.nextToken().substring(1);
		String sD = tkz.nextToken().substring(1);
		String sS = tkz.nextToken().substring(1);
		sentenceString = sS.trim();
		try {
			sentence = SentenceFactory.getInstance().createParser(sentenceString);
		} catch (Exception e) {
			sentence = null;
		}
		timestamp = Long.parseLong(sT);
		data = sD;
	}
	
	public Sentence getSentence() {
		return sentence;
	}
	
	public void setSentence(Sentence sentence) {
		this.sentence = sentence;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public String getString() {
		return sentenceString;
	}
}
