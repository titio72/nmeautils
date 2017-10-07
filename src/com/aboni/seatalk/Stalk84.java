package com.aboni.seatalk;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import net.sf.marineapi.nmea.sentence.STALKSentence;


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
                    negative values steer left. Example: 0xFE = 2° left) 
                  SS & 0x01 : when set, turns off heading display on 600R control. 
                  SS & 0x02 : always on with 400G 
                  SS & 0x08 : displays “NO DATA” on 600R 
                  SS & 0x10 : displays “LARGE XTE” on 600R 
                  SS & 0x80 : Displays “Auto Rel” on 600R 
                  TT : Always 0x08 on 400G computer, always 0x05 on 150(G) computer 



 */

public class Stalk84 {

	private int heading;
	private int rudder;
	private int autoDeg;
	private boolean auto;
	private boolean wind;
	private boolean track;
	private boolean err_off_course;
	private boolean err_wind_shift;

	public Stalk84(STALKSentence s) {
		if ("84".equals(s.getCommand())) {
			String[] data = new String[9];
			data[0] = "84";
			for (int i=0; i<s.getParameters().length; i++) data[i+1] = s.getParameters()[i];
			calc(data);
		} else {
			throw new RuntimeException("Type is not 84");
		}
	}
	
	public Stalk84(String...d) {
		String[] data = new String[9];
		for (int i=0; i<d.length; i++) data[i] = d[i];
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
		return auto;
	}

	public boolean isWind() {
		return wind;
	}

	public boolean isTrack() {
		return track;
	}

	public boolean isErr_off_course() {
		return err_off_course;
	}

	public boolean isErr_wind_shift() {
		return err_wind_shift;
	}

	public int getAutoDeg() {
		return autoDeg;
	}
	
	private void calc(String[] data) {
		byte u = (byte)Integer.parseInt(data[1].substring(0, 1), 16);
		int v = Integer.parseInt(data[2].substring(0, 1), 16);
		byte w = (byte)Integer.parseInt(data[2].substring(1, 2), 16);
		byte vw = (byte)(v * 256 + w);
		int xy = Integer.parseInt(data[3], 16);
		byte z = (byte)Integer.parseInt(data[4].substring(1, 2), 16);
		byte m = (byte)Integer.parseInt(data[5].substring(1, 2), 16);
		byte rr = (byte)Integer.parseInt(data[6], 16);

		auto = (z & (byte)0x02)==2;
		wind = (z & (byte)0x04)==4;
		track = (z & (byte)0x08)==8;
		err_off_course = (m & (byte)0x04)==4;
		err_wind_shift = (m & (byte)0x08)==8;
		rudder = rr;
		
		if (auto) {
			autoDeg = xy/2 + ((v & 0xC) >> 2) * 90;
		}
		heading = (vw & 63) * 2 + (u & 0x03) * 90;
	}

	public void dump(OutputStream p) throws IOException {
		String r = "---------------------\r\n";
		r += "Head  " + heading + "\r\n";
		r += "Auto  " + auto + (auto?(" [" + autoDeg + "]"):"") + "\r\n";
		r += "Wind  " + wind + "\r\n";
		r += "Track " + track + "\r\n";
		r += "Off Track  " + err_off_course + "\r\n";
		r += "Wind Shift " + err_wind_shift + "\r\n";
		r += "Rudder " + rudder + "\r\n";
		p.write(r.getBytes());
	}
	
	@Override
	public String toString() {
		String r = "Stalk {84}";
		r += " Head {" + heading + "}";
		r += " Rudder {" + rudder + "}";
		r += " Auto {" + auto + "}";
		r += " AutoDeg {" + autoDeg + "}";
		r += " Wind {" + wind + "}";
		r += " Track {" + track + "}";
		r += " OffTrack {" + err_off_course + "}";
		r += " WindShift {" + err_wind_shift + "}";
		return r;
	}
}