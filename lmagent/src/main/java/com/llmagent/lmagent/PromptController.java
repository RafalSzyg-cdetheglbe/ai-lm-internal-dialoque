package com.llmagent.lmagent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PromptController
{
	@GetMapping("/prompt")
	public String prompt()
	{
		try
		{
			// The URL of the LM studio server
			URL url = new URL("http://localhost:8081/v1/chat/completions");

			// Establish the connection
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			// Build the JSON payload
			String jsonInputString = buildJsonPayload("TheBloke/Mistral-7B-Instruct-v0.2-GGUF", "You should always review given code",
					"helo word", 0.7, -1, false);

			// Write the JSON payload to the request body
			try (OutputStream os = connection.getOutputStream())
			{
				byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}

			// Get the response
			int code = connection.getResponseCode();
			System.out.println("Response Code: " + code);

			// Read the response
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

			// Parse the JSON response
			JSONObject jsonResponse = new JSONObject(response.toString());
			String content = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");

			// Print the content
			System.out.println("Response Content: " + content);
			return content;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return "";
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

