package com.greenjon902.texthotkeys.client;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;

public class ConfigScreenKeyBindingEntry extends AbstractConfigListEntry {
    private final int keyCode;
    private final String command;


    public ConfigScreenKeyBindingEntry(int keyCode, String command) {
        this.keyCode = keyCode;
        this.command = command;
    }
}
