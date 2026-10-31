
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

public class ArmorHudRenderer {

    private static final int PURPLE = 0xFF9B59FF;
    private static final int GREEN = 0xFF35E879;
    private static final int RED = 0xFFFF4545;
    private static final int WHITE = 0xFFFFFFFF;

    public static void register() {
        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player == null || client.options.hudHidden) {
                return;
            }

            render(context, client);
        });
    }

    private static void render(DrawContext context, MinecraftClient client) {
        var armor = client.player.getInventory().armor;

        int x = 12;
        int y = 12;
        int width = 150;
        int height = 38;

        context.fill(x, y, x + width, y + height, 0xCC151020);
        context.fill(x, y, x + width, y + 2, PURPLE);

        context.drawText(
                client.textRenderer,
                "NIGHTWARE ARMOR",
                x + 7, y + 5,
                PURPLE,
                false
        );

        int[] slots = {3, 2, 1, 0};

        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = armor.get(slots[i]);

            int itemX = x + 7 + i * 35;
            int itemY = y + 17;

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
                        itemX - 2, itemY - 2,
                        itemX + 18, itemY + 18,
                        0x6635E879
                );
            }

            context.drawItem(stack, itemX, itemY);

            int color = durability <= 20
                    ? RED
                    : mending ? GREEN : WHITE;

            context.drawText(
                    client.textRenderer,
                    durability + "%",
                    itemX - 1,
                    itemY + 18,
                    color,
                    false
            );
        }
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
                registry.getEntry(Enchantments.MENDING).orElse(null);

        return mending != null
                && EnchantmentHelper.getLevel(mending, stack) > 0;
    }
}
