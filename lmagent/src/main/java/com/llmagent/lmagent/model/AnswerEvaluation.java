package com.llmagent.lmagent.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnswerEvaluation {
	private int truthfulness;
	private int development;
	private int relevanceToTopic;
	private String suggestion;

	public int getTruthfulness() {
		return truthfulness;
	}

	public void setTruthfulness(int truthfulness) {
		this.truthfulness = truthfulness;
	}

	public int getDevelopment() {
		return development;
	}

	public void setDevelopment(int development) {
		this.development = development;
	}

	public int getRelevanceToTopic() {
		return relevanceToTopic;
	}

	public void setRelevanceToTopic(int relevanceToTopic) {
		this.relevanceToTopic = relevanceToTopic;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	@Override
	public String toString() {
		return "AnswerEvaluation{" +
				"truthfulness=" + truthfulness +
				", development=" + development +
				", relevanceToTopic=" + relevanceToTopic +
				", suggestion='" + suggestion + '\'' +
				'}';
	}
}

