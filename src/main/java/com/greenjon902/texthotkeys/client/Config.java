package com.greenjon902.texthotkeys.client;

import java.util.HashMap;

public class Config {
    public static final boolean DEFAULT_NOTIFY_ON_SEND = true;

    public static boolean notifyOnSend;
    public static HashMap<Integer, String> hotkeys;


    public static int getHotkeyAmount() {
        return 5;
    }
}
