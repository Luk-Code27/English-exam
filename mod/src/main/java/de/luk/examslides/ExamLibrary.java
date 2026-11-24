package de.luk.examslides;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.PackRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;

/** Keeps the slide and picture files in <instance>/exam_slides/library and builds the resource pack. */
public final class ExamLibrary {
	private static ExamLibrary instance;

	private final Path root;
	private final Path library;
	private final ExamConfig config;

	private ExamLibrary(Path gameDir) {
		this.root = gameDir.resolve("exam_slides");
		this.library = root.resolve("library");
		this.config = ExamConfig.load(root.resolve("config.json"));
	}

	public static ExamLibrary get() {
		if (instance == null) instance = new ExamLibrary(Minecraft.getInstance().gameDirectory.toPath());
		return instance;
	}

	public ExamConfig config() { return config; }
	public Path folder() { return root; }

	public Path importFolder() { return root.resolve("import"); }

	/** Imports all images from <instance>/exam_slides/import (sorted by name) and deletes them there. */
	public int importFromFolder(boolean asSlides) {
		Path dir = importFolder();
		try {
			Files.createDirectories(dir);
			List<Path> files;
			try (var s = Files.list(dir)) { files = s.filter(Files::isRegularFile).sorted().toList(); }
			int n = addFiles(files, asSlides);
			for (Path f : files) {
				String l = f.getFileName().toString().toLowerCase(Locale.ROOT);
				if (l.endsWith(".png") || l.endsWith(".jpg") || l.endsWith(".jpeg")) Files.deleteIfExists(f);
			}
			return n;
		} catch (IOException e) {
			ExamSlidesClient.LOG.error("Import failed", e);
			return 0;
		}
	}

	public void save() { config.save(root.resolve("config.json")); }

	/** Copies image files into the library and adds them as slides or pictures. Returns how many were added. */
	public int addFiles(List<Path> files, boolean asSlides) {
		int added = 0;
		for (Path f : files) {
			String name = f.getFileName().toString();
			String lower = name.toLowerCase(Locale.ROOT);
			if (!(lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg"))) continue;
			try {
				Files.createDirectories(library);
				String target = uniqueName(name);
				Files.copy(f, library.resolve(target), StandardCopyOption.REPLACE_EXISTING);
				(asSlides ? config.slides : config.images).add(target);
				added++;
			} catch (IOException e) {
				ExamSlidesClient.LOG.error("Could not copy " + f, e);
			}
		}
		save();
		return added;
	}

	private String uniqueName(String name) {
		String n = name;
		int i = 2;
		while (Files.exists(library.resolve(n))) {
			int dot = name.lastIndexOf('.');
			n = name.substring(0, dot) + "_" + i++ + name.substring(dot);
		}
		return n;
	}

	public void move(List<String> list, int index, int delta) {
		int j = index + delta;
		if (index < 0 || j < 0 || index >= list.size() || j >= list.size()) return;
		String a = list.get(index);
		list.set(index, list.get(j));
		list.set(j, a);
		save();
	}

	public void remove(List<String> list, int index) {
		if (index < 0 || index >= list.size()) return;
		list.remove(index);
		save();
	}

	/** Builds the resource pack, switches it on, reloads textures and tells the datapack how many slides exist. */
	public void apply() {
		Minecraft mc = Minecraft.getInstance();
		try {
			Path packDir = mc.getResourcePackDirectory().resolve(PackBuilder.PACK_FOLDER);
			PackBuilder.build(library, packDir, config.slides, config.images);
		} catch (IOException e) {
			ExamSlidesClient.LOG.error("Could not build pack", e);
			ExamSlidesClient.message("Fehler beim Bauen: " + e.getMessage());
			return;
		}
		PackRepository repo = mc.getResourcePackRepository();
		repo.reload();
		String id = "file/" + PackBuilder.PACK_FOLDER;
		if (!repo.getSelectedIds().contains(id)) repo.addPack(id);
		mc.options.updateResourcePacks(repo);
		if (!config.slides.isEmpty()) {
			ExamSlidesClient.runCommand("scoreboard players set #max exam.slide " + config.slides.size());
		}
		ExamSlidesClient.message(config.slides.size() + " Folien und " + config.images.size() + " Bilder übernommen.");
	}
}
