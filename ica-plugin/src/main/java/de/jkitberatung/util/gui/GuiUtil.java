package de.jkitberatung.util.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class GuiUtil {
	
	public static final String LABEL_MANDATORY = " *";
	public static final String LABEL_MANDATORY_EXPLAIN = "* Mandatory Field";
	public static final String LABEL_PASSWORD = "Password:";
	public static final String LABEL_PORT = "Port:";
	public static final String TOOLTIP_COMMENT = "COMMENT";
	public static final String TOOLTIP_NAME = "NAME";
	
	public static final Insets INSETS = new Insets(1,5,1,5);

	private static final int BUTTON_WIDTH = 110;
	private static final int LABEL_WIDTH = 125;
	public  static final int STRUT_HEIGHT = 15;
	private static final int TEXTFIELD_WIDTH = 280;

	
	public static Component getMandatoryPanel(JComponent c) {
		Box box = Box.createHorizontalBox();
		box.add(c);
		box.add(new JLabel(LABEL_MANDATORY));
		return box;
	}
	
	public static void setControlsSize(Component[] components, String guiGenericTitle) {
		for (Component c : components) 
			if (c instanceof JLabel && 
				!((JLabel) c).getText().equals(guiGenericTitle) &&
				!((JLabel) c).getText().equals(GuiUtil.LABEL_MANDATORY))
				setSize((JComponent) c, LABEL_WIDTH);
			else if (c instanceof JTextField)
				setSize((JComponent) c, TEXTFIELD_WIDTH);
			else if (c instanceof JButton) 
				setSize((JComponent) c, BUTTON_WIDTH);
			else 
				setControlsSize(((Container) c).getComponents(), guiGenericTitle);
	}

	private static void setSize(JComponent c, int w) {
		Dimension d = new Dimension(w, c.getPreferredSize().height);
		c.setMinimumSize(d);		
		c.setPreferredSize(d);
		c.setMaximumSize(d);
	}
	
	public static JFileChooser newFileChooser(String dialogTitle,
											  int fileSelectionMode, 
											  JTextField textField,
											  String globalLocation) {
		JFileChooser fileChooser = new JFileChooser();				
		fileChooser.setDialogTitle(dialogTitle);				
		fileChooser.setFileSelectionMode(fileSelectionMode);
		
		if (!textField.getText().trim().isEmpty()) 
			setCurrentDirectory(fileChooser, textField.getText().trim());
		else if (null != globalLocation && !globalLocation.isEmpty())
			setCurrentDirectory(fileChooser, globalLocation);
		
		return fileChooser;
	}

	public static void setCurrentDirectory(JFileChooser fileChooser, String location) {
		if (pointsToFile(location))
			location = location.substring(0,location.lastIndexOf("\\"));
		File dir = new File(location);
		if (dir.exists()) 
			fileChooser.setCurrentDirectory(dir);
	}

	private static boolean pointsToFile(String location) {
		if (location.lastIndexOf("\\") < location.lastIndexOf("."))
			return true;
		return false;
	}
	
	public static JPanel makeTitlePanel(JTextField textField) {
		textField.setEditable(false);
		JLabel jlblStepName = new JLabel("Name:");
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(jlblStepName, new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.NONE, GuiUtil.INSETS,0,0));
		panel.add(textField, new GridBagConstraints(1,0,1,1,1.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.NONE, GuiUtil.INSETS,0,0));

		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Step Properties"));
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);

		return panel ;			
	}
}
