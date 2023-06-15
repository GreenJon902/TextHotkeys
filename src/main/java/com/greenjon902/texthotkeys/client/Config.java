package com.greenjon902.texthotkeys.client;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Config {
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
}
