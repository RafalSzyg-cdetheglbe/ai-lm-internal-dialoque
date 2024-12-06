package com.llmagent.lmagent.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.llmagent.lmagent.model.ModelResponse;
import com.llmagent.lmagent.tools.TokenCounter;

@Service
public class PromptService
{


	private final String LM_STUDIO_SERVER_URL_PROMPT = "http://localhost:8081/v1/chat/completions";

	public ModelResponse sendPrompt(String model, String systemMessage, String userMessage, double temperature, int maxTokens,
			boolean stream){
		try
		{
			URL url = new URL(LM_STUDIO_SERVER_URL_PROMPT);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			String jsonInputString = buildJsonPayload(model,
					systemMessage, userMessage, temperature, maxTokens, stream);

			try (OutputStream os = connection.getOutputStream())
			{
				byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}

			int code = connection.getResponseCode();
			System.out.println("Response Code: " + code);

			StringBuilder response = new StringBuilder();
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)))
			{
				String responseLine;
				while ((responseLine = br.readLine()) != null)
				{
					response.append(responseLine.trim());
				}
			}

			JSONObject jsonResponse = new JSONObject(response.toString());
			String content = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
			int tokenCount = jsonResponse.getJSONObject("usage").getInt("completion_tokens");
			System.out.println("Response Content: " + content + "Counted tokens:" + TokenCounter.countTokens(content));
			return new ModelResponse(content, tokenCount);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Prompting error: ", e);
		}
	}

	private String buildJsonPayload(String model, String systemMessage, String userMessage, double temperature, int maxTokens,
			boolean stream)
	{
		Map<String, Object> payload = new HashMap<>();
		payload.put("model", model);

		JSONArray messages = new JSONArray();
		messages.put(new JSONObject().put("role", "system").put("content", systemMessage));
		messages.put(new JSONObject().put("role", "user").put("content", userMessage));
		payload.put("messages", messages);

		payload.put("temperature", temperature);
		payload.put("max_tokens", maxTokens);
		payload.put("stream", stream);

		return new JSONObject(payload).toString();
	}
}
