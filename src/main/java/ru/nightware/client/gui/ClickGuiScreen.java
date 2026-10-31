
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
        "Combat", "Movement", "Render",
        "Player", "Util", "Themes"
    };

    private static final String[][] MODULES = {
        {"Combat Visual"},
        {"Movement Visual"},
        {"Armor Highlight", "Coordinates", "FPS Counter"},
        {"Player Visual"},
        {"HUD"},
        {}
    };

    // Сохраняются при закрытии ClickGUI
    private static final boolean[][] enabled = new boolean[6][3];

    // Анимация переключателей
    private static final float[][] toggleAnimation = new float[6][3];

    private int selectedColor = 0;
    private int selectedSection = 0;

    private long openTime;
    private long sectionChangeTime;

    private int previousSection = 0;

    private static final long OPEN_DURATION = 350;
    private static final long SECTION_DURATION = 220;

    public ClickGuiScreen() {
        super(Text.literal("NightWare"));
        openTime = System.currentTimeMillis();
        sectionChangeTime = openTime;
    }

    private float clamp(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    private float easeOut(float value) {
        value = clamp(value);
        return 1.0f - (1.0f - value) * (1.0f - value);
    }

    private float openProgress() {
        return easeOut(
            (System.currentTimeMillis() - openTime)
                / (float) OPEN_DURATION
        );
    }

    private float sectionProgress() {
        return easeOut(
            (System.currentTimeMillis() - sectionChangeTime)
                / (float) SECTION_DURATION
        );
    }

    private int blend(int a, int b, float t) {
        t = clamp(t);

        int r = (int) (((a >> 16) & 255) * (1 - t)
                + ((b >> 16) & 255) * t);

        int g = (int) (((a >> 8) & 255) * (1 - t)
                + ((b >> 8) & 255) * t);

        int bl = (int) ((a & 255) * (1 - t)
                + (b & 255) * t);

        return 0xFF000000 | (r << 16) | (g << 8) | bl;
    }

    private int animatedColor(long offset) {
        long time = System.currentTimeMillis() / 8 + offset;

        int index = (int) ((time / 45 + selectedColor)
                % COLORS.length);

        int next = (index + 1) % COLORS.length;

        float progress = (time % 45) / 45.0f;

        return blend(COLORS[index], COLORS[next], progress);
    }

    @Override
    public void renderBackground(
        DrawContext context,
        int mouseX,
        int mouseY,
        float delta
    ) {
        // Размытие полностью отключено
    }

    @Override
    public void render(
        DrawContext context,
        int mouseX,
        int mouseY,
        float delta
    ) {
        float opening = openProgress();

        // Непрозрачный фон
        context.fill(0, 0, width, height, BG);

        int baseX = width / 2 - 190;
        int baseY = height / 2 - 112;

        // Анимация появления: плавное смещение
        int x = baseX;
        int y = baseY + (int) ((1.0f - opening) * 18);

        int accent = COLORS[selectedColor];

        // Основное окно
        context.fill(x, y, x + 380, y + 225, PANEL);

        // Заголовок
        context.fill(x, y, x + 380, y + 36, 0xFF20202A);
        context.fill(
            x, y + 35, x + 380, y + 37,
            animatedColor(0)
        );

        context.drawTextWithShadow(
            textRenderer,
            Text.literal("NightWare"),
            x + 14, y + 13,
            animatedColor(0)
        );

        context.drawTextWithShadow(
            textRenderer,
            Text.literal("1.21.4"),
            x + 335, y + 13,
            0xFFFFFFFF
        );

        // Боковая панель
        context.fill(
            x, y + 37,
            x + 115, y + 225,
            SIDEBAR
        );

        context.drawTextWithShadow(
            textRenderer,
            Text.literal("SECTIONS"),
            x + 11, y + 48,
            0xFF888888
        );

        // Разделы
        for (int i = 0; i < SECTIONS.length; i++) {
            int rowY = y + 65 + i * 25;

            if (selectedSection == i) {
                context.fill(
                    x + 6, rowY - 3,
                    x + 109, rowY + 18,
                    ROW
                );

                context.fill(
                    x + 6, rowY - 3,
                    x + 9, rowY + 18,
                    animatedColor(i * 200L)
                );
            }

            context.drawTextWithShadow(
                textRenderer,
                Text.literal(SECTIONS[i]),
                x + 16, rowY + 3,
                selectedSection == i
                    ? animatedColor(i * 200L)
                    : 0xFFBBBBBB
            );
        }

        int contentX = x + 132;

        // Заголовок выбранной секции
        context.drawTextWithShadow(
            textRenderer,
            Text.literal(SECTIONS[selectedSection].toUpperCase()),
            contentX, y + 51,
            animatedColor(0)
        );

        float transition = sectionProgress();

        // Плавный переход содержимого
        int slide = (int) ((1.0f - transition) * 18);

        if (selectedSection == 5) {
            renderTheme(context, contentX, y, slide);
        } else {
            renderModules(context, contentX, y, slide, delta);
        }

        context.drawTextWithShadow(
            textRenderer,
            Text.literal("NightWare Client"),
            x + 11, y + 210,
            0xFF777777
        );

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderTheme(
        DrawContext context,
        int contentX,
        int y,
        int slide
    ) {
        context.fill(
            contentX - 5 + slide, y + 69,
            contentX + 237 + slide, y + 195,
            ROW
        );

        context.drawTextWithShadow(
            textRenderer,
            Text.literal("Accent Color"),
            contentX + 10 + slide,
            y + 82,
            0xFFFFFFFF
        );

        context.drawTextWithShadow(
            textRenderer,
            Text.literal("Choose interface colors"),
            contentX + 10 + slide,
            y + 99,
            0xFF999999
        );

        for (int i = 0; i < COLORS.length; i++) {
            int colorX = contentX + 12 + slide + (i % 4) * 43;
            int colorY = y + 125 + (i / 4) * 35;

            if (selectedColor == i) {
                context.fill(
                    colorX - 3, colorY - 3,
                    colorX + 25, colorY + 25,
                    0xFFFFFFFF
                );
            }

            context.fill(
                colorX, colorY,
                colorX + 19, colorY + 19,
                COLORS[i]
            );
        }

        context.drawTextWithShadow(
            textRenderer,
            Text.literal("Selected: " + COLOR_NAMES[selectedColor]),
            contentX + 10 + slide,
            y + 181,
            animatedColor(0)
        );
    }

    private void renderModules(
        DrawContext context,
        int contentX,
        int y,
        int slide,
        float delta
    ) {
        String[] modules = MODULES[selectedSection];

        if (modules.length == 0) {
            context.drawTextWithShadow(
                textRenderer,
                Text.literal("No modules yet"),
                contentX + 12 + slide,
                y + 90,
                0xFF999999
            );
            return;
        }

        for (int i = 0; i < modules.length; i++) {
            int rowY = y + 69 + i * 31;

            boolean active = enabled[selectedSection][i];

            // Плавная анимация ON/OFF
            float target = active ? 1.0f : 0.0f;

            toggleAnimation[selectedSection][i] +=
                (target - toggleAnimation[selectedSection][i])
                    * Math.min(1.0f, delta * 12.0f);

            float animation = toggleAnimation[selectedSection][i];

            int color = active
                ? animatedColor(i * 650L)
                : ROW;

            int background = blend(ROW, color, animation);

            context.fill(
                contentX - 5 + slide, rowY,
                contentX + 237 + slide, rowY + 26,
                background
            );

            if (animation > 0.01f) {
                context.fill(
                    contentX - 5 + slide, rowY,
                    contentX - 2 + slide, rowY + 26,
                    animatedColor(i * 300L)
                );
            }

            context.drawTextWithShadow(
                textRenderer,
                Text.literal(modules[i]),
                contentX + 8 + slide,
                rowY + 9,
                0xFFFFFFFF
            );

            context.drawTextWithShadow(
                textRenderer,
                Text.literal(active ? "ON" : "OFF"),
                contentX + 204 + slide,
                rowY + 9,
                blend(0xFF888888, animatedColor(i * 400L),
                    animation)
            );
        }
    }

    @Override
    public boolean mouseClicked(
        double mouseX,
        double mouseY,
        int button
    ) {
        if (button == 0) {
            int x = width / 2 - 190;
            int y = height / 2 - 112;

            // Выбор секции
            for (int i = 0; i < SECTIONS.length; i++) {
                int rowY = y + 65 + i * 25;

                if (mouseX >= x + 6
                    && mouseX <= x + 109
                    && mouseY >= rowY - 3
                    && mouseY <= rowY + 18) {

                    if (selectedSection != i) {
                        previousSection = selectedSection;
                        selectedSection = i;
                        sectionChangeTime =
                            System.currentTimeMillis();
                    }

                    return true;
                }
            }

            int contentX = x + 132;

            // Выбор цвета в Themes
            if (selectedSection == 5) {
                for (int i = 0; i < COLORS.length; i++) {
                    int colorX = contentX + 12 + (i % 4) * 43;
                    int colorY = y + 125 + (i / 4) * 35;

                    if (mouseX >= colorX - 3
                        && mouseX <= colorX + 25
                        && mouseY >= colorY - 3
                        && mouseY <= colorY + 25) {

                        selectedColor = i;
                        return true;
                    }
                }
            } else {
                // Переключение функций
                String[] modules = MODULES[selectedSection];

                for (int i = 0; i < modules.length; i++) {
                    int rowY = y + 69 + i * 31;

                    if (mouseX >= contentX - 5
                        && mouseX <= contentX + 237
                        && mouseY >= rowY
                        && mouseY <= rowY + 26) {

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
