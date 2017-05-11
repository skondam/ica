package de.jkitberatung.recorder;

public class Validator {

	private static boolean validateRecorder(RecordControl recorder) {
		if (recorder.getIcaAddress().isEmpty() ||
			recorder.getUsername().isEmpty()   ||
			recorder.getPassword().isEmpty()   ||
			recorder.getInitialApp().isEmpty() ||
			recorder.getBitmapFolder().isEmpty())			
			return false;		
		return true;
	}

	public static boolean isValid(Object o) {
		if (o instanceof RecordControl)
			return validateRecorder((RecordControl) o);
		return true;
	}

}
