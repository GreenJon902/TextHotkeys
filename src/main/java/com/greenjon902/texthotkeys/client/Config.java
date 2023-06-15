package com.greenjon902.texthotkeys.client;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

public class Config {
    public static final boolean DEFAULT_NOTIFY_ON_SEND = true;

    public static boolean notifyOnSend = DEFAULT_NOTIFY_ON_SEND;
    public static HotkeyInfo[] hotkeys = {new HotkeyInfo("/me bad", InputUtil.GLFW_KEY_P)};

    public static class HotkeyInfo {
        private String text;
        private int keyCode;
        private KeyBinding keyBinding;

        public HotkeyInfo(String text, int keyCode) {
            this.text = text;
            this.keyCode = keyCode;

            this.keyBinding = new KeyBinding(text, keyCode, "");
        }

        public KeyBinding getKeyBinding() {
            return keyBinding;
        }

        public String getText() {
            return text;
        }
    }
}
