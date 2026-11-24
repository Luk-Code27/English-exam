package de.luk.examslides;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** The slide manager: F7 in game. */
public class ManagerScreen extends Screen {
	private static final int PER_PAGE = 6;
	private static int tab = 0; // 0 = slides, 1 = pictures, 2 = command keys
	private static int page = 0;

	private final Screen parent;
	private final List<EditBox> commandBoxes = new ArrayList<>();

	public ManagerScreen(Screen parent) {
		super(Component.literal("Exam Slides"));
		this.parent = parent;
	}

	private ExamLibrary lib() { return ExamLibrary.get(); }

	private List<String> currentList() {
		return tab == 0 ? lib().config().slides : lib().config().images;
	}

	@Override
	protected void init() {
		commandBoxes.clear();
		int cx = this.width / 2;
		int y = 10;
		addRenderableWidget(new StringWidget(cx - 150, y, 300, 12,
				Component.literal("Exam Slides - Folien-Manager"), this.font));
		y += 16;
		String[] tabs = {"Folien", "Bilder", "Befehlstasten"};
		for (int i = 0; i < 3; i++) {
			final int t = i;
			Button b = Button.builder(Component.literal(tabs[i]), btn -> { tab = t; page = 0; rebuildWidgets(); })
					.bounds(cx - 150 + i * 102, y, 96, 20).build();
			b.active = tab != i;
			addRenderableWidget(b);
		}
		y += 28;

		if (tab == 2) {
			initCommands(cx, y);
		} else {
			initList(cx, y);
		}

		addRenderableWidget(Button.builder(Component.literal("Fertig"), btn -> onClose())
				.bounds(cx + 54, this.height - 28, 96, 20).build());
	}

