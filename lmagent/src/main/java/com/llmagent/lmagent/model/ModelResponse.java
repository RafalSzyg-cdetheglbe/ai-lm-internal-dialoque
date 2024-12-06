package com.llmagent.lmagent.model;

public class ModelResponse
{
	private String content;

	private int tokenCount;

	public ModelResponse(String content, int tokenCount)
	{
		this.content = content;
		this.tokenCount = tokenCount;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public int getTokenCount()
	{
		return tokenCount;
	}
}
