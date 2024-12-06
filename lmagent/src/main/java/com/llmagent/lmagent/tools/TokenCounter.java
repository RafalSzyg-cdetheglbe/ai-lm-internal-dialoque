package com.llmagent.lmagent.tools;

public class TokenCounter
{
	public static int countAllTokens(String text) {
		int tokenCount = countTokens(text);
		System.out.println("Token count: " + tokenCount);
		return tokenCount;
	}

	public static int countTokens(String text) {
		if (text == null || text.isEmpty()) {
			return 0;
		}
		String[] tokens = text.split("\\s+");
		return tokens.length;
	}
}
