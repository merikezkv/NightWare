package ru.nightware.client;

import ru.nightware.client.hud.ArmorHudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.lwjgl.glfw.GLFW;

import ru.nightware.client.gui.ClickGuiScreen;

public class NightWare implements ClientModInitializer {

    public static final String NAME = "NightWare";

    private static KeyBinding openGui;

    @Override
    public void onInitializeClient() {
        ArmorHudRenderer.register();

        openGui = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.nightware.clickgui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.nightware"
            )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while (openGui.wasPressed()) {
                client.setScreen(new ClickGuiScreen());
            }

        });
    }
}
