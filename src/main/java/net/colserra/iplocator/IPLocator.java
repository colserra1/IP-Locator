package net.colserra.iplocator;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPLocator implements ModInitializer {
	public static final String MOD_ID = "ip-locator";
	private static final String IPV4_PATTERN =
			"(?:\\d{1,3}\\.){3}\\d{1,3}";
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		MinecraftClient client = MinecraftClient.getInstance();

		ClientReceiveMessageEvents.CHAT.register((message, signed_message, sender, params, timestamp) -> {
			onChatMessage(message, client.player);
		});
		ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
			onChatMessage(message, client.player);
		});
	}

	private void onChatMessage(Text message, PlayerEntity player) {
		String chatContent = message.getString();

		if (containsIpAddress(chatContent)&&!chatContent.contains("Detected IP:")) {
			String ip = extractFirstIPAddress(chatContent);
			System.out.println("Detected IP: " + ip);
			player.sendMessage(Text.of("Detected IP: https://spur.us/context/" + ip), false);
        }
	}

	public static boolean containsIpAddress(String input) {
		Pattern pattern = Pattern.compile(IPV4_PATTERN);
		Matcher matcher = pattern.matcher(input);
		return matcher.find();
	}

	public static String extractFirstIPAddress(String text) {
		// Compile patterns
		Pattern ipv4Pattern = Pattern.compile(IPV4_PATTERN);

		// Match IPv4 addresses
		Matcher ipv4Matcher = ipv4Pattern.matcher(text);
		if (ipv4Matcher.find()) {
			return ipv4Matcher.group();
		}

		// Return null if no IP address is found
		return "No IP Address found";
	}
}