	private void initList(int cx, int y) {
		List<String> list = currentList();
		int pages = Math.max(1, (list.size() + PER_PAGE - 1) / PER_PAGE);
		if (page >= pages) page = pages - 1;
		if (list.isEmpty()) {
			addRenderableWidget(new StringWidget(cx - 150, y + 4, 300, 12,
					Component.literal(tab == 0 ? "Noch keine Folien." : "Noch keine Bilder."), this.font));
		}
		for (int k = 0; k < PER_PAGE; k++) {
			int idx = page * PER_PAGE + k;
			if (idx >= list.size()) break;
			int ry = y + k * 24;
			String label = (idx + 1) + ". " + list.get(idx);
			if (label.length() > 28) label = label.substring(0, 27) + "...";
			addRenderableWidget(new StringWidget(cx - 150, ry + 5, 150, 12, Component.literal(label), this.font));
			int bx = cx + 2;
			if (tab == 0) {
				addRenderableWidget(Button.builder(Component.literal("Zeigen"),
						btn -> { ExamSlidesClient.runCommand("function exam:goto {n:" + (idx + 1) + "}"); onClose(); })
						.bounds(bx, ry, 46, 20).build());
			} else {
				addRenderableWidget(Button.builder(Component.literal("Platzieren"),
						btn -> { ExamSlidesClient.runCommand("function exam:image/place {n:" + (idx + 1) + "}"); onClose(); })
						.bounds(bx, ry, 46, 20).build());
			}
			addRenderableWidget(Button.builder(Component.literal("^"), btn -> { lib().move(list, idx, -1); rebuildWidgets(); })
					.bounds(bx + 50, ry, 20, 20).build());
			addRenderableWidget(Button.builder(Component.literal("v"), btn -> { lib().move(list, idx, 1); rebuildWidgets(); })
					.bounds(bx + 72, ry, 20, 20).build());
			addRenderableWidget(Button.builder(Component.literal("X"), btn -> { lib().remove(list, idx); rebuildWidgets(); })
					.bounds(bx + 94, ry, 20, 20).build());
		}
		int py = y + PER_PAGE * 24 + 2;
		if (pages > 1) {
			addRenderableWidget(Button.builder(Component.literal("<"), btn -> { page = Math.max(0, page - 1); rebuildWidgets(); })
					.bounds(cx - 150, py, 20, 20).build());
			addRenderableWidget(new StringWidget(cx - 126, py + 5, 60, 12,
					Component.literal("Seite " + (page + 1) + "/" + pages), this.font));
			addRenderableWidget(Button.builder(Component.literal(">"), btn -> { page = Math.min(pages - 1, page + 1); rebuildWidgets(); })
					.bounds(cx - 62, py, 20, 20).build());
		}
		addRenderableWidget(new StringWidget(cx - 150, this.height - 66, 300, 12,
				Component.literal("Tipp: PNG/JPG-Dateien einfach in dieses Fenster ziehen."), this.font));
		addRenderableWidget(Button.builder(Component.literal("Dateien wählen..."), btn -> openFileDialog())
				.bounds(cx - 150, this.height - 52, 146, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Übernehmen"), btn -> lib().apply())
				.bounds(cx + 4, this.height - 52, 146, 20).build());
		if (tab == 0) {
			addRenderableWidget(Button.builder(Component.literal("Leinwand hier"),
					btn -> { ExamSlidesClient.runCommand("function exam:screen/place"); onClose(); })
					.bounds(cx - 150, this.height - 28, 96, 20).build());
		} else {
			addRenderableWidget(Button.builder(Component.literal("Bild entfernen"),
					btn -> { ExamSlidesClient.runCommand("function exam:image/remove"); onClose(); })
					.bounds(cx - 150, this.height - 28, 96, 20).build());
		}
	}

	private void initCommands(int cx, int y) {
		addRenderableWidget(new StringWidget(cx - 150, y, 300, 12,
				Component.literal("Befehl pro Taste (ohne /). Tasten: Optionen > Steuerung."), this.font));
		y += 18;
		List<String> cmds = lib().config().commands;
		for (int i = 0; i < 5; i++) {
			addRenderableWidget(new StringWidget(cx - 150, y + 5 + i * 24, 40, 12, Component.literal("Taste " + (i + 1)), this.font));
			EditBox box = new EditBox(this.font, cx - 105, y + i * 24, 255, 20, Component.literal("Befehl " + (i + 1)));
			box.setMaxLength(256);
			box.setValue(cmds.get(i));
			commandBoxes.add(addRenderableWidget(box));
		}
	}

	private void saveCommands() {
		if (commandBoxes.isEmpty()) return;
		List<String> cmds = lib().config().commands;
		for (int i = 0; i < commandBoxes.size(); i++) cmds.set(i, commandBoxes.get(i).getValue());
		lib().save();
	}

	@Override
	protected void rebuildWidgets() {
		saveCommands();
		super.rebuildWidgets();
	}

	@Override
	public void onFilesDrop(List<Path> files) {
		if (tab == 2) tab = 0;
		int n = lib().addFiles(files, tab == 0);
		ExamSlidesClient.message(n + " Datei(en) hinzugefügt. Zum Anzeigen \"Übernehmen\" drücken.");
		rebuildWidgets();
	}

	/** Opens the Windows file picker via LWJGL tinyfd if available (called by reflection so the mod still loads without it). */
	private void openFileDialog() {
		final boolean asSlides = tab == 0;
		Thread t = new Thread(() -> {
			try {
				Class<?> c = Class.forName("org.lwjgl.util.tinyfd.TinyFileDialogs");
				Method m = null;
				for (Method mm : c.getMethods()) {
					if (mm.getName().equals("tinyfd_openFileDialog") && mm.getParameterCount() == 5
							&& CharSequence.class.isAssignableFrom(mm.getParameterTypes()[0])) {
						m = mm;
						break;
					}
				}
				if (m == null) throw new IllegalStateException("no tinyfd");
				String res = (String) m.invoke(null, "Bilder auswählen", "", null, "Bilder (png, jpg)", true);
				if (res == null || res.isBlank()) return;
				List<Path> files = new ArrayList<>();
				for (String s : res.split("\\|")) files.add(Path.of(s));
				Minecraft.getInstance().execute(() -> {
					int n = lib().addFiles(files, asSlides);
					ExamSlidesClient.message(n + " Datei(en) hinzugefügt. Zum Anzeigen \"Übernehmen\" drücken.");
					if (Minecraft.getInstance().screen instanceof ManagerScreen s) s.rebuildWidgets();
				});
			} catch (Throwable e) {
				ExamSlidesClient.LOG.warn("File dialog not available", e);
				Minecraft.getInstance().execute(() -> ExamSlidesClient.message(
						"Dateiauswahl geht hier nicht. Bitte Dateien ins Fenster ziehen."));
			}
		}, "exam-slides-file-dialog");
		t.setDaemon(true);
		t.start();
	}

	@Override
	public void onClose() {
		saveCommands();
		this.minecraft.setScreen(parent);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
