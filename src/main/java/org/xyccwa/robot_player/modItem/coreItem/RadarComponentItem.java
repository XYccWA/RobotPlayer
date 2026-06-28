package org.xyccwa.robot_player.modItem.coreItem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 雷达组件 - 用于探测敌人/资源，消耗电量
 */
public class RadarComponentItem extends CoreComponentItem {

    private final float energyCostPerSecond;  // 每秒耗电量

    /**
     * @param properties           物品属性
     * @param energyCostPerSecond  每秒耗电量
     */
    public RadarComponentItem(Properties properties, float energyCostPerSecond) {
        super(properties);
        this.energyCostPerSecond = energyCostPerSecond;
    }

    public float getEnergyCostPerSecond() {
        return energyCostPerSecond;
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.RADAR;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.robot_player.radar.desc"));
        tooltip.add(Component.translatable("tooltip.robot_player.radar.effect"));
        if (energyCostPerSecond > 0) {
            tooltip.add(Component.translatable("tooltip.robot_player.energy.cost",
                    Component.literal(String.format("%.1f", energyCostPerSecond) + " ⚡/s").withColor(0xFFAA00)));
        }
    }
}