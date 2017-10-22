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

	public class Conf {
		boolean useVWR = false;
		boolean useCOG = false;
		boolean calcTrue = false;
	}
	
	private NMEATrueWind windCalc;
	
	private long lastMWV_T;
	private long lastMWD;
	private long lastMWV_R;
	private long lastVHW;
	
	private long lastSentMWD;
	
	private static final long CALC_THRESHOLD = 2000;
	private static final long SEND_THROTTLING = 900;
	
	private SpeedMovingAverage dtMWV_VHW; 
	private SpeedMovingAverage dtVHW_MWV; 
	private String updateTrueWindOnSentence = "MWV"; 
	
	public WindStream(TalkerId tid) {
		windCalc = new NMEATrueWind(tid);
		lastMWV_T = 0;
		lastMWD = 0;
		lastMWV_R = 0;
		lastVHW = 0;
		dtMWV_VHW = new SpeedMovingAverage(10*1000);
		dtVHW_MWV = new SpeedMovingAverage(10*1000);
	}

	private boolean shallCalcMWV_T(long now) {
		if (lastMWV_T==0) lastMWV_T = now;
		return (now - lastMWV_T) > CALC_THRESHOLD;
	}
	
	private boolean shallCalcMWD(long now) {
		if (lastMWD==0) lastMWD = now;
		return (now - lastMWD) > CALC_THRESHOLD && (now - lastSentMWD) > SEND_THROTTLING;
	}
	
	public void onSentence(Sentence s) {
		onSentence(s, System.currentTimeMillis());
	}
	
	public void onSentence(Sentence s, long time) {
		if (s.getSentenceId().equals(SentenceId.MWV.name())) {
			MWVSentence mwv = (MWVSentence)s;
			if (mwv.isTrue()) {
				lastMWV_T = time;
			} else {
				lastMWV_R = time;
				updateStats();
			}
			onProcSentence(s, time);
			windCalc.setWind(mwv, time);
			if (shallCalcMWV_T(time) && "MWV".equals(updateTrueWindOnSentence)) {
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
			lastVHW = time;
			updateStats();
			onProcSentence(s, time);
		} else if (s.getSentenceId().equals(SentenceId.HDG.name())) {
			HDGSentence h = (HDGSentence)s;
			windCalc.setHeading(h, time);
			onProcSentence(s, time);
		} else {
			onProcSentence(s, time);
		}
	}

	private void updateStats() {
		if (lastMWV_R > lastVHW) {
			dtVHW_MWV.setSample(lastMWV_R, lastMWV_R - lastVHW);
		} else {
			dtMWV_VHW.setSample(lastVHW, lastVHW - lastMWV_R);
		}
		if (dtMWV_VHW.getAvg()<dtMWV_VHW.getAvg()) {
			updateTrueWindOnSentence = "MWV";
		} else {
			// disable it for now
			//updateTrueWindOnSentence = "VHW";
		}
	}

	protected void onProcSentence(Sentence s, long time) {
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
