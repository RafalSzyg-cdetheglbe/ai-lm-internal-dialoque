package com.llmagent.lmagent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.llmagent.lmagent.model.ModelResponse;
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
	public ModelResponse prompt()
	{
		return promptService.sendPrompt("TheBloke/Mistral-7B-Instruct-v0.2-GGUF", //
				"Powinieneś skonczyć historię tak aby mogła być kontynuowana", //
				"Napisz mi historię o księżniczce i kurwie", 0.7, //
				-1, false);
	}
}

