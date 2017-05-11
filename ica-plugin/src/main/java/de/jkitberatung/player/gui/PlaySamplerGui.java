package de.jkitberatung.player.gui;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JTextField;

import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

import de.jkitberatung.player.PlaySampler;
import de.jkitberatung.util.gui.GuiUtil;


public class PlaySamplerGui extends AbstractSamplerGui {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1571093874194201807L;

	private static final String STATIC_LABEL = "Citrix Sampler GUI";
	
	private JTextField jtfElementName;

	
	public PlaySamplerGui() {
		init();
	}	

	private void init() {
		setLayout(new BorderLayout());
		setBorder(makeBorder());
		
		Box box = Box.createVerticalBox();
		jtfElementName = new JTextField();
		box.add(GuiUtil.makeTitlePanel(jtfElementName));
		box.setAlignmentX(LEFT_ALIGNMENT);
		
		add(box,BorderLayout.NORTH);
		
		GuiUtil.setControlsSize(box.getComponents(), getName());
	}

	public TestElement createTestElement() {
		PlaySampler sampler = new PlaySampler();
		modifyTestElement(sampler);
		return sampler;
	}


	public String getLabelResource() {
		return null;
	}

	public String getStaticLabel() {
		return STATIC_LABEL;
	}
	
	public void modifyTestElement(TestElement element) {
		super.configureTestElement(element);
/*		if (element instanceof PlaySampler) {
			sampler = (PlaySampler) element;
			sampler.setTagInput(jtfTagInput.getText());
		}
*/	}
	
	@Override
	public void configure(TestElement element) {
		super.configure(element);
		jtfElementName.setText(element.getName());
		repaint();
	}
}
