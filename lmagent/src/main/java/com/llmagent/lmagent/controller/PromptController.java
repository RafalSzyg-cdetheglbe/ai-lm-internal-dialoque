package com.llmagent.lmagent.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.llmagent.lmagent.model.ModelResponse;
import com.llmagent.lmagent.model.PromptPayload;
import com.llmagent.lmagent.model.StoryRating;
import com.llmagent.lmagent.service.PromptService;

@RestController
public class PromptController
{
	private final PromptService promptService;

	public PromptController(PromptService promptService)
	{
		this.promptService = promptService;
	}

	@GetMapping("/prompt")
	public ModelResponse prompt(@RequestBody PromptPayload payload)
	{
		return promptService.sendPrompt(payload.getModelName(), //
				payload.getSystemMessage(), //
				payload.getUserMessage(), //
				payload.getTemperature(), //
				payload.getMaxTokens(), //
				payload.isStream());
	}

	@PostMapping("/startHistoryMakingPipeline")
	public String startHistoryMakingPipeline(@RequestBody PromptPayload payload)
	{
		return promptService.startHistoryMakingPipeline(payload.getNumberOfIterations(), //
				payload.getModelName(), //
				payload.getSystemMessage(), //
				payload.getUserMessage(), //
				payload.getTemperature(), //
				payload.getMaxTokens(), //
				payload.isStream());
	}

	@PostMapping("/startRatingPipeline")
	public String startRatingPipeline(@RequestBody PromptPayload payload)
	{
		return promptService.startRatingPipeline(payload.getNumberOfIterations(), //
				payload.getModelName(), //
				payload.getSystemMessage(), //
				payload.getUserMessage(), //
				payload.getTemperature(), //
				payload.getMaxTokens(), //
				payload.isStream());
	}

	@PostMapping("/startRatingMistralPipeline")
	public String startRatingMistralPipeline(@RequestBody PromptPayload payload)
	{
		return promptService.startRatingMistralPipeline(payload.getSystemMessage(), payload.getUserMessage(),
				payload.getModelName(), payload.getTemperature());
	}

	@PostMapping("/startIterativeRatingMistralPipeline")
	public StoryRating startIterativeRatingMistralPipeline(@RequestBody PromptPayload payload)
	{
		return promptService.startIterativeRatingMistralPipeline(payload.getUserMessage(),
				payload.getModelName(), payload.getTemperature(), payload.getNumberOfIterations(), payload.getMaxTokens(), payload.getFileName());
	}

	@PostMapping("/startIterativeRatingOnlyMistralPipeline")
	public StoryRating startIterativeRatingOnlyMistralPipeline(@RequestBody PromptPayload payload)
	{
		return promptService.startIterativeRatingOnlyMistralPipeline(payload.getUserMessage(),
				payload.getModelName(), payload.getTemperature(), payload.getNumberOfIterations(), payload.getMaxTokens(), payload.getSystemMessage(), payload.getFileName());
	}

	@PostMapping("/startAnsweringPipeline")
	public HttpStatus startAnsweringRating(@RequestBody PromptPayload payload)
	{
		promptService.startAnwseringIterations(payload.getModelName(), payload.getTemperature(), payload.getNumberOfIterations(),
				payload.getMaxTokens());
		return HttpStatus.OK;
	}

}
