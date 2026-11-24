package de.luk.examslides;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Builds the resource pack folder "exam_slides_live" from the images in the library.
 * Slides become exam:slide_1..N, pictures exam:image_1..M (same format as the datapack expects).
 */
public final class PackBuilder {
	public static final String PACK_FOLDER = "exam_slides_live";
	private static final int SIZE = 1024;

	private PackBuilder() {}

	public static void build(Path library, Path packDir, List<String> slides, List<String> images) throws IOException {
		Path assets = packDir.resolve("assets/exam");
		deleteTree(assets);
		Path tex = Files.createDirectories(assets.resolve("textures/item"));
		Path models = Files.createDirectories(assets.resolve("models/item"));
		Path items = Files.createDirectories(assets.resolve("items"));
		Files.writeString(packDir.resolve("pack.mcmeta"), """
				{
				  "pack": {
				    "description": "Exam slides (made by the Exam Slides mod)",
				    "min_format": [97, 0],
				    "max_format": 200
				  }
				}
				""");

		for (int i = 0; i < slides.size(); i++) {
			BufferedImage src = read(library.resolve(slides.get(i)));
			// Slide: always 16:9, letterboxed into 1024x576 at the top of a 1024x1024 canvas
			BufferedImage canvas = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = canvas.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setColor(java.awt.Color.BLACK);
			g.fillRect(0, 0, SIZE, 576);
			double scale = Math.min(SIZE / (double) src.getWidth(), 576 / (double) src.getHeight());
			int w = (int) Math.round(src.getWidth() * scale), h = (int) Math.round(src.getHeight() * scale);
			g.drawImage(src, (SIZE - w) / 2, (576 - h) / 2, w, h, null);
			g.dispose();
			String name = "slide_" + (i + 1);
			ImageIO.write(canvas, "png", tex.resolve(name + ".png").toFile());
			writeModel(models, items, name, 16, 9, 16, 9);
		}

		for (int i = 0; i < images.size(); i++) {
			BufferedImage src = read(library.resolve(images.get(i)));
			double scale = Math.min(SIZE / (double) src.getWidth(), SIZE / (double) src.getHeight());
			int w = Math.max(1, (int) Math.round(src.getWidth() * scale));
			int h = Math.max(1, (int) Math.round(src.getHeight() * scale));
			BufferedImage canvas = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = canvas.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawImage(src, 0, 0, w, h, null);
			g.dispose();
			String name = "image_" + (i + 1);
			ImageIO.write(canvas, "png", tex.resolve(name + ".png").toFile());
			// element size keeps the aspect ratio, longest side = 16
			double ew = 16.0 * w / SIZE, eh = 16.0 * h / SIZE;
			writeModel(models, items, name, ew, eh, ew, eh);
		}
	}

	private static void writeModel(Path models, Path items, String name, double ew, double eh, double u, double v) throws IOException {
		double x0 = 8 - ew / 2, x1 = 8 + ew / 2, y0 = 8 - eh / 2, y1 = 8 + eh / 2;
		String uv = String.format(Locale.ROOT, "[0, 0, %.4f, %.4f]", u, v);
		String model = String.format(Locale.ROOT, """
				{
				  "textures": { "slide": "exam:item/%1$s", "particle": "exam:item/%1$s" },
				  "elements": [{
				    "from": [%2$.4f, %3$.4f, 7.9], "to": [%4$.4f, %5$.4f, 8.1], "shade": false,
				    "faces": {
				      "north": { "uv": %6$s, "texture": "#slide" },
				      "south": { "uv": %6$s, "texture": "#slide" }
				    }
				  }]
				}
				""", name, x0, y0, x1, y1, uv);
		Files.writeString(models.resolve(name + ".json"), model);
		Files.writeString(items.resolve(name + ".json"),
				"{ \"model\": { \"type\": \"minecraft:model\", \"model\": \"exam:item/" + name + "\" } }\n");
	}

	private static BufferedImage read(Path file) throws IOException {
		BufferedImage img = ImageIO.read(file.toFile());
		if (img == null) throw new IOException("Not an image: " + file.getFileName());
		return img;
	}

	private static void deleteTree(Path dir) throws IOException {
		if (!Files.exists(dir)) return;
		try (Stream<Path> s = Files.walk(dir)) {
			for (Path p : s.sorted(Comparator.reverseOrder()).toList()) Files.delete(p);
		}
	}
}
