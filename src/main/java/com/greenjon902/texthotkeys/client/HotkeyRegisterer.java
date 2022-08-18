package com.greenjon902.texthotkeys.client;

import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static com.greenjon902.texthotkeys.client.ConfigFile.configFile;

public class HotkeyRegisterer {


    /**
     * Loads the hotkey names and their message/command
     *
     * @return A map of hotkey names to their messages/commands
     */
    private static String[] loadHotkeyInformation() throws FileNotFoundException {
        System.out.println("Loading config file!");
        List<String> newHotkeyInformation = new ArrayList<>();

        ConfigFile.checkFile();

        Scanner scanner = new Scanner(configFile);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.startsWith("#")) {
                newHotkeyInformation.add(line);
                System.out.println("Found command \"" + line + "\"");
            }
        }

        return newHotkeyInformation.toArray(String[]::new);
    }

    /**
     * Load the hotkey information and register it
     */
    public static KeyBinding[] register() throws FileNotFoundException {
        System.out.println("Attempting loading");
        String[] newHotkeyInformation = loadHotkeyInformation();

        KeyBinding[] keyBindings = new KeyBinding[newHotkeyInformation.length];
        for (int i=0; i<newHotkeyInformation.length; i++) {
            keyBindings[i] = new KeyBinding(newHotkeyInformation[i], GLFW.GLFW_KEY_5, // TODO: Load correct key
                    "category.textHotkeys.commands");
        }

        return keyBindings;
    }
}
