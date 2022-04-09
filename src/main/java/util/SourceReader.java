package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SourceReader {
	private final String fileName;
	public SourceReader(String fileName) {
		this.fileName =fileName;
	}

	/**
	 * @return List of Last names inf the file fileName
	 */
	public List<String> loadNames() {
		List<String> lastNames = new ArrayList<String>();
		InputStream fileStream = getClass().getClassLoader().getResourceAsStream(this.fileName);
		try {
			assert fileStream != null;
			try (InputStreamReader inputStreamReader = new InputStreamReader(fileStream, StandardCharsets.UTF_8);
					 BufferedReader lineReader = new BufferedReader(inputStreamReader)) {
				String currentLastName;
				while ((currentLastName = lineReader.readLine()) != null) {
					lastNames.add(currentLastName);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lastNames;

	}

}
