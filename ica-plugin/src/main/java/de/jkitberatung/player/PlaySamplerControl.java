package de.jkitberatung.player;

import java.util.logging.Logger;

import org.apache.jmeter.control.GenericController;

import de.jkitberatung.util.IcaConnector;


public class PlaySamplerControl extends GenericController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4000209595000276037L;

	private static final String ADDRESS = "PlaySamplerControl.ica_address";
	private static final String USERNAME = "PlaySamplerControl.username";
	private static final String PASSWORD = "PlaySamplerControl.password";
	private static final String INITIAL_APP = "PlaySamplerControl.initial_app";
	private static final String DOMAIN = "PlaySamplerControl.domain";
	private static final String PORT = "PlaySamplerControl.port";
	private static final String INTERACTION_PATH = "PlaySamplerControl.interactions_path";
	private static final String SCREENSHOTS_PATH = "PlaySamplerControl.screenshots_path";
	private static final String SCREENSHOTS_FOLDER_PATH = "PlaySamplerControl.screenshots_folder_path";
	private static final String SLEEP_FACTOR = "PlaySamplerControl.sleep_factor";
	private static final String USE_SLEEP_TIMES = "PlaySamplerControl.use_sleep_times";
	private static final String RUNNING_MODE = "PlaySamplerControl.running_mode";
	
	private IcaConnector icaConnector;
	
	private static final Logger L =Logger.getLogger(PlaySamplerControl.class.getName());

	public void setDomain(String domain) {
		setProperty(DOMAIN, domain);
	}

	public String getDomain() {
		return getPropertyAsString(DOMAIN);
	}

	public void setPort(String port) {
		setProperty(PORT, port);
	}

	public String getPort() {
		return getPropertyAsString(PORT);
	}

	public void setPassword(String password) {
		setProperty(PASSWORD, password);
	}

	public String getPassword() {
		return getPropertyAsString(PASSWORD);
	}

	public void setUsername(String username) {
		setProperty(USERNAME, username);
	}

	public String getUsername() {
		return getPropertyAsString(USERNAME);
	}

	public void setIcaAddress(String icaAddress) {
		setProperty(ADDRESS, icaAddress);
	}

	public String getIcaAddress() {		
		return getPropertyAsString(ADDRESS);
	}

	public void setInitialApp(String initialApp) {
		setProperty(INITIAL_APP, initialApp);
	}

	public String getInitialApp() {
		return getPropertyAsString(INITIAL_APP);
	}

	public void setInteractionPath(String path) {
		setProperty(INTERACTION_PATH, path);
	}
	
	public String getInteractionsPath() {
		return getPropertyAsString(INTERACTION_PATH);
	}
	
	public void setScreenshotsHashesFilePath(String path) {
		setProperty(SCREENSHOTS_PATH, path);
	}
	
	public String getScreenshotsHashesFilePath() {
		return getPropertyAsString(SCREENSHOTS_PATH);
	}
	
	public void setScreenshotsForderPath(String path) {
		setProperty(SCREENSHOTS_FOLDER_PATH, path);
	}
	
	public String getScreenshotsFolderPath() {
		return getPropertyAsString(SCREENSHOTS_FOLDER_PATH);
	}	

	public void setSleepFactor(String sleepFactor) {
		setProperty(SLEEP_FACTOR, sleepFactor);
	}

	public String getSleepFactor() {
		return getPropertyAsString(SLEEP_FACTOR);
	}	

	public void setSleepTimes(boolean checkBoxSelected) {
		setProperty(USE_SLEEP_TIMES, checkBoxSelected);
	}

	public boolean useSleepTimes() {
		return getPropertyAsBoolean(USE_SLEEP_TIMES);
	}

    @Override
    public Object clone() {
    	
    	Object clone = super.clone();
   	
    	IcaConnector icaConnector = new IcaConnector();
		icaConnector.setAddress(getIcaAddress());
		icaConnector.setDomain(getDomain());
		icaConnector.setPort(getPort());
		icaConnector.setUsername(getUsername());
		icaConnector.setPassword(getPassword());
		icaConnector.setApp(getInitialApp());
		icaConnector.setRunningMode(getRunningMode());
    	((PlaySamplerControl) clone).setIcaConnector(icaConnector);
    	
    	return clone;
    }

	public void setIcaConnector(IcaConnector icaConnector) {
		this.icaConnector = icaConnector;
	}

	public IcaConnector getIcaConnector() {
		return icaConnector;
	}

	public void setRunningMode(String mode) {
		setProperty(RUNNING_MODE, mode);
	}

	public String getRunningMode() {
		return getPropertyAsString(RUNNING_MODE);
	}
}
