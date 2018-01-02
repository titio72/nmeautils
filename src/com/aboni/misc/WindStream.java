package com.aboni.misc;

import java.io.BufferedReader;
import java.io.FileReader;

import com.aboni.nmea.sentences.NMEASentenceItem;
import com.aboni.nmea.sentences.VWRSentence;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.HDGSentence;
import net.sf.marineapi.nmea.sentence.HDMSentence;
import net.sf.marineapi.nmea.sentence.MWDSentence;
import net.sf.marineapi.nmea.sentence.MWVSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.sentence.VHWSentence;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Side;
import net.sf.marineapi.nmea.util.Units;

public class WindStream {

	public static class Conf {
		public boolean useVWR = false;
		public boolean forceCalcTrue = false;
	}
	
	private NMEATrueWind windCalc;
	
	private long lastMWV_T;
	private long lastMWD;
	
	private long lastSentMWD;
	
	private static final long CALC_THRESHOLD = 2000;
	private static final long SEND_THROTTLING = 900;

	private Conf conf;
	private TalkerId tid;
		
	public WindStream(TalkerId tid) {
		this(tid, new Conf());
	}
	
	public WindStream(TalkerId tid, Conf conf) {
		this.tid = tid;
		this.conf = conf;
		windCalc = new NMEATrueWind(tid);
		lastMWV_T = 0;
		lastMWD = 0;
	}
	
	public void setConf(Conf conf) {
		this.conf = conf;
	}
	
	public Conf getConf() {
		return conf;
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
		if (s.getSentenceId().equals(SentenceId.VWR.name())) {
			if (conf.useVWR) {
				VWRSentence vwr = (VWRSentence) s;
				MWVSentence mwv_r = (MWVSentence) SentenceFactory.getInstance().createParser(tid,  SentenceId.MWV);
				mwv_r.setAngle(vwr.getSide()==Side.STARBOARD?vwr.getAngle():(360-vwr.getAngle()));
				mwv_r.setSpeed(vwr.getSpeed());
				mwv_r.setStatus(DataStatus.ACTIVE);
				mwv_r.setSpeedUnit(Units.KNOT);
				mwv_r.setTrue(false);
				onProcSentence(mwv_r, time);
				windCalc.setWind(mwv_r, time);
				if (shallCalcMWV_T(time)) {
					windCalc.calcMWVSentence(time);
					onProcSentence(windCalc.getTrueWind(), time);
				}
				if (shallCalcMWD(time)) {
					windCalc.calcMWDSentence(time);
					onProcSentence(windCalc.getWind(), time);
				}
			}
		} else if (s.getSentenceId().equals(SentenceId.MWV.name())) {
			if (!conf.useVWR) {
				MWVSentence mwv = (MWVSentence)s;
				if (mwv.isTrue())  {
					if (!conf.forceCalcTrue) {
						lastMWV_T = time;
						windCalc.setWind(mwv, time);
						onProcSentence(s, time);
						if (shallCalcMWD(time)) {
							windCalc.calcMWDSentence(time);
							onProcSentence(windCalc.getWind(), time);
						}
					}
				} else {
					onProcSentence(s, time);
					windCalc.setWind(mwv, time);
					if (shallCalcMWV_T(time)) {
						windCalc.calcMWVSentence(time);
						onProcSentence(windCalc.getTrueWind(), time);
						if (shallCalcMWD(time)) {
							windCalc.calcMWDSentence(time);
							onProcSentence(windCalc.getWind(), time);
						}
					}
				}
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
		} else if (s.getSentenceId().equals(SentenceId.HDM.name())) {
			HDMSentence h = (HDMSentence)s;
			windCalc.setHeading(h, time);
			onProcSentence(s, time);
		} else {
			onProcSentence(s, time);
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
