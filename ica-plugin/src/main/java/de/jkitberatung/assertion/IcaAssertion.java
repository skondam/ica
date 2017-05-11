package de.jkitberatung.assertion;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.jmeter.assertions.Assertion;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;

import de.jkitberatung.player.PlaySampleResult;


public class IcaAssertion extends AbstractTestElement implements Serializable, Assertion {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2490090929370818008L;

	public AssertionResult getResult(SampleResult sampleResult) {
		AssertionResult result = new AssertionResult(getName());
		result.setFailure(false);
		result.setError(false);
		sampleResult.setSuccessful(true);
		
		PlaySampleResult playSamplerResult = (PlaySampleResult) sampleResult;		
		ArrayList<Object> playbackHashes = playSamplerResult.getPlaybackHashes();
		ArrayList<Object> recordingHashes = playSamplerResult.getRecordingHashes();
		
		if (playbackHashes.size() != recordingHashes.size()) {
			result.setFailure(true);
			result.setError(true);
			result.setFailureMessage("Different number of hashes between recording and playback");
		} else 
			for (int index=0; index<playbackHashes.size(); index++) 
				if (!playbackHashes.get(index).toString().equals(recordingHashes.get(index).toString())) {
					result.setFailure(true);
					result.setError(true);
					result.setFailureMessage("Distinct hashes");
					break;
				}
		
		return result;
	}
}
