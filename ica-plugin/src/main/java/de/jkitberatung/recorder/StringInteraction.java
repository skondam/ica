package de.jkitberatung.recorder;

import java.util.ArrayList;
import java.util.List;

public class StringInteraction extends Interaction {

	private String name;
	private List<Interaction> interactions;
	
	public StringInteraction(Label label, Object value) {
		super(label, value);
		interactions = new ArrayList<Interaction>();
	}
	
	public StringInteraction(String name) {
		this.name = name;
		interactions = new ArrayList<Interaction>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Interaction> getInteractions() {
		return interactions;
	}

	public void setInteractions(List<Interaction> interactions) {
		this.interactions = interactions;
	}

	public void addInteraction(Interaction interaction) {
		interactions.add(interaction);
		
	}
}
