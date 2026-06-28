package org.xyccwa.robot_player.modItem.coreItem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 主控芯片组件 - 提升机器人性能，消耗电量
 */
public class CpuComponentItem extends CoreComponentItem {

    private final float energyCostPerSecond;  // 每秒耗电量

    /**
     * @param properties           物品属性
     * @param energyCostPerSecond  每秒耗电量
     */
    public CpuComponentItem(Properties properties, float energyCostPerSecond) {
        super(properties);
        this.energyCostPerSecond = energyCostPerSecond;
    }

    public float getEnergyCostPerSecond() {
        return energyCostPerSecond;
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.CPU;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.robot_player.cpu.desc"));
        tooltip.add(Component.translatable("tooltip.robot_player.cpu.effect"));
        if (energyCostPerSecond > 0) {
            tooltip.add(Component.translatable("tooltip.robot_player.energy.cost",
                    Component.literal(String.format("%.1f", energyCostPerSecond) + " ⚡/s").withColor(0xFFAA00)));
        }
    }
}