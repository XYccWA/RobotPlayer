package org.xyccwa.robot_player.modItem.coreItem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 主电池组组件 - 决定最大电量
 */
public class BatteryComponentItem extends CoreComponentItem {

    private final int bonusMaxEnergy;     // 增加的最大电量

    /**
     * @param properties       物品属性
     * @param bonusMaxEnergy   增加的最大电量
     */
    public BatteryComponentItem(Properties properties, int bonusMaxEnergy) {
        super(properties);
        this.bonusMaxEnergy = bonusMaxEnergy;
    }

    public int getBonusMaxEnergy() {
        return bonusMaxEnergy;
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.BATTERY;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.robot_player.battery.desc"));

        if (bonusMaxEnergy > 0) {
            tooltip.add(Component.translatable("tooltip.robot_player.battery.energy",
                    Component.literal("+" + bonusMaxEnergy + " ⚡").withColor(0xFFAA00)));
        }
    }
}