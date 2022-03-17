package com.greenjon902.texthotkeys.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;
import org.apache.commons.io.FileUtils;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

@Environment(EnvType.CLIENT)
public class TextHotkeysClient implements ClientModInitializer {

    static File configFile = FabricLoader.getInstance().getConfigDir().resolve("textHotkeys.txt").toFile();
    static URL defaultConfigFile = TextHotkeysClient.class.getResource("/textHotkeys.txt");

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
                assert client.player != null;

                client.player.sendMessage(new TranslatableText("chat.config.reload"), false);
                check_file();
                reload();


                try {
                    KeyBindingRegistryImpl.class.getDeclaredField("moddedKeyBindings").setAccessible(true);

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void reload() {
        System.out.println("Reloading config file!");
        check_file();
        try {
            Scanner scanner = new Scanner(configFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);
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
