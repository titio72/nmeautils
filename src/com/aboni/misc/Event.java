package com.aboni.misc;

public class Event<C> {

	C event;
	long timestamp;
	
	Event(C sentence, long t) {
		timestamp = t;
		event = sentence;
	}
	
	long getAge(long now) {
		if (event!=null) {
			return now - timestamp;
		} else {
			return -1;
		}
	}

	void setEvent(C e, long time) {
		event = e;
		timestamp = time;
	}
}
