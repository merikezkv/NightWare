
package ru.nightware.client.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import ru.nightware.client.gui.ClickGuiScreen;

import java.nio.DoubleBuffer;

public class ArmorHudRenderer {

    private static final int PURPLE = 0xFF9B59FF;
    private static final int GREEN = 0xFF35E879;
    private static final int RED = 0xFFFF4545;
    private static final int WHITE = 0xFFFFFFFF;

    private static int armorX = 12;
    private static int armorY = 35;

    private static final int WATERMARK_X = 5;
    private static final int WATERMARK_Y = 5;

    private static final int ARMOR_WIDTH = 112;
    private static final int ARMOR_HEIGHT = 34;

    private static boolean mouseWasDown;
    private static boolean dragging;

    private static int dragOffsetX;
    private static int dragOffsetY;

    public static void register() {
        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player == null || client.options.hudHidden) {
                return;
            }

            if (ClickGuiScreen.isModuleEnabled(4, 0)) {
                updateDragging(client);
                renderArmor(context, client);
            } else {
                dragging = false;
                mouseWasDown = false;
            }

            if (ClickGuiScreen.isModuleEnabled(4, 1)) {
                renderWatermark(context, client);
            }
        });
    }

    private static void updateDragging(MinecraftClient client) {
        if (client.currentScreen != null) {
            dragging = false;
            mouseWasDown = false;
            return;
        }

        var window = client.getWindow();
        long handle = window.getHandle();

        boolean mouseDown = GLFW.glfwGetMouseButton(
                handle,
                GLFW.GLFW_MOUSE_BUTTON_1
        ) == GLFW.GLFW_PRESS;

        int mouseX;
        int mouseY;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer px = stack.mallocDouble(1);
            DoubleBuffer py = stack.mallocDouble(1);

            GLFW.glfwGetCursorPos(handle, px, py);

            mouseX = (int) (
                    px.get(0) * window.getScaledWidth()
                            / window.getWidth()
            );

            mouseY = (int) (
                    py.get(0) * window.getScaledHeight()
                            / window.getHeight()
            );
        }

        if (mouseDown && !mouseWasDown) {
            if (inside(mouseX, mouseY,
                    armorX, armorY,
                    ARMOR_WIDTH, ARMOR_HEIGHT)) {

                dragging = true;
                dragOffsetX = mouseX - armorX;
                dragOffsetY = mouseY - armorY;
            }
        }

        if (mouseDown && dragging) {
            armorX = Math.max(0, Math.min(
                    mouseX - dragOffsetX,
                    window.getScaledWidth() - ARMOR_WIDTH
            ));

            armorY = Math.max(0, Math.min(
                    mouseY - dragOffsetY,
                    window.getScaledHeight() - ARMOR_HEIGHT
            ));
        }

        if (!mouseDown) {
            dragging = false;
        }

        mouseWasDown = mouseDown;
    }

    private static boolean inside(
            int mx, int my,
            int x, int y,
            int w, int h
    ) {
        return mx >= x && mx <= x + w
                && my >= y && my <= y + h;
    }

    private static void renderArmor(
            DrawContext context,
            MinecraftClient client
    ) {
        var armor = client.player.getInventory().armor;

        int x = armorX;
        int y = armorY;

        context.fill(
                x, y,
                x + ARMOR_WIDTH,
                y + ARMOR_HEIGHT,
                0xCC151020
        );

        context.fill(x, y, x + ARMOR_WIDTH, y + 2, PURPLE);

        context.drawText(
                client.textRenderer,
                "ARMOR",
                x + 4, y + 4,
                PURPLE,
                false
        );

        int[] slots = {3, 2, 1, 0};

        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = armor.get(slots[i]);

            int itemX = x + 3 + i * 27;
            int itemY = y + 15;

            if (stack.isEmpty()) {
                context.fill(
                        itemX, itemY,
                        itemX + 16, itemY + 16,
                        0x55333333
                );
                continue;
            }

            boolean mending = hasMending(stack);
            int durability = getDurability(stack);

            if (mending) {
                context.fill(
                        itemX - 1, itemY - 1,
                        itemX + 17, itemY + 17,
                        0x6635E879
                );
            }

            context.drawItem(stack, itemX, itemY);

            int color = durability <= 20
                    ? RED
                    : mending ? GREEN : WHITE;

            context.drawText(
                    client.textRenderer,
                    String.valueOf(durability),
                    itemX - 1,
                    itemY + 16,
                    color,
                    false
            );
        }
    }

    private static void renderWatermark(
            DrawContext context,
            MinecraftClient client
    ) {
        int x = WATERMARK_X;
        int y = WATERMARK_Y;

        context.fill(
                x, y, x + 75, y + 13,
                0xCC151020
        );

        context.fill(
                x, y, x + 2, y + 13,
                PURPLE
        );

        context.drawText(
                client.textRenderer,
                "NightWare",
                x + 5, y + 3,
                PURPLE,
                false
        );
    }

    private static int getDurability(ItemStack stack) {
        if (!stack.isDamageable()) {
            return 100;
        }

        int max = stack.getMaxDamage();
        int remaining = max - stack.getDamage();

        return Math.max(0, remaining * 100 / max);
    }

    private static boolean hasMending(ItemStack stack) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world == null) {
            return false;
        }

        Registry<Enchantment> registry =
                client.world.getRegistryManager()
                        .getOrThrow(RegistryKeys.ENCHANTMENT);

        RegistryEntry<Enchantment> mending =
                registry.getEntry(
                        Enchantments.MENDING.getValue()
                ).orElse(null);

        return mending != null
                && EnchantmentHelper.getLevel(mending, stack) > 0;
    }
            }
