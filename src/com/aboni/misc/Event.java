package com.aboni.misc;

public class Event<C> {

	C ev;
	long timestamp;
	
	Event(C sentence, long t) {
		timestamp = t;
		ev = sentence;
	}
	
	long getAge(long now) {
		if (ev!=null) {
			return now - timestamp;
		} else {
			return -1;
		}
	}

	void setEvent(C e, long time) {
		ev = e;
		timestamp = time;
	}
}
