package org.xyccwa.robot_player.dataGen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.xyccwa.robot_player.RobotPlayer;
import org.xyccwa.robot_player.modItem.RobotPlayerItem;

public class RobotPlayerEnUsLangProvider extends LanguageProvider {
    public RobotPlayerEnUsLangProvider(PackOutput output) {
        super(output, RobotPlayer.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // ========== Core Components ==========
        add(RobotPlayerItem.BASIC_RADAR.get(), "Basic Radar");
        add(RobotPlayerItem.ADVANCED_RADAR.get(), "Advanced Radar");
        add(RobotPlayerItem.BASIC_CPU.get(), "Basic CPU");
        add(RobotPlayerItem.ADVANCED_CPU.get(), "Advanced CPU");
        add(RobotPlayerItem.BASIC_THRUSTER.get(), "Basic Thruster");
        add(RobotPlayerItem.ADVANCED_THRUSTER.get(), "Advanced Thruster");
        add(RobotPlayerItem.BASIC_FRAME.get(), "Basic Frame");
        add(RobotPlayerItem.ADVANCED_FRAME.get(), "Advanced Frame");
        add(RobotPlayerItem.BASIC_BATTERY.get(), "Basic Battery");
        add(RobotPlayerItem.ADVANCED_BATTERY.get(), "Advanced Battery");

// ========== Creative Tab ==========
        add("itemGroup.robot_player.creative_tab", "Robot Player");

// ========== Tooltips ==========
        add("tooltip.robot_player.radar.desc", "§7Install in robot core slot");
        add("tooltip.robot_player.radar.effect", "§aEffect: Detect nearby enemies");
        add("tooltip.robot_player.cpu.desc", "§7Install in robot core slot");
        add("tooltip.robot_player.cpu.effect", "§aEffect: Increase movement speed");
        add("tooltip.robot_player.thruster.desc", "§7Install in robot core slot");
        add("tooltip.robot_player.thruster.effect", "§aEffect: Double jump ability");
        add("tooltip.robot_player.frame.desc", "§7Install in robot core slot");
        add("tooltip.robot_player.frame.health", "§c+%s Max Health");
        add("tooltip.robot_player.frame.slots", "§a+%s Expansion Slots");
        add("tooltip.robot_player.battery.desc", "§7Install in robot core slot");
        add("tooltip.robot_player.battery.energy", "§e+%s Max Energy");
        add("tooltip.robot_player.energy.cost", "§eEnergy Cost: %s");
    }
}
