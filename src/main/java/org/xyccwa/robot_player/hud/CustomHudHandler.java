package org.xyccwa.robot_player.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.xyccwa.robot_player.RobotPlayer;
import org.xyccwa.robot_player.attachment.RobotEnergyData;
import org.xyccwa.robot_player.attachment.RobotPlayerAttachments;

@EventBusSubscriber(modid = RobotPlayer.MOD_ID, value = Dist.CLIENT)
public class CustomHudHandler {

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiLayerEvent.Pre event) {
        var name = event.getName();

        if (VanillaGuiLayers.PLAYER_HEALTH.equals(name) ||
                VanillaGuiLayers.FOOD_LEVEL.equals(name) ||
                VanillaGuiLayers.EXPERIENCE_BAR.equals(name) ||
                VanillaGuiLayers.ARMOR_LEVEL.equals(name)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiLayerEvent.Post event) {
        renderCustomBars(event.getGuiGraphics());
    }

    private static void renderCustomBars(net.minecraft.client.gui.GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) return;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int rightMargin = 10;
        int topMargin = 10;
        int barWidth = 120;
        int barHeight = 10;
        int textPadding = 60;

        // ===== 生命值 =====
        int healthBarX = screenWidth - barWidth - rightMargin;
        int healthTextX = healthBarX - textPadding;
        int healthY = topMargin;

        // ===== 电量 =====
        int energyBarX = screenWidth - barWidth - rightMargin;
        int energyTextX = energyBarX - textPadding;
        int energyY = healthY + barHeight + 5;

        // ---------- 1. 生命值条 ----------
        float healthPercent = player.getHealth() / player.getMaxHealth();
        int healthFill = (int)(barWidth * healthPercent);
        drawBar(guiGraphics, healthBarX, healthY, barWidth, barHeight, healthFill, 0xFF001133, 0xFF3399FF);

        String healthText = String.format("%.1f%%", player.getHealth()/player.getMaxHealth()*100);
        drawText(guiGraphics, mc, healthText, healthTextX, textPadding, healthY, barHeight, 0xFF3399FF);

        // ---------- 2. 电量条 ----------
        RobotEnergyData energyData = player.getData(RobotPlayerAttachments.ROBOT_ENERGY);

        int energyCurrent = energyData.getEnergy();
        int energyMax = energyData.getMaxEnergy();

        // 防止除以0
        if (energyMax <= 0) energyMax = 50;

        float energyPercent = (float) energyCurrent / energyMax;
        int energyFill = (int)(barWidth * Math.min(energyPercent, 1.0f));

        drawBar(guiGraphics, energyBarX, energyY, barWidth, barHeight, energyFill, 0xFF331900, 0xFFFFCC00);

        String energyText = String.format("%.1f%%",(float) energyCurrent / energyMax * 100);
        drawText(guiGraphics, mc, energyText, energyTextX, textPadding, energyY, barHeight, 0xFFFFCC00);
    }

    private static void drawBar(net.minecraft.client.gui.GuiGraphics guiGraphics,
                                int x, int y, int width, int height,
                                int fillWidth, int bgColor, int fgColor) {
        guiGraphics.fill(x, y, x + width, y + height, bgColor);
        guiGraphics.fill(x, y, x + Math.min(fillWidth, width), y + height, fgColor);
        guiGraphics.fill(x, y, x + width, y + 1, 0xFFFFFFFF);
        guiGraphics.fill(x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
        guiGraphics.fill(x, y, x + 1, y + height, 0xFFFFFFFF);
        guiGraphics.fill(x + width - 1, y, x + width, y + height, 0xFFFFFFFF);
    }

    private static void drawText(net.minecraft.client.gui.GuiGraphics guiGraphics,
                                 Minecraft mc, String text,
                                 int textX, int textPadding,
                                 int barY, int barHeight, int color) {
        int textY = barY + (barHeight - mc.font.lineHeight) / 2;
        int textWidth = mc.font.width(text);
        guiGraphics.drawString(mc.font, text,
                textX + (textPadding - textWidth),
                textY, color, true);
    }
}