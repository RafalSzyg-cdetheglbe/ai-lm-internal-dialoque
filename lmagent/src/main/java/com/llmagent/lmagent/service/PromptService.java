package com.llmagent.lmagent.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.llmagent.lmagent.model.ModelResponse;
import com.llmagent.lmagent.utils.CsvUtlis;
import com.llmagent.lmagent.utils.SystemPromptMessages;

import nl.dannyj.mistral.MistralClient;
import nl.dannyj.mistral.builders.MessageListBuilder;
import nl.dannyj.mistral.models.completion.ChatCompletionRequest;
import nl.dannyj.mistral.models.completion.ChatCompletionResponse;
import nl.dannyj.mistral.models.completion.Message;

@Service
public class PromptService
{
	private final String LM_STUDIO_SERVER_URL_PROMPT = "http://localhost:8081/v1/chat/completions";

	private final MistralClient mistralClient;

	public PromptService(MistralClient mistralClient)
	{
		this.mistralClient = mistralClient;
	}

	public ModelResponse sendPrompt(String model, String systemMessage, String userMessage, double temperature, int maxTokens,
			boolean stream)
	{
		try
		{
			URL url = new URL(LM_STUDIO_SERVER_URL_PROMPT);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			String jsonInputString = buildJsonPayload(model, systemMessage, userMessage, temperature, maxTokens, stream);

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
			System.out.println("Response Content: " + content);
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

	public String startHistoryMakingPipeline(int numberOfIterations, String model, String systemMessage, String userMessage,
			double temperature, int maxTokens, boolean stream)
	{
		ModelResponse response = sendPrompt(model, systemMessage, userMessage, temperature, maxTokens, stream);

		List<String> storyBatches = new ArrayList<>();
		ModelResponse previousResponse = new ModelResponse(userMessage, 0);
		storyBatches.add(response.getContent());
		for (int i = 0; i < numberOfIterations; i++)
		{
			ModelResponse modelResponse = sendPrompt(model, //
					"continue while ensuring a logical progression and consistent style throughout.", //
					previousResponse.getContent(), temperature, //
					maxTokens, stream);
			previousResponse.setContent(modelResponse.getContent());
			storyBatches.add(modelResponse.getContent());
		}
		return buildStoryString(storyBatches);
	}

	public int rateStory(String story)
	{
		return 0;
	}


	public String startRatingPipeline(int numberOfIterations, String model, String systemMessage, String userMessage,
			double temperature, int maxTokens, boolean stream)
	{
		return String.valueOf('x');
	}

	private String buildStoryString(List<String> storyBatches)
	{
		StringBuilder story = new StringBuilder();
		for (String batch : storyBatches)
		{
			story.append(batch);
			story.append("\n");
		}
		CsvUtlis.saveStringToCsv(story.toString(), "story.csv");
		return story.toString();
	}

	public String startRatingMistralPipeline(String userMessage, String promptMessage, String model, double temperature)
	{
		List<Message> messages = new MessageListBuilder().system(userMessage).user(promptMessage).build();

		ChatCompletionRequest request = ChatCompletionRequest.builder().model(model).temperature(temperature).messages(messages)
				.safePrompt(false).build();

		ChatCompletionResponse response = mistralClient.createChatCompletion(request);

		Message firstChoice = response.getChoices().get(0).getMessage();
		System.out.println(firstChoice.getRole() + ":\n" + firstChoice.getContent() + "\n");
		return response.getChoices().get(0).getMessage().getContent();
	}

	public String startIterativeRatingMistralPipeline(final String fake, final String promptMessage, final String modelName,
			final double temperature)
	{
		String userMessage = SystemPromptMessages.MISTRAL_RATING_MESSAGE_WITHOUT_EXPLANATION_WITH_SUGGESTION;
		ModelResponse pMessage = this.sendPrompt("heBloke/Mistral-7B-Instruct-v0.2-GGUF", "Make me a history",
				"Tell me story about friendship", 100, 500, false);

		String  spMessage = pMessage.getContent();

		List<Message> messages = new MessageListBuilder().system(userMessage).user(spMessage).build();

		ChatCompletionRequest request = ChatCompletionRequest.builder().model(modelName).temperature(temperature).messages(
				messages).safePrompt(false).build();

		ChatCompletionResponse response = mistralClient.createChatCompletion(request);

		Message firstChoice = response.getChoices().get(0).getMessage();
		System.out.println(firstChoice.getRole() + ":\n" + firstChoice.getContent() + "\n");
		return response.getChoices().get(0).getMessage().getContent();
	}
}
