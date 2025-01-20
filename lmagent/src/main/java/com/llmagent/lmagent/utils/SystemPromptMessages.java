package com.llmagent.lmagent.utils;

public class SystemPromptMessages
{
	final static public String MISTRAL_RATING_MESSAGE = """
			{
			    "prompt": "Rate the given story on a scale of 1-100 for the following aspects: cohesion, number of mistakes, text length, emotion, and engagement. Provide your response in the following JSON format:
			    {
			        "cohesion": <rating (1-10)>,
			        "mistakes": <rating (1-10)>,
			        "text_length": <rating (1-10)>,
			        "emotion": <rating (1-10)>,
			        "engagement": <rating (1-10)>
			    }
			    Ensure your ratings are well-reasoned, considering the following:
			    - Cohesion: How well do the ideas and events flow together?
			    - Mistakes: How many grammatical or spelling errors are present? (A higher score means fewer mistakes.)
			    - Text Length: Is the story appropriately long for its purpose?
			    - Emotion: How effectively does the story evoke feelings or emotions?
			    - Engagement: How captivating or interesting is the story for the reader?
			    Provide your ratings based on these criteria, ensuring alignment with common language and storytelling standards."
			}
			""";

	final static public String MISTRAL_RATING_MESSAGE_WITHOUT_EXPLANATION_WITH_SUGGESTION = """

			    Rate the given story on a scale of 1-10 for the following aspects: cohesion, mistakes, characters, emotion, and engagement. Also provide sugestion for making it better. Response only using json. Provide your response in the following JSON format:
			    {
			        "cohesion": <rating (1-10)>,
			        "mistakes": <rating (1-10)>,
			        "characters": <rating (1-10)>,
			        "emotion": <rating (1-10)>,
			        "engagement": <rating (1-10)>
			        "suggestion": <suggestion>
			    }
			""";

	final static public String ANWSER_EVALUATION_WITHSUGGESTION = """
			{
			    "prompt": "Rate the given answer on a scale of 1-10 for the following aspects: truthfulness, development, relevance to topic. Also provide a suggestion for improvement. Provide your response in the following JSON format and use ONLY JSON TO ANSWER:
			    {
			        "truthfulness": <rating (1-10)>,
			        "development": <rating (1-10)>,
			        "relevanceToTopic": <rating (1-10)>,
			        "suggestion": <suggestion>
			    }
			    Ensure your ratings are well-reasoned, considering the following:
			    - Truthfulness: How accurate and honest is the answer?
			    - Development: How well is the answer developed and supported?
			    - Relevance to Topic: How well does the answer address the topic or question?
			}
			""";
}
