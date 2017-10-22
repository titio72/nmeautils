package com.aboni.nmea.sentences;

import com.aboni.geo.Track;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.RMBSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;
import net.sf.marineapi.nmea.util.DataStatus;
import net.sf.marineapi.nmea.util.Direction;
import net.sf.marineapi.nmea.util.Waypoint;

public class TrackToRBC {

	private Track track;
	private TalkerId talkerId;
	
	public TrackToRBC(Track t, TalkerId id) {
		track = t;
		talkerId = id;
	}
	
	public RMBSentence getSentence() {
		
		RMBSentence rmb = (RMBSentence)SentenceFactory.getInstance().createParser(talkerId, SentenceId.RMB);
		rmb.setSteerTo(track.getTurn()==Track.RIGHT?Direction.RIGHT:Direction.LEFT);
		rmb.setCrossTrackError(track.getXTE());
		rmb.setArrivalStatus((track.getDistanceToDest()<0.1)?DataStatus.ACTIVE:DataStatus.VOID);
		rmb.setStatus(DataStatus.ACTIVE);
		rmb.setBearing(track.getBRG());
		rmb.setRange(track.getTotalDistance());
		rmb.setVelocity(track.getVMG());
		rmb.setOriginId("000");
		rmb.setDestination(new Waypoint("001", track.getEnd().getLatitude(), track.getEnd().getLongitude()));
		
		return rmb;
	}
	
}
