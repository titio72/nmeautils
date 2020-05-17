package com.aboni.nmea.sentences;

import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.TalkerId;

public class NMEABasicSentenceFilter implements NMEASentenceFilter {

    private final String sentenceId;
    private final TalkerId talkerId;
    private final String source;

    public NMEABasicSentenceFilter(String sentenceId, TalkerId talkerId, String source) {
        if (sentenceId == null) sentenceId = "";
        this.sentenceId = sentenceId;
        this.talkerId = talkerId;
        this.source = source;
    }

    public NMEABasicSentenceFilter(String sentenceId, String source) {
        if (sentenceId == null) sentenceId = "";
        this.sentenceId = sentenceId;
        this.talkerId = null;
        this.source = source;
    }

    public NMEABasicSentenceFilter(String sentenceId) {
        if (sentenceId == null) sentenceId = "";
        this.sentenceId = sentenceId;
        this.talkerId = null;
        this.source = "";
    }

    public TalkerId getTalkerId() {
        return talkerId;
    }

    public String getSentenceId() {
        return sentenceId;
    }

    public String getSource() {
        return source;
    }

    private boolean isAllSentences() {
        return sentenceId.isEmpty();
    }

    private boolean isAllTalkers() {
        return talkerId == null;
    }

    private boolean isAllSources() {
        return source.isEmpty();
    }

    @Override
    public boolean match(Sentence s, String src) {
        if ((isAllSources() || getSource().equals(src)) && (isAllSentences() || getSentenceId().equals(s.getSentenceId()))) {
            return isAllTalkers() || getTalkerId().equals(s.getTalkerId());
        } else {
            return false;
        }
    }
}
