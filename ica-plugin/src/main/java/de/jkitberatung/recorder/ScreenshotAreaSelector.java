package de.jkitberatung.recorder;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ScreenshotAreaSelector extends JDialog implements ActionListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -565076137106071499L;

	private static final String SAVE_COMMAND = "save";

	BufferedImage image;
	private String filePath;
	private ScreenshotPanel selectionPanel;

	private JScrollPane scrollPane;

	public ScreenshotAreaSelector(String filePath) {

		this.filePath = filePath; 

		loadImage();
		init();

		setLocation(100,100);
		pack();

		setModal(true);
		setVisible(true);
	}

	private void init() {
		setTitle("Select a screenshot area");
		
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		JPanel panel = new JPanel(new BorderLayout());

		selectionPanel = new ScreenshotPanel(this, image);
		scrollPane = new JScrollPane(selectionPanel);
		panel.add(scrollPane , BorderLayout.CENTER);

		JPanel commandPanel = new JPanel();
		JButton saveButton = new JButton("Take screenshot & Close");
		saveButton.setActionCommand(SAVE_COMMAND);
		saveButton.addActionListener(this);
		commandPanel.add(saveButton);

		panel.add(commandPanel, BorderLayout.SOUTH);
		getContentPane().add(panel );
	}

	private void loadImage()  {

		try  {
			image = ImageIO.read(new File(filePath));

		} catch(MalformedURLException mue) {
			System.out.println("Error: " + mue.getMessage());
		} catch(IOException ioe) {
			System.out.println("Error: " + ioe.getMessage());

		}

	}

	public static void main(String[] args)  {
		new ScreenshotAreaSelector("d:/tmp/screenshots/fullscreenshot.bmp");

	}

	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();

		if (actionCommand.equals(SAVE_COMMAND)) {
			
			Rectangle area = getSelectedArea();
			if (area.getHeight() <= 0 || area.getWidth() <= 0) {
				JOptionPane.showMessageDialog(null, "No valid area selected. Please select an area before closing", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			setVisible(false);
		} 

	}

	public Rectangle getSelectedArea() {
		return selectionPanel.getRectangle();
	}

	public void scroll(Rectangle rectangle) {
		scrollPane.getViewport().scrollRectToVisible(rectangle);
	}
}

