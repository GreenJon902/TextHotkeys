package com.greenjon902.texthotkeys.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.tinyremapper.extension.mixin.common.data.Message;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

@Environment(EnvType.CLIENT)
public class TextHotkeysClient implements ClientModInitializer {
    static File configFile = FabricLoader.getInstance().getConfigDir().resolve("textHotkeys.txt").toFile();
    static URL defaultConfigFile = TextHotkeysClient.class.getResource("/textHotkeys.txt");

    private final Map<KeyBinding, String> keyBindings = new HashMap<>();
    private final Map<String, String> hotkeyInformation = new HashMap<>();

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


        // Make key bindings for menu
        try {
            reload();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        for (String hotkeyName : hotkeyInformation.keySet()) {
            keyBindings.put(KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    hotkeyName,
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "category.textHotkeys.commands"
            )), hotkeyName);
        }


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert client.player != null;

            // Open Config
            while (openConfigBinding.wasPressed()) {
                client.player.sendMessage(Text.translatable("chat.config.open"), false);
                check_file();

                try {
                    if (Desktop.isDesktopSupported()) Desktop.getDesktop().edit(configFile);
                    else {
                        System.out.println("Could not get desktop, attempting alternatives");
                        Runtime runtime = Runtime.getRuntime();
                        if (SystemUtils.IS_OS_WINDOWS)
                            runtime.exec("notepad.exe " + configFile.getAbsolutePath());
                        else if (SystemUtils.IS_OS_LINUX)
                            runtime.exec("x-terminal-emulator -e nano " + configFile.getAbsolutePath());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    client.player.sendMessage(Text.translatable("chat.config.open.failed"), false);
                }
            }

            // Reload
            while (reloadKeyBinding.wasPressed()) {
                client.player.sendMessage(Text.translatable("chat.config.reload"), false);
                try {
                    boolean needRestart = reload();
                    if (needRestart) {
                        client.player.sendMessage(Text.translatable("chat.config.reload.needRestart"));
                    }
                } catch (Exception e) {
                    client.player.sendMessage(Text.translatable("chat.error"), false);
                }
                client.player.sendMessage(Text.translatable("chat.config.reload.finished"), false);
            }
        });

        startMainloop();
    }

    private void startMainloop() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert client.player != null;

            for (KeyBinding keyBinding : keyBindings.keySet()) {
                while (keyBinding.wasPressed()) {
                    String name = keyBindings.get(keyBinding);
                    String value = hotkeyInformation.get(name);

                    if (value.startsWith("/")) {
                        client.player.sendMessage(Text.translatable("chat.run.command", name), false);
                        client.player.networkHandler.sendChatCommand(value.replaceFirst("/", ""));
                    } else {
                        client.player.sendMessage(Text.translatable("chat.run.message", name), false);
                        client.player.networkHandler.sendChatMessage(value);
                    }
                }
            }
        });
    }

    /**
     * Reload the hotkey information
     * @return Does the game need restarting
     */
    private boolean reload() throws FileNotFoundException {
        System.out.println("Attempting reloading");
        Map<String, String> newHotkeyInformation = loadHotkeyInformation();

        boolean needRestart = !hotkeyInformation.keySet().equals(newHotkeyInformation.keySet());
        hotkeyInformation.putAll(newHotkeyInformation); // Right-union of old and new hotkey informations

        return needRestart;
    }

    /**
     * Loads the hotkey names and their message/command
     *
     * @return A map of hotkey names to their messages/commands
     */
    private Map<String, String> loadHotkeyInformation() throws FileNotFoundException {
        System.out.println("Loading config file!");
        Map<String, String> newHotkeyInformation = new HashMap<>();

        check_file();

        Scanner scanner = new Scanner(configFile);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.startsWith("#")) {

                String hotkeyName, hotkeyValue;
                String[] hotkeyParts = line.split("=", 2);
                hotkeyName = hotkeyParts[0];
                hotkeyValue = hotkeyParts[1];

                newHotkeyInformation.put(hotkeyName, hotkeyValue);
                System.out.println("Found keybinding for key " + hotkeyName + " which runs \"" + hotkeyValue + "\"");
            }
        }

        return newHotkeyInformation;
    }

    /**
     * Checks whether the config file is there
     */
    private void check_file() {
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
