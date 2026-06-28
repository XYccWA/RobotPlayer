package org.xyccwa.robot_player.modItem.coreItem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 结构框架组件 - 决定最大生命值和附加槽位数量
 */
public class FrameComponentItem extends CoreComponentItem {

    private final int bonusMaxHealth;      // 增加的最大生命值（半心为单位，2 = 1颗心）
    private final int bonusExpansionSlots; // 增加的扩展槽位数量

    /**
     * @param properties          物品属性
     * @param bonusMaxHealth      增加的最大生命值（半心，例如 2 = 1颗心，4 = 2颗心）
     * @param bonusExpansionSlots 增加的扩展槽位数量
     */
    public FrameComponentItem(Properties properties, int bonusMaxHealth, int bonusExpansionSlots) {
        super(properties);
        this.bonusMaxHealth = bonusMaxHealth;
        this.bonusExpansionSlots = bonusExpansionSlots;
    }

    public int getBonusMaxHealth() {
        return bonusMaxHealth;
    }

    public int getBonusExpansionSlots() {
        return bonusExpansionSlots;
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.FRAME;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.robot_player.frame.desc"));

        if (bonusMaxHealth > 0) {
            float hearts = bonusMaxHealth / 2.0f;
            tooltip.add(Component.translatable("tooltip.robot_player.frame.health",
                    Component.literal("+" + hearts + "❤️").withColor(0xFF5555)));
        }

        if (bonusExpansionSlots > 0) {
            tooltip.add(Component.translatable("tooltip.robot_player.frame.slots",
                    Component.literal("+" + bonusExpansionSlots).withColor(0x55FF55)));
        }
    }
}