package com.greenjon902.texthotkeys.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

public class ConfigScreen {
    public static Screen build(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.textHotkeys.configScreen"))
                .setSavingRunnable(() -> {});
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.textHotkeys.config.general"));
        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.textHotkeys.notifyOnSend"), Config.notifyOnSend)
                .setDefaultValue(Config.DEFAULT_NOTIFY_ON_SEND)
                .setSaveConsumer(newValue -> Config.notifyOnSend = newValue)
                .build());

        ConfigCategory hotkeys = builder.getOrCreateCategory(Text.translatable("category.textHotkeys.config.hotkeys"));
        for (int keyCode : Config.hotkeys.keySet()) {
            hotkeys.addEntry(entryBuilder.);
        }

        return builder.build();
    }
}
