package com.llmagent.lmagent.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StoryRating {
	private int cohesion;
	private int mistakes;
	private int characters;
	private int emotion;
	private int engagement;
	private String suggestion;

	public int getCohesion() {
		return cohesion;
	}

	public void setCohesion(int cohesion) {
		this.cohesion = cohesion;
	}

	public int getMistakes() {
		return mistakes;
	}

	public void setMistakes(int mistakes) {
		this.mistakes = mistakes;
	}

	public int getCharacters() {
		return characters;
	}

	public void setCharacters(int textLength) {
		this.characters = textLength;
	}

	public int getEmotion() {
		return emotion;
	}

	public void setEmotion(int emotion) {
		this.emotion = emotion;
	}

	public int getEngagement() {
		return engagement;
	}

	public void setEngagement(int engagement) {
		this.engagement = engagement;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	@Override
	public String toString() {
		return "StoryRating{" +
				"cohesion=" + cohesion +
				", mistakes=" + mistakes +
				", textLength=" + characters +
				", emotion=" + emotion +
				", engagement=" + engagement +
				", suggestion='" + suggestion + '\'' +
				'}';
	}
}
