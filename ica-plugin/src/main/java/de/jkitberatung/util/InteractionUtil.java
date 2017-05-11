package de.jkitberatung.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jmeter.util.JMeterUtils;

import de.jkitberatung.recorder.Interaction;
import de.jkitberatung.recorder.RecordingStep;
import de.jkitberatung.recorder.StringInteraction;

public class InteractionUtil {

	private static final String SEPARATOR = ":";
	private static final Object DEFAULT_SLEEPING_TIME = "75";

	public static List<RecordingStep> readInteractions(String path) throws IOException {

		List<RecordingStep> steps = new ArrayList<RecordingStep>();

		//reading interactions from recordings file
		BufferedReader br = null;
		File file = new File(path);

		if (!file.exists()) {
			JMeterUtils.reportErrorToUser("Recording file not found");
			return steps;
		}
		
		br = new BufferedReader(new FileReader(file));

		String line = "";
		StringInteraction stringInteraction = null;
		boolean isStringInteraction = false;
		RecordingStep step = null;

		while ((line = br.readLine()) != null) {
			//System.out.println("Read line:" + line);
			if (line.equals(""))
				continue;

			String[] array = getPairArray(line);
			String key = "";
			String value = "";
			if (array.length > 1) {
				key = array[0];
				value = array[1];
			} else if (array.length == 1)
				key = array[0];
			
			try {
				if (key.equals(RecordingStep.STEP_KEY)) {
					step = new RecordingStep(value, false);
					steps.add(step);

				} else if (Interaction.Label.valueOf(key).equals(Interaction.Label.StringTagStart)) {
					//System.out.println("IS STRING INTERACTION START");
					isStringInteraction = true;
					stringInteraction = new StringInteraction(Interaction.Label.StringTag, value);
					stringInteraction.setName(value);
					step.addInteraction(stringInteraction);

				} else if (Interaction.Label.valueOf(key).equals(Interaction.Label.StringTagEnd)) {
					//System.out.println("IS STRING INTERACTION END");
					isStringInteraction = false;

				} else {
					if (isStringInteraction) {
						//System.out.println("Add string sub interaction:" + key);
						stringInteraction.addInteraction(new Interaction(Interaction.Label.valueOf(key), value));
					} else
						step.addInteraction(new Interaction(Interaction.Label.valueOf(key), value));
				}

			} catch (Exception e) {
				// Ignore unknown steps
				//System.out.println("Error reading: " + e.getMessage());
				e.printStackTrace();
				continue;
			}
		}

		br.close();

		//set as first
		steps.get(0).setFirst(true);
		//ALT+F4 to close the window (+Tab+Enter in case the Save As dialog pops-up)
		RecordingStep lastStep = steps.get(steps.size()-1);
		setExitStep(lastStep);
		//set as last
		lastStep.setLast(true);

		return steps;
	}

	private static void setExitStep(RecordingStep recordingStep) {
		recordingStep.addInteraction(new Interaction(Interaction.Label.SleepTime, DEFAULT_SLEEPING_TIME));
		recordingStep.addInteraction(new Interaction(Interaction.Label.KeyDown,   "18"));//ALT down
		recordingStep.addInteraction(new Interaction(Interaction.Label.SleepTime, DEFAULT_SLEEPING_TIME));
		recordingStep.addInteraction(new Interaction(Interaction.Label.KeyDown,   "115"));//F4 down
		recordingStep.addInteraction(new Interaction(Interaction.Label.SleepTime, DEFAULT_SLEEPING_TIME));
		recordingStep.addInteraction(new Interaction(Interaction.Label.KeyUp,     "115"));//F4 up
		recordingStep.addInteraction(new Interaction(Interaction.Label.SleepTime, DEFAULT_SLEEPING_TIME));
		recordingStep.addInteraction(new Interaction(Interaction.Label.KeyUp,     "18"));//ALT up
		recordingStep.addInteraction(new Interaction(Interaction.Label.SleepTime, DEFAULT_SLEEPING_TIME));
		recordingStep.addInteraction(new Interaction(Interaction.Label.KeyDown,   "9"));//Tab down
		recordingStep.addInteraction(new Interaction(Interaction.Label.SleepTime, DEFAULT_SLEEPING_TIME));
		recordingStep.addInteraction(new Interaction(Interaction.Label.KeyUp,     "9"));//Tab up
		recordingStep.addInteraction(new Interaction(Interaction.Label.SleepTime, DEFAULT_SLEEPING_TIME));
		recordingStep.addInteraction(new Interaction(Interaction.Label.KeyDown,   "13"));//Enter down
		recordingStep.addInteraction(new Interaction(Interaction.Label.SleepTime, DEFAULT_SLEEPING_TIME));
		recordingStep.addInteraction(new Interaction(Interaction.Label.KeyUp,     "13"));//Enter up
		recordingStep.addInteraction(new Interaction(Interaction.Label.SleepTime, DEFAULT_SLEEPING_TIME));
	}
/*
	private static void setMaximizeStep(RecordingStep recordingStep) {
		recordingStep.addInteraction(new Interaction(Interaction.Label.SleepTime, "6000"));
		recordingStep.addInteraction(new Interaction(Interaction.Label.KeyDown,   "18"));//ALT down
		recordingStep.addInteraction(new Interaction(Interaction.Label.SleepTime, DEFAULT_SLEEPING_TIME));
		recordingStep.addInteraction(new Interaction(Interaction.Label.KeyDown,   "32"));//Space down
		recordingStep.addInteraction(new Interaction(Interaction.Label.SleepTime, DEFAULT_SLEEPING_TIME));
		recordingStep.addInteraction(new Interaction(Interaction.Label.KeyUp,     "32"));//Space up
		recordingStep.addInteraction(new Interaction(Interaction.Label.SleepTime, DEFAULT_SLEEPING_TIME));
		recordingStep.addInteraction(new Interaction(Interaction.Label.KeyDown,   "88"));//x down
		recordingStep.addInteraction(new Interaction(Interaction.Label.SleepTime, DEFAULT_SLEEPING_TIME));
		recordingStep.addInteraction(new Interaction(Interaction.Label.KeyUp,     "88"));//x up
		recordingStep.addInteraction(new Interaction(Interaction.Label.SleepTime, DEFAULT_SLEEPING_TIME));
		recordingStep.addInteraction(new Interaction(Interaction.Label.KeyUp,     "18"));//ALT up
		recordingStep.addInteraction(new Interaction(Interaction.Label.SleepTime, DEFAULT_SLEEPING_TIME));
	}
*/
	private static String[] getPairArray(String string) {

		return string.split(SEPARATOR);
	}

	public static String listToStr(List<Interaction> interactions) {
		String str = "";
		for (Interaction interaction : interactions) 
			if (interaction.getLabel().equals(Interaction.Label.KeyUp)) 
				str += (char) Integer.valueOf(interaction.getValue().toString()).intValue();
		return str;
	}
}
