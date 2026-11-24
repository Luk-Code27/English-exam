package de.luk.examslides;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Saved in <instance>/exam_slides/config.json */
public class ExamConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public List<String> slides = new ArrayList<>();
	public List<String> images = new ArrayList<>();
	public List<String> commands = new ArrayList<>(List.of(
			"function exam:screen/place", "function exam:reset", "", "", ""));

	public static ExamConfig load(Path file) {
		try {
			if (Files.exists(file)) {
				ExamConfig c = GSON.fromJson(Files.readString(file), ExamConfig.class);
				if (c != null) {
					while (c.commands.size() < 5) c.commands.add("");
					return c;
				}
			}
		} catch (Exception e) {
			ExamSlidesClient.LOG.error("Could not read config", e);
		}
		return new ExamConfig();
	}

	public void save(Path file) {
		try {
			Files.createDirectories(file.getParent());
			Files.writeString(file, GSON.toJson(this));
		} catch (IOException e) {
			ExamSlidesClient.LOG.error("Could not save config", e);
		}
	}
}
