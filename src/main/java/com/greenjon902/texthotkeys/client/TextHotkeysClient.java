package com.greenjon902.texthotkeys.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class TextHotkeysClient implements ClientModInitializer {

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

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigBinding.wasPressed()) {
                client.player.sendMessage(new LiteralText("Opening config file..."), false);
                java.awt.Desktop.getDesktop().edit();
            }
        });
    }
}
