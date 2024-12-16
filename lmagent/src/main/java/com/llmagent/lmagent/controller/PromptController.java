package com.llmagent.lmagent.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.llmagent.lmagent.model.ModelResponse;
import com.llmagent.lmagent.model.PromptPayload;
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
}
