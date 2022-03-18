package com.greenjon902.texthotkeys.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.commons.io.FileUtils;
import org.lwjgl.glfw.GLFW;

import javax.print.attribute.standard.JobKOctets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

@Environment(EnvType.CLIENT)
public class TextHotkeysClient implements ClientModInitializer {
    static File configFile = FabricLoader.getInstance().getConfigDir().resolve("textHotkeys.txt").toFile();
    static URL defaultConfigFile = TextHotkeysClient.class.getResource("/textHotkeys.txt");

    private final HashMap<Integer, String> keyBindings = new HashMap<>();
    private final ArrayList<Integer> currentPressed = new ArrayList<>();

    @Override
    public void onInitializeClient() {

        KeyBinding reloadKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.texthotkeys.reload",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.textHotkeys"
        ));

        KeyBinding openConfigBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.texthotkeys.openConfig",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.textHotkeys"
        ));

        reload();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert client.player != null;

            while (openConfigBinding.wasPressed()) {
                client.player.sendMessage(new TranslatableText("chat.config.open"), false);
                check_file();

                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("notepad.exe " + configFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    client.player.sendMessage(new TranslatableText("chat.config.open.failed"), false);
                }
            }

            while (reloadKeyBinding.wasPressed()) {
                client.player.sendMessage(new TranslatableText("chat.config.reload"), false);
                check_file();
                reload();
                client.player.sendMessage(new TranslatableText("chat.config.reload.finished"), false);
            }

            for (int key : keyBindings.keySet()) {
                if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), key)) {
                    if (!currentPressed.contains(key)) {
                        client.player.sendMessage(new TranslatableText("chat.run.command", keyBindings.get(key)), false);
                        client.player.sendChatMessage(keyBindings.get(key));

                        currentPressed.add(key);
                    }
                } else {
                    currentPressed.remove((Object)key);
                }
            }
        });
    }

    public void reload() {
        System.out.println("Reloading config file!");
        keyBindings.clear();

        check_file();
        try {
            Scanner scanner = new Scanner(configFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.startsWith("#")) {

                    int key;
                    String command;
                    String[] commandParts = line.split("=", 2);
                    key = Integer.parseInt(commandParts[0]);
                    command = commandParts[1];

                    keyBindings.put(key, command);
                    System.out.println("Found keybinding for key " + key + " which runs \"" + command + "\"");
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void check_file() {
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();

            try {
                FileUtils.copyURLToFile(defaultConfigFile, configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
