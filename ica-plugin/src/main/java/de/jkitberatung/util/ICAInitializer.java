package de.jkitberatung.util;

import java.util.logging.Logger;

import de.jkitberatung.ica.wsh.ClassFactory;
import de.jkitberatung.ica.wsh.ICAColorDepth;
import de.jkitberatung.ica.wsh.IICAClient;
import de.jkitberatung.ica.wsh.OutputMode;

public class ICAInitializer {

	private static final Logger L = Logger.getLogger(ICAInitializer.class.getName());
	private static String PASSWORD_CITRIX = "Password";
	
	/**
	 * Create ICA client and connect to the server
	 * @param initialApp 
	 * @param icaAddress 
	 * @param port 
	 * @param domain 
	 * @param username 
	 * @param password 
	 * @param runningMode 
	 */
	public static IICAClient initICASession(String initialApp, String icaAddress, String port, 
											String domain, String username, String password, 
											String runningMode) {

		L.fine("Initializing ICA Session... " + icaAddress + "|" + username + "|" + password);
		
		IICAClient ica = ClassFactory.createICAClient();

		// For a desktop session ise ica.Application = "";
		String initApp = initialApp;
//		ica.application(initApp.startsWith("#") ? initApp : "#"+initApp);
		ica.initialProgram(initApp.startsWith("#") ? initApp : "#"+initApp);

		// Launch a new session
		ica.launch(true);

		// Set Server address
		ica.address(icaAddress);

		if (port != null && port.length() > 0)
			ica.icaPortNumber(Integer.valueOf(port));

		if (domain != null && domain.length() > 0)
			ica.domain(domain);

		ica.username(username);

		ica.setProp(PASSWORD_CITRIX, password);

		ica.desiredColor(ICAColorDepth.Color24Bit);

		if (runningMode.equals("NORMAL"))
			ica.outputMode(OutputMode.OutputModeNormal);
		else
			ica.outputMode(OutputMode.OutputModeWindowless);

		return ica;
	}
}
