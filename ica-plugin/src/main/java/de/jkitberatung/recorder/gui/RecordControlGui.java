package de.jkitberatung.recorder.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.jmeter.control.gui.AbstractControllerGui;
import org.apache.jmeter.gui.JMeterGUIComponent;
import org.apache.jmeter.gui.util.MenuFactory;
import org.apache.jmeter.testelement.TestElement;

import de.jkitberatung.recorder.RecordControl;
import de.jkitberatung.recorder.Validator;
import de.jkitberatung.util.gui.CitrixPanel;
import de.jkitberatung.util.gui.GuiUtil;




public class RecordControlGui extends AbstractControllerGui 
			implements JMeterGUIComponent, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 486980722170366821L;
	
	private static final String CONNECTING = "Connecting...";
	private static final String LABEL_BMP_FOLDER = "Screenshots Folder:";
	private static final String RECORD = "Record";
	private static final String RECORDING = "Recording...";
	private static final String SAVE = "Save";
	private static final String SCREENSHOT = "Screenshot";
	private static final String STATIC_LABEL = "Citrix ICA Recorder";
	private static final String STEP_ADD = "Add Step";
	private static final String STEP_DELETE = "Delete Step";
	private static final String STEPS_ROOT_NODE = "Recording Steps";
	private static final String STOP = "Stop";
	private static final String TAG_START = "Start Tag";
	private static final String TAG_END = "End Tag";
	private static final String TITLE_BUTTONS_PANEL = "";
	private static final String TITLE_LOCAL_SETTINGS_PANEL = "Local Settings";

	private static final String CHOOSE_BMP_FOLDER = "Select Screenshots Folder";	
	private static final String CONFIRM_SAVE_SESSION = "You have an unsaved recording session. Do you want to save it now?\r\n" +
													   "By pressing \"No\" you will lose the current session.";
	private static final String VALIDATION_FAULT = "Please fill-in all mandatory fields";
	private static final String VALIDATION_FAULT_TITLE = "Failed to initiate recording session";

	private static final int STEPS_PANE_HEIGHT = 200;
	private static final int STEPS_PANE_WIDTH = 420;
	private static final long DEATH=1306886400000L;

	
	private CitrixPanel citrixPanel;
	private RecordControl recorder;

	private String globalLocation;

	private JTree stepsTree;
	private DefaultTreeModel stepsTreeModel;	
	private DefaultMutableTreeNode stepsTreeRootNode;

	private JTextField jtfBitmapFolder;
	private JButton jbtnAddStep;
	private JButton jbtnDelStep;
	private JButton jbtnEndTag;
	private JButton jbtnRecord;
	private JButton jbtnSave;
	private JButton jbtnScrshot;
	private JButton jbtnStartTag;
	private JButton jbtnStop;

	private boolean unsavedSession;

	private HashMap<String, ArrayList<String>> tagsMap;


	public RecordControlGui() {
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
        box.add(Box.createVerticalStrut(GuiUtil.STRUT_HEIGHT));
		box.add(makeButtonsPanel());
		box.add(new JLabel(GuiUtil.LABEL_MANDATORY_EXPLAIN));
		box.setAlignmentX(Component.LEFT_ALIGNMENT);		
	
		add(box,BorderLayout.NORTH);		
		
		GuiUtil.setControlsSize(box.getComponents(), getName());
		enableStepsPanel(false);
	}

	private Component makeCitrixPanel() {
		citrixPanel = new CitrixPanel(getName(), new de.jkitberatung.util.KeyListener(this));
		return citrixPanel;
	}

	private JPanel makeLocalSettingsPanel() {		
		jtfBitmapFolder = new JTextField();	
		JLabel jlblBitmapFolder = new JLabel(LABEL_BMP_FOLDER);
		JButton button = new JButton("Browse");
		button.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = GuiUtil.newFileChooser(CHOOSE_BMP_FOLDER,
																  JFileChooser.DIRECTORIES_ONLY,
																  jtfBitmapFolder,
																  globalLocation);				
				if (fileChooser.showOpenDialog(RecordControlGui.this) == JFileChooser.APPROVE_OPTION) {
					globalLocation = fileChooser.getSelectedFile().getAbsolutePath();
					jtfBitmapFolder.setText(globalLocation);
				}
			}
		});

		JPanel panel = new JPanel(new GridBagLayout());

		panel.add(jlblBitmapFolder, new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.NONE, GuiUtil.INSETS,0,0));
		panel.add(GuiUtil.getMandatoryPanel(jtfBitmapFolder), new GridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.NONE, GuiUtil.INSETS,0,0));
		panel.add(button, new GridBagConstraints(2,0,1,1,1.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.NONE, GuiUtil.INSETS,0,0));

		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), TITLE_LOCAL_SETTINGS_PANEL));
		panel.setAlignmentX(LEFT_ALIGNMENT);

		return panel;
	}
	
	private JPanel makeStepsPanel() {		
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());

		stepsTreeRootNode = new DefaultMutableTreeNode(STEPS_ROOT_NODE);
        stepsTreeModel = new DefaultTreeModel(stepsTreeRootNode);
        stepsTree = new JTree(stepsTreeModel);
        JScrollPane scrollPane = new JScrollPane(stepsTree);
        Dimension dimension = new Dimension(STEPS_PANE_WIDTH, STEPS_PANE_HEIGHT);
		scrollPane.setMinimumSize(dimension);
		scrollPane.setPreferredSize(dimension);
		scrollPane.setMaximumSize(dimension);
        
        Box buttonsPanel = Box.createVerticalBox();
        jbtnAddStep = newButton(STEP_ADD);
        buttonsPanel.add(jbtnAddStep);
        buttonsPanel.add(Box.createVerticalStrut(3));
        jbtnDelStep = newButton(STEP_DELETE);
        buttonsPanel.add(jbtnDelStep);
        buttonsPanel.add(Box.createVerticalStrut(3));
        jbtnStartTag = newButton(TAG_START);
        buttonsPanel.add(jbtnStartTag);
        buttonsPanel.add(Box.createVerticalStrut(3));
        jbtnEndTag = newButton(TAG_END);
        buttonsPanel.add(jbtnEndTag);
        buttonsPanel.add(Box.createVerticalStrut(3));
		jbtnScrshot = newButton(SCREENSHOT);
		buttonsPanel.add(jbtnScrshot);

		panel.add(scrollPane, new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.BOTH, GuiUtil.INSETS,0,0));
		panel.add(buttonsPanel, new GridBagConstraints(1,0,1,1,1.0,0.0,GridBagConstraints.WEST,
				   GridBagConstraints.VERTICAL, new Insets(1,10,1,5),0,0));

		panel.setAlignmentX(LEFT_ALIGNMENT);

		tagsMap = new HashMap<String, ArrayList<String>>();
		
        return panel;	
    }

	private JPanel makeButtonsPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), TITLE_BUTTONS_PANEL));
		jbtnRecord = newButton(RECORD);
		panel.add(jbtnRecord);
		jbtnStop = newButton(STOP);
		jbtnStop.setEnabled(false);
		panel.add(jbtnStop);
		jbtnSave = newButton(SAVE);
		jbtnSave.setEnabled(false);
		panel.add(jbtnSave);
		panel.setAlignmentX(LEFT_ALIGNMENT);
		return panel;
	}

	private JButton newButton(String label) {
		JButton button = new JButton(label);
		button.setActionCommand(label);
		button.addActionListener(this);
		return button;
	}

	public TestElement createTestElement() {
		recorder = new RecordControl();
        modifyTestElement(recorder);
        return recorder;
	}


	public String getLabelResource() {
		return null;
	}

	@Override
	public String getStaticLabel() {
		return STATIC_LABEL;
	}
	
	
	public void modifyTestElement(TestElement element) {
		super.configureTestElement(element);
		if (element instanceof RecordControl) {
			recorder = (RecordControl) element;
			recorder.setIcaAddress(citrixPanel.getHost());
			recorder.setDomain(citrixPanel.getDomain());
			recorder.setPort(citrixPanel.getPort());
			recorder.setUsername(citrixPanel.getUsername());
			recorder.setPassword(citrixPanel.getPassword());
			recorder.setInitialApp(citrixPanel.getInitialApp());
			recorder.setBitmapFolder(jtfBitmapFolder.getText().trim());
		}
	}

	@Override
	public void configure(TestElement element) {
		super.configure(element);
		recorder = (RecordControl) element;
		citrixPanel.setName(recorder.getName());
		citrixPanel.setComment(recorder.getComment());
		citrixPanel.setHost(recorder.getIcaAddress());
		citrixPanel.setDomain(recorder.getDomain());
		citrixPanel.setPort(recorder.getPort());
		citrixPanel.setUser(recorder.getUsername());
		citrixPanel.setPass(recorder.getPassword());
		citrixPanel.setInitialApp(recorder.getInitialApp());
		String bitmapFolder = recorder.getBitmapFolder().trim();
		jtfBitmapFolder.setText(bitmapFolder);
		if (!bitmapFolder.isEmpty())
			globalLocation = bitmapFolder;
		repaint();
	}
	
	@Override
	public Collection<String> getMenuCategories() {
        return Arrays.asList(new String[] { MenuFactory.NON_TEST_ELEMENTS });
	}

	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		
		if (actionCommand.equals(STEP_ADD)) 			
			addStep();			
		else if (actionCommand.equals(STEP_DELETE)) 			
			deleteStep();		
		else if (actionCommand.equals(TAG_START))
			startTag();		
		else if (actionCommand.equals(TAG_END))
			endTag();		
		else if (actionCommand.equals(RECORD)) {
			modifyTestElement(recorder);
			if (!Validator.isValid(recorder)) {
				JOptionPane.showMessageDialog(this, 
											  VALIDATION_FAULT, VALIDATION_FAULT_TITLE, 
											  JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (unsavedSession) {
				int saveDialogResponse = JOptionPane.showConfirmDialog(this, CONFIRM_SAVE_SESSION);
				if (saveDialogResponse == JOptionPane.YES_OPTION) {
					if (save()) { 
						initRecording();
						return;
					}
				} else if (saveDialogResponse == JOptionPane.CANCEL_OPTION)
					return;
			}
			initRecording();
			
		} else if (actionCommand.equals(STOP)) 
			stop();		
		else if (actionCommand.equals(SAVE))
			save();			
		else if (actionCommand.equals(SCREENSHOT)) 			
			recorder.takeScreenShot(true);		
	}

	private void initRecording() {
		clearSteps();		
		if (addStep())
			record();	
	}

	private void record() {
		setEnabled(jbtnRecord);
		recorder.setGuiHandler(this);
		recorder.record();
	}

	public void stop() {
		setEnabled(jbtnStop);
		recorder.stop();
	}

	private boolean save() {
		JFileChooser jFileChooser = new JFileChooser();
		
		if (null != globalLocation && !globalLocation.isEmpty())
			GuiUtil.setCurrentDirectory(jFileChooser, globalLocation);
		
		if (jFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jFileChooser.getSelectedFile();
			if (selectedFile.exists() &&
				JOptionPane.showConfirmDialog(RecordControlGui.this, "Overwrite existing file?") != JOptionPane.YES_OPTION)
				return save();

			recorder.save(selectedFile);
			unsavedSession = false;
		}
		return true;
	}

	private boolean addStep() {
        String stepName = JOptionPane.showInputDialog(RecordControlGui.this, "Step Name:");
        if (stepName == null || stepName.trim().isEmpty())
        	return false;
        stepName = stepName.trim();
        if (tagsMap.containsKey(stepName)) {
        	JOptionPane.showMessageDialog(RecordControlGui.this, 
        								  "The step name must be unique.", 
        								  "Error: duplicate step name", 
        								  JOptionPane.ERROR_MESSAGE);
        	addStep();
        	return false;
        }
		recorder.newStep(stepName, true);			
		DefaultMutableTreeNode parentNode = null;
        parentNode = stepsTreeRootNode;
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(stepName);
        stepsTreeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
        tagsMap.put(stepName, new ArrayList<String>());
        stepsTree.scrollPathToVisible(new TreePath(childNode.getPath()));
        return true;
	}

	private void deleteStep() {
        TreePath currentSelection = stepsTree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            if (parent != null) {
        		recorder.deleteStep(parent.getIndex(currentNode));
                stepsTreeModel.removeNodeFromParent(currentNode);
                tagsMap.remove(currentNode.toString());
            }
        } else
            JOptionPane.showMessageDialog(RecordControlGui.this, 
										  "No step selected. You must specify which step you would like to delete, by selecting it.", 
										  "Failed to delete step", 
										  JOptionPane.ERROR_MESSAGE);
	}

	private void clearSteps() {
		recorder.clearSteps();
		stepsTreeRootNode.removeAllChildren();
		stepsTreeModel.reload();
		tagsMap.clear();
	}

	private void startTag() {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) stepsTreeModel.getChild(stepsTreeRootNode, stepsTreeRootNode.getChildCount()-1);
        if (parentNode == null) { 
            JOptionPane.showMessageDialog(RecordControlGui.this, 
            							  "Please create a step before inserting a string tag.", 
            							  "Failed to insert string tag", 
            							  JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.print("Adding tag to parrent node " + parentNode.toString() + "... ");
        String tagName = JOptionPane.showInputDialog(RecordControlGui.this, "Tag Name:");
        if (tagName == null || tagName.trim().isEmpty())
        	return;
        tagName = tagName.trim();
        if (tagsMap.get(parentNode.toString()).contains(tagName)) {
        	JOptionPane.showMessageDialog(RecordControlGui.this, 
										  "The tag name must be unique within a certain tag.", 
										  "Error: duplicate tag name", 
										  JOptionPane.ERROR_MESSAGE);
			startTag();
			return;
        }
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(tagName);
        stepsTreeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
        tagsMap.get(parentNode.toString()).add(tagName);
        stepsTree.scrollPathToVisible(new TreePath(childNode.getPath()));
        jbtnStartTag.setEnabled(false);
        jbtnEndTag.setEnabled(true);
        jbtnAddStep.setEnabled(false);
        jbtnDelStep.setEnabled(false);
        System.out.println("done");
        recorder.addStringTagStart(tagName);
	}
	
	private void endTag() {
		DefaultMutableTreeNode currentStep = (DefaultMutableTreeNode) stepsTreeModel.getChild(stepsTreeRootNode, stepsTreeRootNode.getChildCount()-1);
		String currentTagName = null;
		if (currentStep != null)
			currentTagName = currentStep.getLastChild().toString();
		if (currentTagName == null || currentTagName.isEmpty()) {
            JOptionPane.showMessageDialog(RecordControlGui.this, 
										  "No started tag found.", 
										  "Failed to end string tag", 
										  JOptionPane.ERROR_MESSAGE);
            return;
		}			
		jbtnStartTag.setEnabled(true);
		jbtnEndTag.setEnabled(false);
        jbtnAddStep.setEnabled(true);
        jbtnDelStep.setEnabled(true);
		recorder.addStringTagEnd(currentTagName);
	}

	private void setEnabled(JButton btn) {
		btn.setEnabled(false);
		
		if (btn == jbtnRecord) {
			jbtnRecord.setText(CONNECTING);
			jbtnSave.setEnabled(false);
		} else if (btn == jbtnStop) {
			jbtnRecord.setEnabled(true);
			jbtnRecord.setText(RECORD);
			if (unsavedSession)
				jbtnSave.setEnabled(true);
			else
				jbtnSave.setEnabled(false);
			enableStepsPanel(false);
		}
	}

	private void enableStepsPanel(boolean b) {
		jbtnAddStep.setEnabled(b);
		jbtnDelStep.setEnabled(b);
		if (!b)
			jbtnEndTag.setEnabled(b);
		jbtnScrshot.setEnabled(b);
		jbtnStartTag.setEnabled(b);
	}

	public void onConnect() {
		unsavedSession = true;
		jbtnStop.setEnabled(true);
		jbtnRecord.setText(RECORDING);
		enableStepsPanel(true);
	}
}
