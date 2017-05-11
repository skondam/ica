package de.jkitberatung.recorder;

public class Interaction
{

	public enum Label {ScreenShot,
		KeyDown, KeyUp,
		MouseDown, MouseUp, MouseDoubleClick, MouseMove,
		SessWndCreate, SessWndDestroy, SessWndForeground,
		WndMove, WndSize, WndActivate, WndDeactivate, WndMinimize, WndCaptionChange, WndDestroy,
		SleepTime,
		StringTagStart, StringTagEnd, StringTag};
	
    private Label label;
    private Object value;

	public Interaction() {
		
	}
    
    public Interaction(Label label, Object value) {
		setLabel(label);
		setValue(value);
	}

	public Label getLabel()
    {
        return label;
    }

    public void setLabel(Label label)
    {
        this.label = label;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    @Override
    public String toString() {
    	
    	return getLabel().name() + ":" + getValue();
    }

  
}
