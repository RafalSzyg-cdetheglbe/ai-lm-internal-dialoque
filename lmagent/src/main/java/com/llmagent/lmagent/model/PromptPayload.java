package com.llmagent.lmagent.model;

public class PromptPayload {
	private int numberOfIterations;
	private String modelName;
	private String systemMessage;
	private String userMessage;
	private double temperature;
	private int maxTokens;
	private boolean stream;
	private String fileName;

	// Getters and Setters
	public int getNumberOfIterations() {
		return numberOfIterations;
	}

	public void setNumberOfIterations(int numberOfIterations) {
		this.numberOfIterations = numberOfIterations;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getSystemMessage() {
		return systemMessage;
	}

	public void setSystemMessage(String systemMessage) {
		this.systemMessage = systemMessage;
	}

	public String getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public int getMaxTokens() {
		return maxTokens;
	}

	public void setMaxTokens(int maxTokens) {
		this.maxTokens = maxTokens;
	}

	public boolean isStream() {
		return stream;
	}

	public void setStream(boolean stream) {
		this.stream = stream;
	}

	public String getFileName()
	{
		return fileName;
	}
}