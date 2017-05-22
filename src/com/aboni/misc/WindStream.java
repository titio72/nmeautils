package com.aboni.misc;

import java.io.BufferedReader;
import java.io.FileReader;

import com.aboni.nmea.sentences.NMEASentenceItem;

import net.sf.marineapi.nmea.sentence.HDGSentence;
import net.sf.marineapi.nmea.sentence.MWDSentence;
import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.sentence.VHWSentence;

public class WindStream {

	private NMEATrueWind windCalc;
	
	private long lastMWV_T;
	private long lastMWD;

	private long lastSentMWD;
	
	private static final long THRESHOLD = 2000;
	private static final long SEND_THROTTLING = 900;
	
	public WindStream(TalkerId tid) {
		windCalc = new NMEATrueWind(tid);
		lastMWV_T = 0;
		lastMWD = 0;
	}

	private boolean shallCalcMWV_T(long now) {
		if (lastMWV_T==0) lastMWV_T = now;
		return (now - lastMWV_T) > THRESHOLD;
	}
	
	private boolean shallCalcMWD(long now) {
		if (lastMWD==0) lastMWD = now;
		return (now - lastMWD) > THRESHOLD && (now - lastSentMWD) > SEND_THROTTLING;
	}
	
	public void onSentence(Sentence s) {
		onSentence(s, System.currentTimeMillis());
	}
	
	public void onSentence(Sentence s, long time) {
		if (s.getSentenceId().equals(SentenceId.MWV.name())) {
			MWVSentence mwv = (MWVSentence)s;
			if (mwv.isTrue()) {
				lastMWV_T = time;
			}
			onProcSentence(s, time);
			windCalc.setWind(mwv, time);
			if (shallCalcMWV_T(time)) {
				windCalc.calcMWVSentence(time);
				onProcSentence(windCalc.getTrueWind(), time);
			}
			if (shallCalcMWD(time)) {
				windCalc.calcMWDSentence(time);
				onProcSentence(windCalc.getWind(), time);
			}
		} else if (s.getSentenceId().equals(SentenceId.MWD.name())) {
			MWDSentence mwd = (MWDSentence)s;
			lastMWD = time;
			windCalc.setWind(mwd, time);
			onProcSentence(s, time);
		} else if (s.getSentenceId().equals(SentenceId.VHW.name())) {
			VHWSentence vhw = (VHWSentence)s;
			windCalc.setSpeed(vhw, time);
			onProcSentence(s, time);
		} else if (s.getSentenceId().equals(SentenceId.HDG.name())) {
			HDGSentence h = (HDGSentence)s;
			windCalc.setHeading(h, time);
			onProcSentence(s, time);
		}
	}

	private void onProcSentence(Sentence s, long time) {
		if (s!=null) {
			System.out.println(time + " " + s.toSentence());
		}
	}
	
	
	public static void main(String[] args) {
		try {
			WindStream s = new WindStream(TalkerId.II);
			FileReader r = new FileReader("petter.log");
			BufferedReader br = new BufferedReader(r);
			String line;
			while ((line = br.readLine())!=null) {
				NMEASentenceItem itm = new NMEASentenceItem(line);
				s.onSentence(itm.getSentence(), itm.getTimestamp());
			}
			r.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
}
