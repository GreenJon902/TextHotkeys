package com.greenjon902.texthotkeys.client;

import com.google.common.collect.ImmutableList;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        for (Config.HotkeyInfo hotkeyInfo : Config.hotkeys) {
            hotkeys.addEntry(new HotKeyEntry(hotkeyInfo));
        }

        return builder.build();
    }

}
