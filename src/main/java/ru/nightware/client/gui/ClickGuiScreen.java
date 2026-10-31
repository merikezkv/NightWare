
package ru.nightware.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClickGuiScreen extends Screen {

    private static final int BG = 0xFF09090D;
    private static final int PANEL = 0xFF171720;
    private static final int SIDEBAR = 0xFF111118;
    private static final int ROW = 0xFF252530;

    private static final int[] COLORS = {
        0xFFAA66FF,
        0xFF5599FF,
        0xFF45D483,
        0xFFFF5555,
        0xFFFFAA44,
        0xFFFF66BB,
        0xFFFFFFFF
    };

    private static final String[] COLOR_NAMES = {
        "Purple", "Blue", "Green", "Red",
        "Orange", "Pink", "White"
    };

    private static final String[] SECTIONS = {
        "Combat", "Movement",
        "Render", "Player", "Util", "Themes"
    };

    private static final String[][] MODULES = {
        {},
        {"Combat Visual"},
        {"Movement Visual"},
        {"Armor Highlight", "Coordinates", "FPS Counter"},
        {"Player Visual"},
        {"HUD"}
    };

    private static final boolean[][] enabled = new boolean[6][3];

    private int selectedColor = 0;
    private int selectedSection = 0;

    public ClickGuiScreen() {
        super(Text.literal("NightWare"));
    }

    private int animatedColor(long offset) {
        long time = System.currentTimeMillis() / 12 + offset;
        int index = (int) ((time / 35) % COLORS.length);
        int next = (index + 1) % COLORS.length;

        float progress = (time % 35) / 35.0f;

        return blend(COLORS[index], COLORS[next], progress);
    }

    private int blend(int a, int b, float t) {
        int r = (int) (((a >> 16) & 255) * (1 - t)
                + ((b >> 16) & 255) * t);
        int g = (int) (((a >> 8) & 255) * (1 - t)
                + ((b >> 8) & 255) * t);
        int bl = (int) ((a & 255) * (1 - t)
                + (b & 255) * t);

        return 0xFF000000 | (r << 16) | (g << 8) | bl;
    }

    @Override
    public void renderBackground(
        DrawContext context, int mouseX,
        int mouseY, float delta
    ) {
        // Размытие отключено
    }

    @Override
    public void render(
        DrawContext context, int mouseX,
        int mouseY, float delta
    ) {
        context.fill(0, 0, width, height, BG);

        int x = width / 2 - 210;
        int y = height / 2 - 125;
        int accent = COLORS[selectedColor];

        // Главное окно
        context.fill(x, y, x + 420, y + 250, PANEL);

        // Заголовок
        context.fill(x, y, x + 420, y + 38, 0xFF20202A);
        context.fill(x, y + 37, x + 420, y + 39, accent);

        context.drawTextWithShadow(
            textRenderer, Text.literal("NightWare"),
            x + 14, y + 14, accent
        );

        context.drawTextWithShadow(
            textRenderer, Text.literal("1.21.4"),
            x + 375, y + 14, 0xFFFFFFFF
        );

        // Боковая панель
        context.fill(x, y + 39, x + 125, y + 250, SIDEBAR);

        context.drawTextWithShadow(
            textRenderer, Text.literal("SECTIONS"),
            x + 12, y + 51, 0xFF888888
        );

        for (int i = 0; i < SECTIONS.length; i++) {
            int rowY = y + 70 + i * 28;

            if (selectedSection == i) {
                context.fill(
                    x + 7, rowY - 4,
                    x + 118, rowY + 19, ROW
                );

                context.fill(
                    x + 7, rowY - 4,
                    x + 10, rowY + 19, accent
                );
            }

            context.drawTextWithShadow(
                textRenderer, Text.literal(SECTIONS[i]),
                x + 18, rowY + 3,
                selectedSection == i ? accent : 0xFFBBBBBB
            );
        }

        int contentX = x + 145;

        context.drawTextWithShadow(
            textRenderer,
            Text.literal(SECTIONS[selectedSection].toUpperCase()),
            contentX, y + 55, accent
        );

        if (selectedSection == 0) {
            renderTheme(context, contentX, y);
        } else {
            renderModules(context, contentX, y);
        }

        context.drawTextWithShadow(
            textRenderer, Text.literal("NightWare Client"),
            x + 12, y + 233, 0xFF777777
        );

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderTheme(
        DrawContext context, int contentX, int y
    ) {
        context.fill(
            contentX - 5, y + 76,
            contentX + 260, y + 220, ROW
        );

        context.drawTextWithShadow(
            textRenderer, Text.literal("Accent Color"),
            contentX + 10, y + 90, 0xFFFFFFFF
        );

        context.drawTextWithShadow(
            textRenderer, Text.literal("Choose interface colors"),
            contentX + 10, y + 108, 0xFF999999
        );

        for (int i = 0; i < COLORS.length; i++) {
            int colorX = contentX + 12 + (i % 4) * 48;
            int colorY = y + 140 + (i / 4) * 43;

            if (selectedColor == i) {
                context.fill(
                    colorX - 3, colorY - 3,
                    colorX + 27, colorY + 27,
                    0xFFFFFFFF
                );
            }

            context.fill(
                colorX, colorY,
                colorX + 24, colorY + 24,
                COLORS[i]
            );
        }

        context.drawTextWithShadow(
            textRenderer,
            Text.literal("Selected: " + COLOR_NAMES[selectedColor]),
            contentX + 10, y + 203, COLORS[selectedColor]
        );
    }

    private void renderModules(
        DrawContext context, int contentX, int y
    ) {
        String[] modules = MODULES[selectedSection];

        if (modules.length == 0) {
            context.drawTextWithShadow(
                textRenderer, Text.literal("No modules yet"),
                contentX + 12, y + 95, 0xFF999999
            );
            return;
        }

        for (int i = 0; i < modules.length; i++) {
            int rowY = y + 76 + i * 35;
            boolean active = enabled[selectedSection][i];

            int color = active
                ? animatedColor(i * 500L)
                : ROW;

            context.fill(
                contentX - 5, rowY,
                contentX + 260, rowY + 29,
                color
            );

            if (active) {
                context.fill(
                    contentX - 5, rowY,
                    contentX - 2, rowY + 29,
                    animatedColor(i * 500L + 200)
                );
            }

            context.drawTextWithShadow(
                textRenderer, Text.literal(modules[i]),
                contentX + 9, rowY + 10,
                active ? 0xFFFFFFFF : 0xFFBBBBBB
            );

            context.drawTextWithShadow(
                textRenderer,
                Text.literal(active ? "ON" : "OFF"),
                contentX + 225, rowY + 10,
                active ? 0xFFFFFFFF : 0xFF888888
            );
        }
    }

    @Override
    public boolean mouseClicked(
        double mouseX, double mouseY, int button
    ) {
        if (button == 0) {
            int x = width / 2 - 210;
            int y = height / 2 - 125;

            // Выбор раздела
            for (int i = 0; i < SECTIONS.length; i++) {
                int rowY = y + 70 + i * 28;

                if (mouseX >= x + 7 && mouseX <= x + 118
                    && mouseY >= rowY - 4
                    && mouseY <= rowY + 19) {
                    selectedSection = i;
                    return true;
                }
            }

            int contentX = x + 145;

            // Выбор цвета
            if (selectedSection == 0) {
                for (int i = 0; i < COLORS.length; i++) {
                    int colorX = contentX + 12 + (i % 4) * 48;
                    int colorY = y + 140 + (i / 4) * 43;

                    if (mouseX >= colorX - 3
                        && mouseX <= colorX + 27
                        && mouseY >= colorY - 3
                        && mouseY <= colorY + 27) {
                        selectedColor = i;
                        return true;
                    }
                }
            } else {
                // Включение и выключение модулей
                String[] modules = MODULES[selectedSection];

                for (int i = 0; i < modules.length; i++) {
                    int rowY = y + 76 + i * 35;

                    if (mouseX >= contentX - 5
                        && mouseX <= contentX + 260
                        && mouseY >= rowY
                        && mouseY <= rowY + 29) {
                        enabled[selectedSection][i] =
                            !enabled[selectedSection][i];
                        return true;
                    }
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
