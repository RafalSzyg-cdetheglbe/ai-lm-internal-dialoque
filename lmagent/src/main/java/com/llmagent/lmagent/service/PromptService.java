package com.llmagent.lmagent.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.llmagent.lmagent.model.AnswerEvaluation;
import com.llmagent.lmagent.model.ModelResponse;
import com.llmagent.lmagent.model.StoryRating;
import com.llmagent.lmagent.utils.CsvUtlis;
import com.llmagent.lmagent.utils.QuestionCategories;
import com.llmagent.lmagent.utils.SystemPromptMessages;

import nl.dannyj.mistral.MistralClient;
import nl.dannyj.mistral.builders.MessageListBuilder;
import nl.dannyj.mistral.exceptions.MistralAPIException;
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
		return buildStoryString(storyBatches, 0);
	}

	public String startRatingPipeline(int numberOfIterations, String model, String systemMessage, String userMessage,
			double temperature, int maxTokens, boolean stream)
	{
		return String.valueOf('x');
	}

	private String buildStoryString(List<String> storyBatches, int id)
	{
		StringBuilder story = new StringBuilder();
		for (String batch : storyBatches)
		{
			story.append(batch);
			story.append("\n");
		}
		CsvUtlis.saveStringToCsv(story.toString(), "story.csv", id);
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

	public StoryRating startIterativeRatingMistralPipeline(String promptMessage, final String modelName,
			final double temperature,
			final int iterations, final int maxTokens, final String fileName)
	{
		String suggestion = "Make a coherent story"; //starting message
		StoryRating storyRating = null;

		String userMessage = SystemPromptMessages.MISTRAL_RATING_MESSAGE_WITHOUT_EXPLANATION_WITH_SUGGESTION;

		for (int i = 0; i < iterations; i++)
		{
			//send message to local model
			ModelResponse pMessage = this.sendPrompt(modelName, suggestion, promptMessage, 0.7, maxTokens, false);

			//get message
			String spMessage = pMessage.getContent();

			CsvUtlis.saveStringToCsv(spMessage, fileName, i);

			//build rating message
			List<Message> messages = new MessageListBuilder().system(userMessage).user(spMessage).build();

			//build request
			ChatCompletionRequest request =
					ChatCompletionRequest.builder().model("mistral-large-latest").temperature(temperature)
					.messages(messages).safePrompt(false).build();

			//send request to mistral
			ChatCompletionResponse response = mistralClient.createChatCompletion(request);

			Message firstChoice = response.getChoices().get(0).getMessage();
			System.out.println(firstChoice.getRole() + ":\n" + firstChoice.getContent() + "\n");
			storyRating = parseJsonString(firstChoice);
			CsvUtlis.saveRatingToCSV(storyRating, i, fileName);

			//for control tests disabled
			//promptMessage = "You should correct this text to make it better: " + spMessage;

			if (storyRating != null && storyRating.getSuggestion() != null)
			{
				//for controll tests disabled
				//suggestion = storyRating.getSuggestion();
			}
		}
		if (storyRating == null)
		{
			throw new RuntimeException("Error while parsing the JSON string");
		}
		return storyRating;
	}

	private StoryRating parseJsonString(Message stringJson)
	{
		String[] lines = stringJson.getContent().split("\n");
		StringBuilder jsonStringBuilder = new StringBuilder();
		for (int i = 1; i < lines.length - 1; i++)
		{
			jsonStringBuilder.append(lines[i]).append("\n");
		}
		String jsonString = jsonStringBuilder.toString().trim();


		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(jsonString, StoryRating.class);
		}
		catch (Exception e)
		{
			System.out.println("Error while parsing the JSON string: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private AnswerEvaluation parseAnswerEvaluationJsonString(Message stringJson) {
		String content = stringJson.getContent().trim();
		String jsonString;

		// Handle '''json and ''' markers if present
		if (content.startsWith("```json") && content.endsWith("```")) {
			// Extract the JSON between the markers
			content = content.substring(7, content.length() - 3).trim();
		}

		// Build the JSON string from the content
		if (content.startsWith("{") && content.endsWith("}")) {
			// Direct JSON content without extra parsing
			jsonString = content;
		} else {
			// Handle multi-line JSON content
			String[] lines = content.split("\n");
			StringBuilder jsonStringBuilder = new StringBuilder();
			for (String line : lines) {
				jsonStringBuilder.append(line).append("\n");
			}
			jsonString = jsonStringBuilder.toString().trim();
		}

		try {
			// Parse JSON into the AnswerEvaluation object
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(jsonString, AnswerEvaluation.class);
		} catch (Exception e) {
			System.out.println("Error while parsing the JSON string: " + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}


	public StoryRating startIterativeRatingOnlyMistralPipeline(String promptMessage, final String modelName,
			final double temperature, final int iterations, final int maxTokens, String suggestion, String fileName)
	{
		//String suggestion = "Make a coherent story"; //starting message
		StoryRating storyRating = null;
		String systemPredefinedMessageToRate = SystemPromptMessages.MISTRAL_RATING_MESSAGE_WITHOUT_EXPLANATION_WITH_SUGGESTION;

		for (int i = 0; i < iterations; i++)
		{
			try
			{
				List<Message> storyMakingMessages = new MessageListBuilder().system(suggestion).user(promptMessage).build();
				ChatCompletionRequest storyRequest = ChatCompletionRequest.builder().model("mistral-large-latest").temperature(
						temperature).messages(storyMakingMessages).safePrompt(false).build();

				ChatCompletionResponse storyResponse = sendRequestWithRetry(storyRequest, 10); // Retry logic here
				Message storyMessageResponse = storyResponse.getChoices().get(0).getMessage();
				System.out.println(storyMessageResponse.getRole() + ":\n" + storyMessageResponse.getContent() + "\n");

				CsvUtlis.saveStringToCsv(storyMessageResponse.getContent(), fileName, i);

				List<Message> ratingMessages = new MessageListBuilder().system(systemPredefinedMessageToRate).user(
						storyMessageResponse.getContent()).build();
				ChatCompletionRequest ratingRequest = ChatCompletionRequest.builder().model("mistral-large-latest").temperature(
						temperature).messages(ratingMessages).safePrompt(false).build();

				ChatCompletionResponse response = sendRequestWithRetry(ratingRequest, 10); // Retry logic here
				Message firstChoice = response.getChoices().get(0).getMessage();
				System.out.println(firstChoice.getRole() + ":\n" + firstChoice.getContent() + "\n");

				storyRating = parseJsonString(firstChoice);
				CsvUtlis.saveRatingToCSV(storyRating, i, fileName);
				promptMessage =
						"Correct this text with suggestion given, and reply only with corrected text: " + storyMessageResponse.getContent();

				if (storyRating != null && storyRating.getSuggestion() != null)
				{
					suggestion = storyRating.getSuggestion();
				}
			}
			catch (Exception e)
			{
				System.err.println("Error during iteration " + i + ": " + e.getMessage());
				if (storyRating == null)
				{
					i = i - 1;
				}
				//throw new RuntimeException("Error while parsing the JSON string");
			}
		}

		if (storyRating == null)
		{
			throw new RuntimeException("Error while parsing the JSON string");
		}

		return storyRating;
	}


	public void startAnwseringIterations(String modelname, Double temperature, int iterations, int maxTokens)
	{
		List<String> logicQuestions = QuestionCategories.logicQuestions;
		List<String> generalKnowledgeQuestions = QuestionCategories.generalKnowledgeQuestions;
		List<String> philosophyQuestions = QuestionCategories.philosophyQuestions;
		List<String> textInterpretationQuestions = QuestionCategories.textInterpretationQuestions;

		for (int i = 0; i < generalKnowledgeQuestions.size(); i++)
		{
			startAnswerRating(modelname, "General Knowledge", generalKnowledgeQuestions.get(i), i + 1, temperature, iterations,
					maxTokens);
		}

		// Logic
		for (int i = 0; i < logicQuestions.size(); i++)
		{
			startAnswerRating(modelname, "Logic", logicQuestions.get(i), i + 1, temperature, iterations, maxTokens);
		}

		// Text Interpretation
		for (int i = 0; i < textInterpretationQuestions.size(); i++)
		{
			startAnswerRating(modelname, "Text Interpretation", textInterpretationQuestions.get(i), i + 1, temperature,
					iterations, maxTokens);
		}

		// Philosophy
		for (int i = 0; i < philosophyQuestions.size(); i++)
		{
			startAnswerRating(modelname, "Philosophy", philosophyQuestions.get(i), i + 1, temperature, iterations, maxTokens);
		}
	}

	public void startAnswerRating(final String modelName, String category, String question, int questionNumber,
			final double temperature, final int iterations, final int maxTokens)
	{
		String promptMessage = question;
		String suggestion = "Answer the question"; //starting message
		AnswerEvaluation answerEvaluation = null;

		String userMessage = SystemPromptMessages.ANWSER_EVALUATION_WITHSUGGESTION;

		for (int i = 0; i < iterations; i++)
		{
			//send message to local model
			ModelResponse pMessage = this.sendPrompt(modelName, suggestion, promptMessage, 0.7, maxTokens, false);

			//get message
			String spMessage = pMessage.getContent();

			CsvUtlis.saveStringToCsv(spMessage, category + questionNumber, i);

			//build rating message
			List<Message> messages =
					new MessageListBuilder().system(userMessage + "The question is: " + question).user(spMessage)
					.build();

			//build request
			ChatCompletionRequest request =
					ChatCompletionRequest.builder().model("mistral-large-latest").temperature(temperature)
					.messages(messages).safePrompt(false).build();

			//send request to mistral
			ChatCompletionResponse response = mistralClient.createChatCompletion(request);

			Message firstChoice = response.getChoices().get(0).getMessage();
			System.out.println(firstChoice.getRole() + ":\n" + firstChoice.getContent() + "\n");
			answerEvaluation = parseAnswerEvaluationJsonString(firstChoice);
			if (answerEvaluation == null)
			{
				continue;
			}
			CsvUtlis.saveRatingToCSV(answerEvaluation, i, category + questionNumber);

			promptMessage = "You should correct this answer to make it better: " + spMessage;

			if (answerEvaluation != null && answerEvaluation.getSuggestion() != null)
			{
				suggestion = answerEvaluation.getSuggestion();
			}
		}
		if (answerEvaluation == null)
		{
			throw new RuntimeException("Error while parsing the JSON string");
		}
	}

	private ChatCompletionResponse sendRequestWithRetry(ChatCompletionRequest request, int retries)
	{
		for (int i = 0; i < retries; i++)
		{
			try
			{
				return mistralClient.createChatCompletion(request);
			}
			catch (MistralAPIException e)
			{
				if (i == retries - 1)
				{
					throw e;
				}
				System.out.println("Retrying... Attempt: " + (i + 1));
			}
		}
		return null;
	}
}
