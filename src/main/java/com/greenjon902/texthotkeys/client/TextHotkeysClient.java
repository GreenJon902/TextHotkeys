package com.greenjon902.texthotkeys.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import org.apache.commons.io.FileUtils;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Environment(EnvType.CLIENT)
public class TextHotkeysClient implements ClientModInitializer {

    static File configFile = FabricLoader.getInstance().getConfigDir().resolve("textHotkeys.ini").toFile();
    static URL defaultConfigFile = TextHotkeysClient.class.getResource("/textHotkeys.ini");

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
            while (openConfigBinding.wasPressed()) {
                assert client.player != null;

                client.player.sendMessage(new LiteralText("Opening config file..."), false);
                check_file();
                System.out.println(configFile.exists());

                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("notepad.exe " + configFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    client.player.sendMessage(new LiteralText("Failed to open config file!"), false);
                }

            }
        });
    }

    public void reload() {
        System.out.println(configFile);
        check_file();
    }

    public void check_file() {
        System.out.println(configFile.exists());
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            System.out.println(defaultConfigFile);
            System.out.println(configFile);

            try {
                FileUtils.copyURLToFile(defaultConfigFile, configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
