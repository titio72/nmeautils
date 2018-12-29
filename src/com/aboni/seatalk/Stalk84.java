package com.aboni.seatalk;

import com.aboni.misc.Utils;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.Checksum;
import net.sf.marineapi.nmea.sentence.STALKSentence;

import java.io.IOException;
import java.io.OutputStream;


/*

 84  U6  VW  XY 0Z 0M RR SS TT  Compass heading  Autopilot course and 
                  Rudder position (see also command 9C) 
                  Compass heading in degrees: 
                    The two lower  bits of  U * 90 + 
                    the six lower  bits of VW *  2 + 
                    number of bits set in the two higher bits of U = 
                    (U & 0x3)* 90 + (VW & 0x3F)* 2 + (U & 0xC ? (U & 0xC == 0xC ? 2 : 1): 0) 
                  Turning direction: 
                    Most significant bit of U = 1: Increasing heading, Ship turns right 
                    Most significant bit of U = 0: Decreasing heading, Ship turns left 
                  Autopilot course in degrees: 
                    The two higher bits of  V * 90 + XY / 2 
                  Z & 0x2 = 0 : Autopilot in Standby-Mode 
                  Z & 0x2 = 2 : Autopilot in Auto-Mode 
                  Z & 0x4 = 4 : Autopilot in Vane Mode (WindTrim), requires regular "10" datagrams 
                  Z & 0x8 = 8 : Autopilot in Track Mode
                  M: Alarms + audible beeps 
                    M & 0x04 = 4 : Off course 
                    M & 0x08 = 8 : Wind Shift
                  Rudder position: RR degrees (positive values steer right, 
                    negative values steer left. Example: 0xFE = 2� left) 
                  SS & 0x01 : when set, turns off heading display on 600R control. 
                  SS & 0x02 : always on with 400G 
                  SS & 0x08 : displays �NO DATA� on 600R 
                  SS & 0x10 : displays �LARGE XTE� on 600R 
                  SS & 0x80 : Displays �Auto Rel� on 600R 
                  TT : Always 0x08 on 400G computer, always 0x05 on 150(G) computer 



 */

@SuppressWarnings("unused")
public class Stalk84 {

	public enum TURN {
		PORT(0),
		STARBOARD(1);
		
		final int value;
		TURN(int v) {value = v;}
	}
	
	public enum STATUS {
		STATUS_STANDBY(0x0), 
		STATUS_AUTO(0x2),
		STATUS_WINDVANE(0x4), 
		STATUS_TRACK(0x8); 

		final int value;
		STATUS(int v) {value = v;}
		static STATUS fromValue(int i) {
			switch (i) {
			case 0x0: return STATUS_STANDBY; 
			case 0x2: return STATUS_AUTO; 
			case 0x4: return STATUS_WINDVANE; 
			case 0x8: return STATUS_TRACK;
			default: throw new UnsupportedOperationException();
			}
		}
	}
	
	public enum ERROR {
		ERROR_NONE(0x0),
		ERROR_GENERIC(0x2),
		ERROR_OFF_COURSE(0x4),
		ERROR_WIND_SHIFT(0x8);		

		final int value;
		ERROR(int v) {value = v;}
		static ERROR fromValue(int i) {
			switch (i) {
			case 0x0: return ERROR_NONE; 
			case 0x2: return ERROR_GENERIC; 
			case 0x4: return ERROR.ERROR_OFF_COURSE; 
			case 0x8: return ERROR_WIND_SHIFT;
			default: throw new UnsupportedOperationException();
			}
		}
	}
	
	private int heading;
	private int rudder;
	private int autoDeg;
	private int status; 
	private int error;
	private TURN turning;
	private String sentence;
	
	public Stalk84(int heading, int headingAuto, int rudder, STATUS status, ERROR error, TURN turn) {
		this.heading = (int)Utils.normalizeDegrees0_360(heading);
		this.autoDeg = (int)Utils.normalizeDegrees0_360(headingAuto);
		this.rudder = (int)Utils.normalizeDegrees180_180(rudder);
		this.error = error.value;
		this.status = status.value;
		this.turning = turn;
		calcSentence();
	}
	
	private void calcSentence() {
		//U6  VW  XY 0Z 0M RR SS TT
		
		int rr = rudder & 0xFF;
		int ss = 0x2;
		int tt = 0x6;
		int m = error;
		int z = status;
		
		int vH = (autoDeg/90)*4;
		int xy = (autoDeg % 90)*2;
		
		int uL = (heading/90);
		int vwL = (heading%90)/2;
		
		int vw = (vwL | (vH * 16)) & 0xFF;
		int u = uL + (0x8) * turning.value;

		String res = "$STALK,84";
		res += "," + Integer.toHexString(u) + "6";
		res += "," + String.format("%02x", vw);
		res += "," + String.format("%02x", xy);
		res += "," + "4" + Integer.toHexString(z);
		res += "," + "0" + Integer.toHexString(m);
		res += "," + String.format("%02x", rr);
		res += "," + String.format("%02x", ss);
		res += "," + String.format("%02x", tt);
		res = res.toUpperCase();	
		
		sentence = res + "*" + Checksum.calculate(res);
	}

