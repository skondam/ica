package de.jkitberatung.recorder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecordingStep
{
	public static final String STEP_KEY = "STEP";
	private String name;
	private List<Interaction> interactionList;

	private long lastTimestamp;
	private boolean autoAddSleepTimes = true;
	private boolean last;
	private boolean first;
	
	public RecordingStep(String name, boolean autoAddSleepTimes)
	{
		this.autoAddSleepTimes = autoAddSleepTimes; // in Play mode we don't observe (auto add) sleep times
		lastTimestamp = System.currentTimeMillis();
		setName(name);
		interactionList = new ArrayList<Interaction>();
	}

	public boolean isAutoAddSleeptimes() {
		return autoAddSleepTimes;
	}

	public void setAutoAddSleeptimes(boolean autoAddSleeptimes) {
		this.autoAddSleepTimes = autoAddSleeptimes;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<Interaction> getInteractionList()
	{
		return interactionList;
	}

	public void setInteractionList(List<Interaction> interactionList)
	{
		this.interactionList = interactionList;
	}

	public void addInteraction(Interaction i)
	{
		if (autoAddSleepTimes) {
			interactionList.add(new Interaction(Interaction.Label.SleepTime, (System.currentTimeMillis() - lastTimestamp) + ""));
			lastTimestamp = System.currentTimeMillis();
		}
		
		interactionList.add(i);
	}

	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(STEP_KEY + ":" + getName());

		for (Iterator<Interaction> iterator = interactionList.iterator(); iterator.hasNext();) {
			Interaction interaction = (Interaction) iterator.next();
			sb.append("\n" + interaction.toString());
		}

		return sb.toString();
	}

	public void setLast(boolean b) {
		this.last = b;
	}
	
	public boolean isLast() {
		return last;
	}

	public void setFirst(boolean b) {
		this.first = b;
	}
	
	public boolean isFirst() {
		return first;
	}
}
