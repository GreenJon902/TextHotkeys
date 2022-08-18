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
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static com.greenjon902.texthotkeys.client.ConfigFile.configFile;
import com.greenjon902.texthotkeys.client.ConfigFile;

@Environment(EnvType.CLIENT)
public class TextHotkeysClient implements ClientModInitializer {
    private KeyBinding[] keyBindings = new KeyBinding[0];

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
            keyBindings = HotkeyRegisterer.register();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert client.player != null;

            // Open Config
            while (openConfigBinding.wasPressed()) {
                client.player.sendMessage(Text.translatable("chat.config.open"), false);
                ConfigFile.checkFile();

                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("notepad.exe " + configFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    client.player.sendMessage(Text.translatable("chat.config.open.failed"), false);
                }
            }

            // Reload
            while (reloadKeyBinding.wasPressed()) {
                client.player.sendMessage(Text.translatable("chat.config.reload"), false);
                try {
                    keyBindings = HotkeyRegisterer.register();
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

            for (KeyBinding keyBinding : keyBindings) {
                while (keyBinding.wasPressed()) {
                    String hotkeyValue = keyBinding.getTranslationKey();

                    if (hotkeyValue.startsWith("/")) {
                        client.player.sendMessage(Text.translatable("chat.run.command", hotkeyValue), false);
                        client.player.sendCommand(hotkeyValue.replaceFirst("/", ""));
                    } else {
                        client.player.sendMessage(Text.translatable("chat.run.message", hotkeyValue), false);
                        client.player.sendChatMessage(hotkeyValue, null);
                    }
                }
            }
        });
    }
}
