package org.xyccwa.robot_player.dataGen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.xyccwa.robot_player.RobotPlayer;
import org.xyccwa.robot_player.modItem.RobotPlayerItem;

public class RobotPlayerZhCnLangProvider extends LanguageProvider {
    public RobotPlayerZhCnLangProvider(PackOutput output) {
        super(output, RobotPlayer.MOD_ID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        // ========== 核心组件 ==========
        add(RobotPlayerItem.BASIC_RADAR.get(), "基础雷达");
        add(RobotPlayerItem.ADVANCED_RADAR.get(), "高级雷达");
        add(RobotPlayerItem.BASIC_CPU.get(), "基础主控芯片");
        add(RobotPlayerItem.ADVANCED_CPU.get(), "高级主控芯片");
        add(RobotPlayerItem.BASIC_THRUSTER.get(), "基础推进系统");
        add(RobotPlayerItem.ADVANCED_THRUSTER.get(), "高级推进系统");
        add(RobotPlayerItem.BASIC_FRAME.get(), "基础结构框架");
        add(RobotPlayerItem.ADVANCED_FRAME.get(), "高级结构框架");
        add(RobotPlayerItem.BASIC_BATTERY.get(), "基础主电池组");
        add(RobotPlayerItem.ADVANCED_BATTERY.get(), "高级主电池组");

// ========== 创意标签页 ==========
        add("itemGroup.robot_player.creative_tab", "机器人玩家");

// ========== 提示文本 ==========
        add("tooltip.robot_player.radar.desc", "§7安装在机器人核心区");
        add("tooltip.robot_player.radar.effect", "§a效果: 自动探测周围敌人");
        add("tooltip.robot_player.cpu.desc", "§7安装在机器人核心区");
        add("tooltip.robot_player.cpu.effect", "§a效果: 提升移动速度");
        add("tooltip.robot_player.thruster.desc", "§7安装在机器人核心区");
        add("tooltip.robot_player.thruster.effect", "§a效果: 提供二段跳能力");
        add("tooltip.robot_player.frame.desc", "§7安装在机器人核心区");
        add("tooltip.robot_player.frame.health", "§c+%s 最大生命值");
        add("tooltip.robot_player.frame.slots", "§a+%s 扩展槽位");
        add("tooltip.robot_player.battery.desc", "§7安装在机器人核心区");
        add("tooltip.robot_player.battery.energy", "§e+%s 最大电量");
        add("tooltip.robot_player.energy.cost", "§e耗电: %s");

    }

    private void addDeathMessage(String key, String value) {
        add("death.attack." + key, value);
    }
}