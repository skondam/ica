package de.jkitberatung.player.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.tree.TreePath;

import org.apache.jmeter.control.gui.AbstractControllerGui;
import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.JMeterGUIComponent;
import org.apache.jmeter.gui.tree.JMeterTreeModel;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.gui.util.PowerTableModel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;


import de.jkitberatung.assertion.IcaAssertion;
import de.jkitberatung.assertion.gui.IcaAssertionGui;
import de.jkitberatung.player.PlaySampler;
import de.jkitberatung.player.PlaySamplerControl;
import de.jkitberatung.recorder.Interaction;
import de.jkitberatung.recorder.RecordingStep;
import de.jkitberatung.recorder.StringInteraction;
import de.jkitberatung.util.IcaConnector;
import de.jkitberatung.util.InteractionUtil;
import de.jkitberatung.util.gui.CitrixPanel;
import de.jkitberatung.util.gui.GuiUtil;



public class PlaySamplerControlGui extends AbstractControllerGui 
								implements JMeterGUIComponent/*, TableModelListener*/ {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2775000194731817750L;
	
	private static final String CHOOSE_INTERACTIONS_FILE = "Choose interaction file";
	private static final String CHOOSE_SCREENSHOTS_FILE = "Screenshots hashes file";
	private static final String CHOOSE_SCREENSHOTS_FOLDER = "Select Screenshots Folder";
	private static final Class<?>[] COLUMN_CLASSES = {String.class,String.class,
													  String.class,String.class};
	public static final String DEFAULT_SLEEP_FACTOR = "1 (as recorded)";
	private static final String LABEL_SLEEP = "Play sleeping times multiplied with: ";
	private static final String[] SLEEP_FACTORS = {"0.5",DEFAULT_SLEEP_FACTOR,"1.5","2"};
	private static final String STATIC_LABEL = "Citrix ICA Player";
	private static final String[] TABLE_HEADER = {"Step", "Tag", "Raw Interaction", "Custom"};
	private static final String TITLE_LOCAL_SETTINGS_PANEL = "Local Settings";

	private static final int STEPS_PANE_HEIGHT = 210;

	private CitrixPanel citrixPanel;
	private String globalLocation;
	private PlaySamplerControl samplerController;
	private LinkedHashMap<String, LinkedHashMap<String, String>> tagsMap;// (step, (tag, user input))
	
	private StepsTableModel tableModel;
	
	private JTextField jtfInteractionsFile;
	private JTextField jtfScreenshotsFile;
	private JTextField jtfScreenshotsFolder;

	private JComboBox jcbSleepFactor;
	private JCheckBox jckbPlaySleep;

	private boolean stepsTableUpdated;

	private List<RecordingStep> steps;

	private JRadioButton jrbNormalMode;

	private JRadioButton jrbWindowlessMode;


	public PlaySamplerControlGui() {
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		setBorder(makeBorder());
		
		Box box = Box.createVerticalBox();
		box.add(makeCitrixPanel());        
        box.add(Box.createVerticalStrut(GuiUtil.STRUT_HEIGHT));
		box.add(makeLocalSettingsPanel());
        box.add(Box.createVerticalStrut(GuiUtil.STRUT_HEIGHT));
		box.add(makeStepsPanel());
		box.add(new JLabel(GuiUtil.LABEL_MANDATORY_EXPLAIN));
		box.setAlignmentX(Component.LEFT_ALIGNMENT);		
		
		add(box,BorderLayout.NORTH);

		GuiUtil.setControlsSize(box.getComponents(), getName());
	}

	private Component makeCitrixPanel() {
		citrixPanel = new CitrixPanel(getName(), new de.jkitberatung.util.KeyListener(this));
		return citrixPanel;
	}
	
	private Component makeStepsPanel() {		
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Recording Steps"));
        tableModel = new StepsTableModel(TABLE_HEADER, COLUMN_CLASSES);
		JScrollPane scrollPane = new JScrollPane(new JTable(tableModel));
		scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, STEPS_PANE_HEIGHT));
		panel.add(scrollPane, BorderLayout.NORTH);
		panel.setAlignmentX(LEFT_ALIGNMENT);
        return panel;	
    }

	private JPanel makeLocalSettingsPanel() {		
		jtfInteractionsFile = new JTextField();	
		JLabel jlblRecordedFile = new JLabel("Interactions File:");
		JButton button = new JButton("Browse");
		button.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = GuiUtil.newFileChooser(CHOOSE_INTERACTIONS_FILE, 
						 								  		  JFileChooser.FILES_ONLY, 
						 								  		  jtfInteractionsFile,
						 								  		  globalLocation);				
				if (fileChooser.showOpenDialog(PlaySamplerControlGui.this) == JFileChooser.APPROVE_OPTION) {
					jtfInteractionsFile.setText(fileChooser.getSelectedFile().getAbsolutePath());
					globalLocation = fileChooser.getSelectedFile().getParent();
					updateStepsTable();					
					updatePlaySamplersList();
				}
			}
		});

		jtfScreenshotsFile = new JTextField();	
		JLabel jlblScreenshotsFile = new JLabel("Hashes File:");
		JButton scrButton = new JButton("Browse");
		scrButton.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = GuiUtil.newFileChooser(CHOOSE_SCREENSHOTS_FILE, 
																  JFileChooser.FILES_ONLY, 
																  jtfScreenshotsFile,
																  globalLocation);				
				if (fileChooser.showOpenDialog(PlaySamplerControlGui.this) == JFileChooser.APPROVE_OPTION) {
					jtfScreenshotsFile.setText(fileChooser.getSelectedFile().getAbsolutePath());
					IcaConnector.getInstance().setLocationHashFile(fileChooser.getSelectedFile().getAbsolutePath());
					globalLocation = fileChooser.getSelectedFile().getParent();
				}
			}
		});
		
		jtfScreenshotsFolder = new JTextField();	
		JLabel jlblScreenshotsFolder = new JLabel("Screenshots Folder:");
		JButton scrFolderButton = new JButton("Browse");
		scrFolderButton.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = GuiUtil.newFileChooser(CHOOSE_SCREENSHOTS_FOLDER, 
																  JFileChooser.DIRECTORIES_ONLY, 
																  jtfScreenshotsFolder,
																  globalLocation);				
				if (fileChooser.showOpenDialog(PlaySamplerControlGui.this) == JFileChooser.APPROVE_OPTION) {
					globalLocation = fileChooser.getSelectedFile().getAbsolutePath();
					jtfScreenshotsFolder.setText(globalLocation);
					IcaConnector.getInstance().setLocationHashFolder(globalLocation);
				}
			}
		});
		
		JPanel panel = new JPanel(new GridBagLayout());

		panel.add(jlblRecordedFile, new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.NONE, GuiUtil.INSETS,0,0));
		panel.add(GuiUtil.getMandatoryPanel(jtfInteractionsFile), new GridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.NONE, GuiUtil.INSETS,0,0));
		panel.add(button, new GridBagConstraints(2,0,1,1,1.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.NONE, GuiUtil.INSETS,0,0));

		panel.add(jlblScreenshotsFile, new GridBagConstraints(0,1,1,1,0.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.NONE, GuiUtil.INSETS,0,0));
		panel.add(GuiUtil.getMandatoryPanel(jtfScreenshotsFile), new GridBagConstraints(1,1,1,1,0.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.NONE, GuiUtil.INSETS,0,0));
		panel.add(scrButton, new GridBagConstraints(2,1,1,1,1.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.NONE, GuiUtil.INSETS,0,0));

		panel.add(jlblScreenshotsFolder, new GridBagConstraints(0,2,1,1,0.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.NONE, GuiUtil.INSETS,0,0));
		panel.add(GuiUtil.getMandatoryPanel(jtfScreenshotsFolder), new GridBagConstraints(1,2,1,1,0.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.NONE, GuiUtil.INSETS,0,0));
		panel.add(scrFolderButton, new GridBagConstraints(2,2,1,1,1.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.NONE, GuiUtil.INSETS,0,0));

		
		jckbPlaySleep = new JCheckBox(LABEL_SLEEP);
		jckbPlaySleep.setSelected(true);
		jckbPlaySleep.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				jcbSleepFactor.setEnabled(((JCheckBox) e.getSource()).isSelected());
			}
		});
		jcbSleepFactor = new JComboBox(SLEEP_FACTORS);
		jcbSleepFactor.setSelectedItem(DEFAULT_SLEEP_FACTOR);
		Box box1 = Box.createHorizontalBox();
		box1.add(jckbPlaySleep);
		box1.add(jcbSleepFactor);
		panel.add(box1, new GridBagConstraints(0,4,2,1,0.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.NONE, new Insets(5,5,5,5),0,0));

		
		jrbNormalMode = new JRadioButton("Normal", true);
		jrbNormalMode.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				if (!jrbWindowlessMode.isSelected())
					jrbNormalMode.setSelected(true);
				if (jrbNormalMode.isSelected())
					jrbWindowlessMode.setSelected(false);
			}
		});
		jrbWindowlessMode = new JRadioButton("Windowless", false);
		jrbWindowlessMode.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				//prevent no-selection scenario
				if (!jrbNormalMode.isSelected())
					jrbWindowlessMode.setSelected(true);
				//prevent both-selected scenario
				if (jrbWindowlessMode.isSelected())
					jrbNormalMode.setSelected(false);
			}
		});
		Box box2 = Box.createHorizontalBox();
		box2.add(new JLabel("Running mode: "));
		box2.add(jrbNormalMode);
		box2.add(jrbWindowlessMode);
		panel.add(box2, new GridBagConstraints(0,3,2,1,0.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.NONE, new Insets(5,5,5,5),0,0));
		

		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), TITLE_LOCAL_SETTINGS_PANEL));
		panel.setAlignmentX(LEFT_ALIGNMENT);

		return panel;
	}

	private void updateStepsTable() {
		clearStepsTable();
		try {
			String[] row = null;
			List<Interaction> interactions = null;
			steps = InteractionUtil.readInteractions(jtfInteractionsFile.getText());
			IcaConnector.getInstance().setSteps(steps);
			StringInteraction strInteraction = null;
			boolean hasTags;
			LinkedHashMap<String,String> tags = null;
			tagsMap = new LinkedHashMap<String, LinkedHashMap<String,String>>(steps.size());
			
			for (RecordingStep step : steps) {
				
				row = newStepRow(step.getName());				
				tags = new LinkedHashMap<String, String>();				
				interactions = step.getInteractionList();				
				hasTags = false;				
				
				for (Interaction interaction : interactions) {
					
					if (interaction instanceof StringInteraction) {
						
						if (hasTags) {
							/* only gets here from second iteration on; it means we have more than 
							   one tag for the current step, so we need a new row.
							 */
							row = new String[TABLE_HEADER.length];
							row[0] = "";							
						} else						
							hasTags = true;							
						
						strInteraction = (StringInteraction) interaction; 
						//tag name
						row[1] = strInteraction.getName();
						//interactions
						row[2] = InteractionUtil.listToStr(strInteraction.getInteractions());
						//custom user input
						row[3] = "${" + step.getName() + ":" + strInteraction.getName() + "}";

						tags.put(row[1], row[3]);
						
						tableModel.addRow(row);
					}
				}
				
				if (!hasTags)
					tableModel.addRow(row);
				
				tagsMap.put(step.getName(), tags);
			}			
			IcaConnector.getInstance().setTagsMap(tagsMap);			
			tableModel.fireTableDataChanged();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			IcaConnector.getInstance().setSamplerController(samplerController);
			stepsTableUpdated = true;
		}
	}

	private String[] newStepRow(String stepName) {
		String[] row = new String[TABLE_HEADER.length];
		row[0] = stepName;
		for (int i=1; i < row.length; i++)
			row[i] = "";
		return row;		
	}

	private void clearStepsTable() {
		while (tableModel.getRowCount() > 0)
			tableModel.removeRow(0);
		tableModel.fireTableDataChanged();
	}
	
	public TestElement createTestElement() {
		samplerController = new PlaySamplerControl();
        modifyTestElement(samplerController);
        return samplerController;
	}


	public void modifyTestElement(TestElement element) {
		super.configureTestElement(element);
		if (element instanceof PlaySamplerControl) {
			samplerController = (PlaySamplerControl) element;
			samplerController.setIcaAddress(citrixPanel.getHost());
			samplerController.setDomain(citrixPanel.getDomain());
			samplerController.setPort(citrixPanel.getPort());
			samplerController.setUsername(citrixPanel.getUsername());
			samplerController.setPassword(citrixPanel.getPassword());
			samplerController.setInitialApp(citrixPanel.getInitialApp());
			samplerController.setInteractionPath(jtfInteractionsFile.getText().trim());
			samplerController.setScreenshotsHashesFilePath(jtfScreenshotsFile.getText().trim());
			samplerController.setScreenshotsForderPath(jtfScreenshotsFolder.getText().trim());
			samplerController.setSleepFactor(jcbSleepFactor.getSelectedItem().toString());
			samplerController.setSleepTimes(jckbPlaySleep.isSelected());
			samplerController.setRunningMode(jrbNormalMode.isSelected()?"NORMAL":"WINDOWLESS");
/*
 * 			IcaConnector icaConnector = samplerController.getIcaConnector();
			if (icaConnector == null)
				icaConnector = new IcaConnector();

 * */
			IcaConnector icaConnector = new IcaConnector();
			icaConnector.setAddress(samplerController.getIcaAddress());
			icaConnector.setDomain(samplerController.getDomain());
			icaConnector.setPort(samplerController.getPort());
			icaConnector.setUsername(samplerController.getUsername());
			icaConnector.setPassword(samplerController.getPassword());
			icaConnector.setApp(samplerController.getInitialApp());
			icaConnector.setRunningMode(samplerController.getRunningMode());
			
			samplerController.setIcaConnector(icaConnector);
			IcaConnector.getInstance().setSamplerController(samplerController);
		}
	}

	@Override
	public void configure(TestElement element) {
		super.configure(element);
		samplerController = (PlaySamplerControl) element;		

		citrixPanel.setName(samplerController.getName());
		citrixPanel.setComment(samplerController.getComment());		
		citrixPanel.setHost(samplerController.getIcaAddress());		
		citrixPanel.setDomain(samplerController.getDomain());		
		citrixPanel.setPort(samplerController.getPort());		
		citrixPanel.setUser(samplerController.getUsername());		
		citrixPanel.setPass(samplerController.getPassword());		
		citrixPanel.setInitialApp(samplerController.getInitialApp());
		
		String interactionsFilePath = samplerController.getInteractionsPath().trim();
		jtfInteractionsFile.setText(interactionsFilePath);
		if (!interactionsFilePath.isEmpty() && !stepsTableUpdated) {
			IcaConnector.resetInstance();
			updateStepsTable();
			globalLocation = (new File(interactionsFilePath)).getParent();
		}

		IcaConnector icaConnector = IcaConnector.getInstance();

		String screenshotsHashesFilePath = samplerController.getScreenshotsHashesFilePath();
		jtfScreenshotsFile.setText(screenshotsHashesFilePath);
		icaConnector.setLocationHashFile(screenshotsHashesFilePath);
		if (!screenshotsHashesFilePath.isEmpty() && 
			(null == globalLocation || globalLocation.isEmpty()))
			globalLocation = (new File(screenshotsHashesFilePath)).getParent();
		
		String screenshotsFolderPath = samplerController.getScreenshotsFolderPath();
		jtfScreenshotsFolder.setText(screenshotsFolderPath);
		icaConnector.setLocationHashFolder(screenshotsFolderPath);
		if (!screenshotsFolderPath.isEmpty() && 
			(null == globalLocation || globalLocation.isEmpty()))
			globalLocation = screenshotsFolderPath;
		
		jcbSleepFactor.setSelectedItem(samplerController.getSleepFactor());
		icaConnector.setSleepingComboBox(jcbSleepFactor);

		jckbPlaySleep.setSelected(samplerController.useSleepTimes());
		icaConnector.setSleepingCheckBox(jckbPlaySleep);
		
		if (samplerController.getRunningMode().equals("NORMAL")) {
			jrbNormalMode.setSelected(true);
			jrbWindowlessMode.setSelected(false);
		} else {
			jrbNormalMode.setSelected(false);
			jrbWindowlessMode.setSelected(true);
		}
		
		repaint();
	}
	
	public String getLabelResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStaticLabel() {
		return STATIC_LABEL;
	}

	private void updatePlaySamplersList() {
		try {
			updateSamplersList();
		} catch (IllegalUserActionException e1) {
			removeAllSamplersFromTree();
			e1.printStackTrace();
			JMeterUtils.reportErrorToUser("Error: test plan refresh failed");
		}    	
	}

	private void updateSamplersList() throws IllegalUserActionException {
    	
		removeAllSamplersFromTree();
    	
		for (String stepName : tagsMap.keySet()) {
			
			PlaySampler sampler = new PlaySampler();
			
			sampler.setController(samplerController);
			
			sampler.setName(stepName + " Sampler");
			
			sampler.setStep(getStepByName(stepName));
			
			sampler.setStringInput(tagsMap.get(stepName));

			LinkedHashMap<String, String> linkedHashMap = tagsMap.get(stepName);

			for (String tagName : linkedHashMap.keySet())
			
				sampler.setTagInput(tagName,linkedHashMap.get(tagName));
	        
			PlaySamplerGui samplerGui = new PlaySamplerGui();
	        samplerGui.configure(sampler);
	        samplerGui.modifyTestElement(sampler);        

	        sampler.setProperty(TestElement.GUI_CLASS, samplerGui.getClass().getName());

			JMeterTreeNode samplerNode = addSamplerToTree(sampler);
			
			addAssertionToTree(GuiPackage.getInstance().getTreeModel(), samplerNode, stepName + " Assertion");
		}
	}

	private void addAssertionToTree(JMeterTreeModel treeModel, JMeterTreeNode samplerNode, String name) throws IllegalUserActionException {
        IcaAssertion icaAssertion = new IcaAssertion();
        icaAssertion.setName(name);
        IcaAssertionGui icaAssertionGui = new IcaAssertionGui();
        icaAssertionGui.configure(icaAssertion);
        icaAssertionGui.modifyTestElement(icaAssertion);
        icaAssertion.setProperty(TestElement.GUI_CLASS, icaAssertionGui.getClass().getName());
        treeModel.addComponent(icaAssertion, samplerNode);
   	}

	private RecordingStep getStepByName(String stepName) {
		for (RecordingStep step : steps) 
			if (step.getName().equals(stepName))
				return step;
		return null;
	}

	private void removeAllSamplersFromTree() {
        JMeterTreeNode threadGroupNode = findThreadGroupNode();
        //no thread group node means that there's nothing to remove
        if (threadGroupNode == null)
        	return;
        //mark the samplers for deletion
        Enumeration<JMeterTreeNode> enumNode = threadGroupNode.children();
    	ArrayList<JMeterTreeNode> children = new ArrayList<JMeterTreeNode>();
        while (enumNode.hasMoreElements()) {
            JMeterTreeNode child = enumNode.nextElement();
            if (child.getUserObject() instanceof PlaySampler) 
				children.add(child);
        }
        //actually delete samplers from the thread group
        for (JMeterTreeNode child : children) 
        	threadGroupNode.remove(child);
        //reload the tree model and select the controller
        JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
        treeModel.reload(threadGroupNode);
    	JMeterTreeNode sampleControllerNode = treeModel.getNodeOf(samplerController);
    	TreePath treePathToController = new TreePath(treeModel.getPathToRoot(sampleControllerNode));
		GuiPackage.getInstance().getMainFrame().getTree().setSelectionPath(treePathToController);
	}

	private JMeterTreeNode addSamplerToTree(PlaySampler sampler) throws IllegalUserActionException {
        JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
        JMeterTreeNode threadGroupNode = findThreadGroupNode();
        if (threadGroupNode == null)
        	return null;

        return treeModel.addComponent(sampler, threadGroupNode);
	}

    private JMeterTreeNode findThreadGroupNode() {
        JMeterTreeModel treeModel = GuiPackage.getInstance().getTreeModel();
		JMeterTreeNode myNode = treeModel.getNodeOf(samplerController);
		if (myNode != null)
			return (JMeterTreeNode) myNode.getParent();
		return null;
    }
	

	class StepsTableModel extends PowerTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4638247606651019631L;

		public StepsTableModel(String[] tableHeader, Class<?>[] columnClasses) {
			super(tableHeader, columnClasses);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			//only the custom user input column should be editable
/*			if (column != TABLE_HEADER.length - 1)
				return false;
			//and only if we have a tag for the current step
			if (getValueAt(row, 1).toString().isEmpty())
				return false;
*/			
			return false;
		}
		
	    public void setValueAt(Object value, int row, int column) {
	    	super.setValueAt(value, row, column);
	    	fireTableCellUpdated(row, column);
	    }
	}


	public TestElement getPlayer() {
		// TODO Auto-generated method stub
		return samplerController;
	}
}
