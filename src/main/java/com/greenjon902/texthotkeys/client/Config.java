package com.greenjon902.texthotkeys.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.net.URL;
import java.util.*;

public class Config {
    public static File configDir = FabricLoader.getInstance().getConfigDir().toFile();
    public static String configPrefix = "textHotkeys_";
    public static File bindingsFile = FabricLoader.getInstance().getConfigDir().resolve("textHotkeysBindings.txt").toFile();

    public static final boolean DEFAULT_NOTIFY_ON_SEND = true;

    public static boolean notifyOnSend = DEFAULT_NOTIFY_ON_SEND;
    public static ArrayList<HotkeyInfo> hotkeys = new ArrayList<>();

    public static class HotkeyInfo {
        private String text;
        private InputUtil.Key key;
        private KeyBinding keyBinding;

        private static int count = 0;

        public HotkeyInfo(String text, InputUtil.Key key) {
            this.text = text;
            this.key = key;

            this.keyBinding = new KeyBinding(String.valueOf(count), key.getCategory(), key.getCode(), "");
            count += 1;
        }

        public KeyBinding getKeyBinding() {
            return keyBinding;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public InputUtil.Key getKey() {
            return key;
        }

        public void setKey(InputUtil.Key key) {
            this.key = key;
            this.keyBinding.setBoundKey(key);
            System.out.println(key);
            System.out.println(key.getTranslationKey());
            KeyBinding.updateKeysByCode();
        }
    }

    public static void loadConfig() {
        notifyOnSend = Boolean.parseBoolean(loadConfigItem("notifyOnSend", DEFAULT_NOTIFY_ON_SEND));

        loadBindings();
    }

    public static void saveConfig() {
        saveConfigItem("notifyOnSend", notifyOnSend);

        saveBindings();
    }

    private static String loadConfigItem(String name, Object def) {
        try {
            System.out.println("[TextHotkeys] Loading " + name);
            File file = new File(configDir, configPrefix + name);

            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                return String.valueOf(fileInputStream.readAllBytes());
            } else {
                System.out.println("[TextHotkeys] File doesn't exist, creating a new version!");
                saveConfigItem(name, String.valueOf(def));
                return String.valueOf(def);
            }

        } catch (Exception e) {
            System.out.println("[TextHotkeys] Failed to load " + name);
            e.printStackTrace();
        }

        return String.valueOf(def);
    }

    private static void loadBindings() {
        try {
            System.out.println("[TextHotkeys] Loading bindings");
            hotkeys.clear();

            if (bindingsFile.exists()) {
                FileInputStream fileInputStream = new FileInputStream(bindingsFile);
                for (String line :new String(fileInputStream.readAllBytes()).split("\n")) {
                    String[] lineParts = line.split("\\|", 3);
                    System.out.println("test\n\n\n\n");
                    System.out.println(line);
                    System.out.println(Arrays.toString(lineParts));
                    InputUtil.Type category = InputUtil.Type.valueOf(lineParts[0]);
                    int keyCode = Integer.valueOf(lineParts[1]);
                    InputUtil.Key key = category.createFromCode(keyCode);

                    String text = lineParts[2];

                    Config.hotkeys.add(new Config.HotkeyInfo(text, key));
                }

            } else {
                System.out.println("[TextHotkeys] File doesn't exist, creating a new version!");
                bindingsFile.createNewFile();
            }

        } catch (Exception e) {
            System.out.println("[TextHotkeys] Failed to load bindings");
            e.printStackTrace();
        }
    }

    private static void saveConfigItem(String name, Object value) {
        saveConfigItem(name, String.valueOf(value));
    }

    private static void saveConfigItem(String name, String value) {
        try {
            System.out.println("[TextHotkeys] Saving " + name);
            File file = new File(configDir, configPrefix + name);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(value.getBytes());

        } catch (Exception e) {
            System.out.println("[TextHotkeys] Failed to save " + name);
            e.printStackTrace();
        }
    }

    private static void saveBindings() {
        try {
            System.out.println("[TextHotkeys] Saving bindings");
            FileOutputStream fileOutputStream = new FileOutputStream(bindingsFile);

            for (HotkeyInfo hotkeyInfo : hotkeys) {
                fileOutputStream.write((hotkeyInfo.key.getCategory() + "|" + hotkeyInfo.key.getCode() + "|" + hotkeyInfo.getText() + "\n").getBytes());
            }

        } catch (Exception e) {
            System.out.println("[TextHotkeys] Failed to save bindings");
            e.printStackTrace();
        }
    }
}
