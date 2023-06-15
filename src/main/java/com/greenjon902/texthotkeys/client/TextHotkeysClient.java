package com.greenjon902.texthotkeys.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class TextHotkeysClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyBinding openConfigBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.textHotkeys.openConfig",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.textHotkeys"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert client.player != null;
            while (openConfigBinding.wasPressed()) {
                MinecraftClient.getInstance().setScreen(ConfigScreen.build(MinecraftClient.getInstance().currentScreen));
            }
        });



        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert client.player != null;

            for (Config.HotkeyInfo hotkeyInfo : Config.hotkeys) {

                while (hotkeyInfo.getKeyBinding().wasPressed()) {
                    String hotkeyValue = hotkeyInfo.getText();
                    System.out.println(hotkeyValue + " sent");

                    if (hotkeyValue.startsWith("/")) {
                        client.player.sendMessage(Text.translatable("chat.textHotkeys.command", hotkeyValue), false);
                        client.player.networkHandler.sendCommand(hotkeyValue.replaceFirst("/", ""));
                    } else {
                        client.player.sendMessage(Text.translatable("chat.textHotkeys.message", hotkeyValue), false);
                        client.player.networkHandler.sendChatMessage(hotkeyValue);
                    }
                }
            }
        });
    }
}
