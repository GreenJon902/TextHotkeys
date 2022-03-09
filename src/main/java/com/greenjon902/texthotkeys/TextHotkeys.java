package com.greenjon902.texthotkeys;

import com.greenjon902.texthotkeys.client.configScreen.ConfigGui;
import com.greenjon902.texthotkeys.client.configScreen.ConfigScreen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class TextHotkeys implements ModInitializer {
    @Override
    public void onInitialize() {
        KeyBinding configMenuKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.texthotkeys.open.config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.examplemod.test"
        ));
    }
}