	public static Stalk84 parse(String sentence) {
		STALKSentence s = (STALKSentence) SentenceFactory.getInstance().createParser(sentence);
		return new Stalk84(s);
	}
	
	public Stalk84(STALKSentence s) {
		if ("84".equals(s.getCommand())) {
			String[] data = new String[9];
			data[0] = "84";
			for (int i=0; i<s.getParameters().length; i++) data[i+1] = s.getParameters()[i];
			calc(data);
			sentence = s.toSentence();
		} else {
			throw new RuntimeException("Type is not 84");
		}
	}
	
	public Stalk84(String...d) {
		String[] data = new String[9];
		StringBuilder b = new StringBuilder("$STALK,");
		for (int i=0; i<d.length; i++) {
			data[i] = d[i];
			b.append(",").append(d[i]);
		}
		sentence = b.toString();
		if (!data[0].equals("84")) throw new RuntimeException("Type is not 84");
		calc(data);
	}
	
	public int getHeading() {
		return heading;
	}

	public int getRudder() {
		return rudder;
	}

	public boolean isAuto() {
		return (status & STATUS.STATUS_AUTO.value)==STATUS.STATUS_AUTO.value;
	}

	public boolean isWind() {
		return (status & STATUS.STATUS_WINDVANE.value)==STATUS.STATUS_WINDVANE.value;
	}

	public boolean isTrack() {
		return (status & STATUS.STATUS_TRACK.value)==STATUS.STATUS_TRACK.value;
	}

	public boolean isErr_off_course() {
		return (error & ERROR.ERROR_OFF_COURSE.value)==ERROR.ERROR_OFF_COURSE.value;
	}

	public boolean isErr_wind_shift() {
		return (error & ERROR.ERROR_WIND_SHIFT.value)==ERROR.ERROR_WIND_SHIFT.value;
	}

	public int getAutoDeg() {
		return autoDeg;
	}

	public TURN getTurning() {
		return turning;
	}
	
	public STATUS getStatus() {
		return STATUS.fromValue(status);
	}
	
	public ERROR getError() {
		return ERROR.fromValue(error);
	}
	
	public String getSTALKSentence() {
		return sentence;
	}
	
	private void calc(String[] data) {
		int u = Integer.parseInt(data[1].substring(0, 1), 16);
		int v = Integer.parseInt(data[2].substring(0, 1), 16);
		int vw = Integer.parseInt(data[2], 16);
		int xy = Integer.parseInt(data[3], 16);
		int z = Integer.parseInt(data[4].substring(1, 2), 16);
		byte m = (byte)Integer.parseInt(data[5].substring(1, 2), 16);
		byte rr = (byte)Integer.parseInt(data[6], 16);

		status = z & 0xF;
		error = m & 0xF;

		rudder = rr;
		
		if (isAuto() || isWind()) {
			autoDeg = xy/2 + ((v & 0xC) >> 2) * 90;
		}
		heading = (u & 0x3) * 90 + (vw & 0x3F)* 2 + ( ((u & 0xC) != 0) ? ( ((u & 0xC) == 0xC) ? 2 : 1): 0); 

        //Most significant bit of U = 1: Increasing heading, Ship turns right 
        //Most significant bit of U = 0: Decreasing heading, Ship turns left
		turning = ((u & 0x8) == 0) ? TURN.PORT : TURN.STARBOARD;
	}

	public void dump(OutputStream p) throws IOException {
		String r = "---------------------\r\n";
		r += "Head  " + heading + "\r\n";
		r += "Auto  " + isAuto() + (isAuto()?(" [" + autoDeg + "]"):"") + "\r\n";
		r += "Wind  " + isWind() + "\r\n";
		r += "Track " + isTrack() + "\r\n";
		r += "Off Track  " + isErr_off_course() + "\r\n";
		r += "Wind Shift " + isErr_wind_shift() + "\r\n";
		r += "Rudder " + rudder + "\r\n";
		p.write(r.getBytes());
	}
	
	@Override
	public String toString() {
		String r = "Stalk {84}";
		r += " Head {" + heading + "}";
		r += " Rudder {" + rudder + "}";
		r += " Auto {" + isAuto() + "}";
		r += " AutoDeg {" + autoDeg + "}";
		r += " Wind {" + isWind() + "}";
		r += " Track {" + isTrack() + "}";
		r += " OffTrack {" + isErr_off_course() + "}";
		r += " WindShift {" + isErr_wind_shift() + "}";
		r += " Sentence {" + sentence + "}";
		return r;
	}
}