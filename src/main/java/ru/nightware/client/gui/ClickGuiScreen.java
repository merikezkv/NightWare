package ru.nightware.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClickGuiScreen extends Screen {

    private static final int PURPLE = 0xFF9B59FF;
    private static final int PANEL = 0xF0191924;
    private static final int ROW = 0xFF252532;

    public ClickGuiScreen() {
        super(Text.literal("NightWare"));
    }

    @Override
    public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta
    ) {

        // Затемнение мира
        context.fill(
            0, 0, width, height,
            0x99000000
        );

        int x = width / 2 - 175;
        int y = height / 2 - 112;

        // Основная панель
        context.fill(
            x, y,
            x + 350, y + 224,
            PANEL
        );

        // Верхняя панель
        context.fill(
            x, y,
            x + 350, y + 36,
            0xFF171720
        );

        // Фиолетовая полоса
        context.fill(
            x, y + 35,
            x + 350, y + 37,
            PURPLE
        );

        // Название клиента
        context.drawTextWithShadow(
            textRenderer,
            Text.literal("NightWare"),
            x + 12,
            y + 13,
            PURPLE
        );

        // Версия
        context.drawTextWithShadow(
            textRenderer,
            Text.literal("1.21.4"),
            x + 300,
            y + 13,
            0xFFBBBBBB
        );

        // Категории
        String[] categories = {
            "Combat",
            "Movement",
            "Visuals",
            "Misc"
        };

        int rowY = y + 52;

        for (String category : categories) {

            context.fill(
                x + 12,
                rowY - 5,
                x + 338,
                rowY + 20,
                ROW
            );

            context.drawTextWithShadow(
                textRenderer,
                Text.literal(category),
                x + 22,
                rowY + 3,
                0xFFFFFFFF
            );

            rowY += 34;
        }

        // Нижняя надпись
        context.drawTextWithShadow(
            textRenderer,
            Text.literal("NightWare Client | Fabric"),
            x + 12,
            y + 204,
            0xFF999999
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
