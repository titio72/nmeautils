package com.aboni.seatalk;

import java.io.PrintWriter;

import net.sf.marineapi.nmea.sentence.STALKSentence;

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
		byte v = (byte)Integer.parseInt(data[2].substring(0, 1), 16);
		byte w = (byte)Integer.parseInt(data[2].substring(1, 2), 16);
		byte vw = (byte)(v * 256 + w);
		byte xy = (byte)Integer.parseInt(data[3], 16);
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
			autoDeg = xy/2 + (v & 0x60) * 90;
		}
		heading = (vw & 63) * 2 + (u & 0x03) * 90;
	}

	public void dump(PrintWriter p) {
		String r = "---------------------\r\n";
		r += "Head  " + heading + "\r\n";
		r += "Auto  " + auto + (auto?(" [" + autoDeg + "]"):"") + "\r\n";
		r += "Wind  " + wind + "\r\n";
		r += "Track " + track + "\r\n";
		r += "Off Track  " + err_off_course + "\r\n";
		r += "Wind Shift " + err_wind_shift + "\r\n";
		r += "Rudder " + rudder + "\r\n";
		p.print(r);
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