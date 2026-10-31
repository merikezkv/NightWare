
package ru.nightware.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClickGuiScreen extends Screen {
    private static final int PURPLE = 0xFFAA66FF;
    private static final int PANEL = 0xFF171720;
    private static final int ROW = 0xFF292936;
    private static final int ACTIVE = 0xFF57358A;

    private final String[] modules = {
        "HUD",
        "Armor Highlight",
        "Coordinates",
        "FPS Counter"
    };

    private final boolean[] enabled = new boolean[modules.length];

    public ClickGuiScreen() {
        super(Text.literal("NightWare"));
    }

    @Override
    public void render(DrawContext context, int mouseX,
                       int mouseY, float delta) {

        // Затемнение игрового мира
        context.fill(0, 0, width, height, 0xCC000000);

        int x = width / 2 - 150;
        int y = height / 2 - 110;

        // Основная панель
        context.fill(x, y, x + 300, y + 220, PANEL);

        // Заголовок
        context.fill(x, y, x + 300, y + 35, 0xFF20202B);
        context.fill(x, y + 34, x + 300, y + 36, PURPLE);

        context.drawTextWithShadow(
            textRenderer,
            Text.literal("NightWare"),
            x + 12, y + 13, PURPLE
        );

        context.drawTextWithShadow(
            textRenderer,
            Text.literal("1.21.4"),
            x + 250, y + 13, 0xFFFFFFFF
        );

        // Список модулей
        for (int i = 0; i < modules.length; i++) {
            int rowY = y + 48 + i * 35;
            int color = enabled[i] ? ACTIVE : ROW;

            context.fill(
                x + 12, rowY,
                x + 288, rowY + 27,
                color
            );

            context.drawTextWithShadow(
                textRenderer,
                Text.literal(modules[i]),
                x + 22, rowY + 9,
                0xFFFFFFFF
            );

            context.drawTextWithShadow(
                textRenderer,
                Text.literal(enabled[i] ? "ON" : "OFF"),
                x + 250, rowY + 9,
                enabled[i] ? PURPLE : 0xFF999999
            );
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(
        double mouseX, double mouseY, int button
    ) {
        if (button == 0) {
            int x = width / 2 - 150;
            int y = height / 2 - 110;

            for (int i = 0; i < modules.length; i++) {
                int rowY = y + 48 + i * 35;

                if (mouseX >= x + 12 && mouseX <= x + 288
                    && mouseY >= rowY && mouseY <= rowY + 27) {

                    enabled[i] = !enabled[i];
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
