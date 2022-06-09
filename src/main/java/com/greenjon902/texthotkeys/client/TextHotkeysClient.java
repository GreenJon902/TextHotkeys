package com.greenjon902.texthotkeys.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.apache.commons.io.FileUtils;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

@Environment(EnvType.CLIENT)
public class TextHotkeysClient implements ClientModInitializer {
    static File configFile = FabricLoader.getInstance().getConfigDir().resolve("textHotkeys.txt").toFile();
    static URL defaultConfigFile = TextHotkeysClient.class.getResource("/textHotkeys.txt");

    private final HashMap<String, String> keyBindingCommands = new HashMap<>();
    private final ArrayList<KeyBinding> keyBindings = new ArrayList<>();

    @Override
    public void onInitializeClient() {

        KeyBinding reloadKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.textHotkeys.reload",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.textHotkeys"
        ));

        KeyBinding openConfigBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.textHotkeys.openConfig",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.textHotkeys"
        ));

        reload();

        for (String commandName : keyBindingCommands.keySet()) {
            keyBindings.add(KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    commandName,
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "category.textHotkeys.commands"
            )));
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert client.player != null;

            while (openConfigBinding.wasPressed()) {
                client.player.sendMessage(Text.translatable("chat.config.open"), false);
                check_file();

                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("notepad.exe " + configFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    client.player.sendMessage(Text.translatable("chat.config.open.failed"), false);
                }
            }

            while (reloadKeyBinding.wasPressed()) {
                client.player.sendMessage(Text.translatable("chat.config.reload"), false);
                check_file();
                reload();
                client.player.sendMessage(Text.translatable("chat.config.reload.finished"), false);
            }

            for (KeyBinding keyBinding : keyBindings) {
                while (keyBinding.wasPressed()) {
                    if (keyBindingCommands.get(keyBinding.getTranslationKey()).startsWith("/")) {
                        client.player.sendMessage(Text.translatable("chat.run.command", keyBinding.getTranslationKey()), false);
                        client.player.sendCommand(keyBindingCommands.get(keyBinding.getTranslationKey()).replaceFirst("/", ""));
                    } else {
                        client.player.sendMessage(Text.translatable("chat.run.message", keyBinding.getTranslationKey()), false);
                        client.player.sendChatMessage(keyBindingCommands.get(keyBinding.getTranslationKey()));
                    }
                }
            }
        });
    }

    public void reload() {
        System.out.println("Reloading config file!");
        keyBindingCommands.clear();

        check_file();
        try {
            Scanner scanner = new Scanner(configFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.startsWith("#")) {

                    String commandName, command;
                    String[] commandParts = line.split("=", 2);
                    commandName = commandParts[0];
                    command = commandParts[1];

                    keyBindingCommands.put(commandName, command);
                    System.out.println("Found keybinding for key " + commandName + " which runs \"" + command + "\"");
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
