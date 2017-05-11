package de.jkitberatung.assertion.gui;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JTextField;

import org.apache.jmeter.assertions.gui.AbstractAssertionGui;
import org.apache.jmeter.testelement.TestElement;

import de.jkitberatung.assertion.IcaAssertion;
import de.jkitberatung.util.gui.GuiUtil;



public class IcaAssertionGui extends AbstractAssertionGui {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7272715565022796154L;
	
	private static final String STATIC_LABEL = "Citrix Assertion GUI";

	private JTextField jtfElementName;
	
	
	public IcaAssertionGui() {
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
		IcaAssertion icaAssertion = new IcaAssertion();
		modifyTestElement(icaAssertion);
		return icaAssertion;
	}

	public void modifyTestElement(TestElement element) {
		super.configureTestElement(element);
	}
	
	@Override
	public void configure(TestElement element) {
		super.configure(element);
		jtfElementName.setText(element.getName());
		
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
}
