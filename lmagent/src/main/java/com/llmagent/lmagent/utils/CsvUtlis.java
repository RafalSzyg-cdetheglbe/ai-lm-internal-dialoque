package com.llmagent.lmagent.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.yaml.snakeyaml.reader.StreamReader;

public class CsvUtlis
{

	/**
	 * Saves a string to a CSV file with a "Generated Text" column and an empty "Rating" column.
	 *
	 * @param text The string to be saved.
	 * @param filePath The path of the CSV file to save the data.
	 */
	public static void saveStringToCsv(String text, String filePath)
	{
		try (FileWriter writer = new FileWriter(filePath))
		{
			writer.append("Generated Text,Rating\n");

			writer.append(escapeCsv(text)).append(",")  // Empty column for Rating.
					.append("\n");

			System.out.println("CSV file created successfully at: " + filePath);
		}
		catch (IOException e)
		{
			System.err.println("Error while writing to the CSV file: " + e.getMessage());
		}
	}

	/**
	 * Escapes special characters in the text for CSV compatibility.
	 *
	 * @param text The input text to escape.
	 * @return The escaped text.
	 */
	private static String escapeCsv(String text)
	{
		if (text.contains(",") || text.contains("\"") || text.contains("\n"))
		{
			text = text.replace("\"", "\"\"");
			return '"' + text + '"';
		}
		return text;
	}
}
