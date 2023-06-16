package com.greenjon902.texthotkeys.client;

import com.greenjon902.texthotkeys.Unregisterable;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.List;

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
        int i = 0;
        for (Config.HotkeyInfo hotkeyInfo : Config.hotkeys) {
            i++;
            hotkeys.addEntry(
                    entryBuilder.startSubCategory(Text.translatable("option.textHotkeys.hotkey.title", i, hotkeyInfo.getText()),
                                    List.of(entryBuilder.startStrField(Text.translatable("option.textHotkeys.hotkey.text", i), hotkeyInfo.getText())
                                                    .setTooltip(Text.translatable("option.textHotkeys.hotkey.text.tooltip"))
                                                    .setDefaultValue(hotkeyInfo.getText())
                                                    .setSaveConsumer(hotkeyInfo::setText)
                                                    .build(),
                                            entryBuilder.startKeyCodeField(Text.translatable("option.textHotkeys.hotkey.binding", i), hotkeyInfo.getKey())
                                                    .setTooltip(Text.translatable("option.textHotkeys.hotkey.binding.tooltip"))
                                                    .setDefaultValue(hotkeyInfo.getKey())
                                                    .setKeySaveConsumer(hotkeyInfo::setKey)
                                                    .build(),
                                            entryBuilder.startBooleanToggle(Text.translatable("option.textHotkeys.hotkey.remove"), false)
                                                    .setTooltip(Text.translatable("option.textHotkeys.hotkey.remove.tooltip"))
                                                    .setDefaultValue(false)
                                                    .setSaveConsumer(newValue -> checkRemove(newValue, hotkeyInfo))
                                                    .build()))
                            .build()
            );
        }

        hotkeys.addEntry(
                entryBuilder.startIntField(Text.translatable("option.textHotkeys.addHotkeys"), 0)
                        .setTooltip(Text.translatable("option.textHotkeys.addHotkeys.tooltip"))
                        .setDefaultValue(0)
                        .setSaveConsumer(ConfigScreen::addNew)
                        .build()
        );
        builder.setSavingRunnable(Config::saveConfig);
        return builder.build();
    }

    private static void checkRemove(Boolean remove, Config.HotkeyInfo hotkeyInfo) {
        if (remove) {
            Object keybinding = hotkeyInfo.getKeyBinding();
            ((Unregisterable)(keybinding)).unregisterSelf();


            Config.hotkeys.remove(hotkeyInfo);
            KeyBinding.updateKeysByCode();
        }
    }

    private static void addNew(Integer amount) {
        for (int i=0; i<amount; i++) {
            Config.hotkeys.add(new Config.HotkeyInfo("", InputUtil.Type.KEYSYM.createFromCode(InputUtil.GLFW_KEY_P)));
        }
        KeyBinding.updateKeysByCode();
    }
}
