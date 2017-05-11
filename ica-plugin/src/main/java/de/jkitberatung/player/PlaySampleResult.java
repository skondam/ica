package de.jkitberatung.player;

import java.util.ArrayList;

import org.apache.jmeter.samplers.SampleResult;

public class PlaySampleResult extends SampleResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<Object> recordingHashes;
	private ArrayList<Object> playbackHashes;
	
	public PlaySampleResult() {
		recordingHashes = new ArrayList<Object>();
		playbackHashes = new ArrayList<Object>();
	}

	public ArrayList<Object> getRecordingHashes() {
		return recordingHashes;
	}

	public ArrayList<Object> getPlaybackHashes() {
		return playbackHashes;
	}

	public void addHash(Object hash, boolean recordingHash) {
		if (recordingHash)
			recordingHashes.add(hash);
		else
			playbackHashes.add(hash);
	}
}
