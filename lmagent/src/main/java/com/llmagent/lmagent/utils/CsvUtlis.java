package com.llmagent.lmagent.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.server.UID;

import com.llmagent.lmagent.model.StoryRating;


public class CsvUtlis
{

	/**
	 * Saves a string to a CSV file with a "Generated Text" column and an empty "Rating" column.
	 * If the file exists, appends the new string as a new row.
	 *
	 * @param text       The string to be saved.
	 * @param filePath   The path of the CSV file to save the data.
	 */
	public static void saveStringToCsv(String text, String filePath) {
		File csvFile = new File(filePath);
		boolean fileExists = csvFile.exists();

		try (FileWriter writer = new FileWriter(filePath, true)) {
			// If the file does not exist, write the header row.
			if (!fileExists) {
				writer.append("Generated Text,Rating\n");
			}

			// Write the content row.
			writer.append(escapeCsv(text))
					.append(",")  // Empty column for Rating.
					.append("\n");

			System.out.println("CSV file updated successfully at: " + filePath);
		} catch (IOException e) {
			System.err.println("Error while writing to the CSV file: " + e.getMessage());
		}
	}

	/**
	 * Escapes special characters in the text for CSV compatibility.
	 *
	 * @param text The input text to escape.
	 * @return The escaped text.
	 */
	private static String escapeCsv(String text) {
		if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
			text = text.replace("\"", "\"\"");
			return '"' + text + '"';
		}
		return text;
	}

	public static void saveRatingToCSV(StoryRating storyRating, int i) {
		// Define the CSV file path
		String csvFilePath = "testMistralExternal.csv";

		try {
			// Check if the file exists
			File csvFile = new File(csvFilePath);
			boolean isFileNew = !csvFile.exists();

			// Open the file in append mode
			try (FileWriter writer = new FileWriter(csvFilePath, true)) {
				// If the file is new, write the header first
				if (isFileNew) {
					writer.write("Id,Cohesion,Mistakes,Characters,Emotion,Engagement\n");
				}

				// Write the ratings data as a new row
				writer.write(String.format("%d,%d,%d,%d,%d,%d\n",
						i,
						storyRating.getCohesion(),
						storyRating.getMistakes(),
						storyRating.getCharacters(),
						storyRating.getEmotion(),
						storyRating.getEngagement()
				));
			}
		} catch (IOException e) {
			System.out.println("Error while writing to the CSV file: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
