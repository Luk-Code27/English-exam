package de.luk.examslides;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExamSlidesClient implements ClientModInitializer {
	public static final Logger LOG = LoggerFactory.getLogger("exam_slides");

	private static final KeyMapping.Category CATEGORY =
			KeyMapping.Category.register(Identifier.fromNamespaceAndPath("exam_slides", "main"));

	private static KeyMapping next, prev, manager;
	private static final KeyMapping[] COMMAND_KEYS = new KeyMapping[5];

	@Override
	public void onInitializeClient() {
		next = register("key.exam_slides.next", InputConstants.KEY_RIGHT);
		prev = register("key.exam_slides.prev", InputConstants.KEY_LEFT);
		manager = register("key.exam_slides.manager", InputConstants.KEY_F7);
		for (int i = 0; i < COMMAND_KEYS.length; i++) {
			COMMAND_KEYS[i] = register("key.exam_slides.command" + (i + 1), InputConstants.UNKNOWN.getValue());
		}
		ClientTickEvents.END_CLIENT_TICK.register(ExamSlidesClient::onTick);
	}

	private static KeyMapping register(String name, int key) {
		return KeyMappingHelper.registerKeyMapping(new KeyMapping(name, key, CATEGORY));
	}

	private static void onTick(Minecraft mc) {
		if (mc.player == null) return;
		while (next.consumeClick()) runCommand("function exam:next");
		while (prev.consumeClick()) runCommand("function exam:prev");
		while (manager.consumeClick()) mc.setScreen(new ManagerScreen(null));
		for (int i = 0; i < COMMAND_KEYS.length; i++) {
			while (COMMAND_KEYS[i].consumeClick()) {
				String cmd = ExamLibrary.get().config().commands.get(i);
				if (cmd == null || cmd.isBlank()) {
					message("Befehlstaste " + (i + 1) + " hat noch keinen Befehl (F7 -> Befehle).");
				} else {
					runCommand(cmd);
				}
			}
		}
	}

	public static void runCommand(String command) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return;
		String c = command.trim();
		if (c.startsWith("/")) c = c.substring(1);
		mc.player.connection.sendCommand(c);
	}

	public static void message(String text) {
		Minecraft mc = Minecraft.getInstance();
		mc.gui.getChat().addMessage(Component.literal("[Exam Slides] " + text));
	}
}
