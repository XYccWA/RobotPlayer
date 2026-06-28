package org.xyccwa.robot_player.modItem.coreItem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 推进系统组件 - 提供推进能力，消耗电量和燃料
 * 属性：
 * - 最大推进加速度（格/秒²）
 * - 档位数（在最大加速度下分多少档）
 * - 燃料容量（决定玩家燃料最大值）
 * - 燃料消耗倍率（实际消耗速率 = 档位 × 倍率）
 * - 耗电量（每秒消耗）
 */
public class ThrusterComponentItem extends CoreComponentItem {

    private final float maxAcceleration;      // 最大推进加速度 (格/秒²)
    private final int maxGear;                // 最大档位数
    private final int fuelCapacity;           // 燃料容量
    private final float fuelConsumptionMultiplier; // 燃料消耗倍率
    private final float energyCostPerSecond;  // 每秒耗电量

    /**
     * @param properties                    物品属性
     * @param maxAcceleration               最大推进加速度 (格/秒²)
     * @param maxGear                       最大档位数 (最小档位为1)
     * @param fuelCapacity                  燃料容量
     * @param fuelConsumptionMultiplier     燃料消耗倍率 (实际消耗 = 档位 × 倍率)
     * @param energyCostPerSecond           每秒耗电量
     */
    public ThrusterComponentItem(Properties properties,
                                 float maxAcceleration,
                                 int maxGear,
                                 int fuelCapacity,
                                 float fuelConsumptionMultiplier,
                                 float energyCostPerSecond) {
        super(properties);
        this.maxAcceleration = maxAcceleration;
        this.maxGear = Math.max(1, maxGear);
        this.fuelCapacity = Math.max(0, fuelCapacity);
        this.fuelConsumptionMultiplier = fuelConsumptionMultiplier;
        this.energyCostPerSecond = energyCostPerSecond;
    }

    public float getMaxAcceleration() {
        return maxAcceleration;
    }

    public int getMaxGear() {
        return maxGear;
    }

    public int getFuelCapacity() {
        return fuelCapacity;
    }

    public float getFuelConsumptionMultiplier() {
        return fuelConsumptionMultiplier;
    }

    public float getEnergyCostPerSecond() {
        return energyCostPerSecond;
    }

    /**
     * 计算指定档位的加速度
     * @param gear 档位 (1 ~ maxGear)
     * @return 该档位的加速度值
     */
    public float getAccelerationForGear(int gear) {
        int clampedGear = Math.max(1, Math.min(gear, maxGear));
        return (maxAcceleration / maxGear) * clampedGear;
    }

    /**
     * 计算指定档位的燃料消耗速率 (每秒)
     * @param gear 档位 (1 ~ maxGear)
     * @return 该档位的燃料消耗速率 (单位/秒)
     */
    public float getFuelConsumptionForGear(int gear) {
        int clampedGear = Math.max(1, Math.min(gear, maxGear));
        return clampedGear * fuelConsumptionMultiplier;
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.THRUSTER;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.robot_player.thruster.desc"));
        tooltip.add(Component.translatable("tooltip.robot_player.thruster.max_acceleration",
                Component.literal(String.format("%.1f", maxAcceleration)).withColor(0x55FF55)));
        tooltip.add(Component.translatable("tooltip.robot_player.thruster.max_gear",
                Component.literal(String.valueOf(maxGear)).withColor(0x55FF55)));
        tooltip.add(Component.translatable("tooltip.robot_player.thruster.fuel_capacity",
                Component.literal("+" + fuelCapacity).withColor(0xFFAA00)));
        tooltip.add(Component.translatable("tooltip.robot_player.thruster.fuel_multiplier",
                Component.literal(String.format("%.2f", fuelConsumptionMultiplier)).withColor(0xFFAA00)));
        if (energyCostPerSecond > 0) {
            tooltip.add(Component.translatable("tooltip.robot_player.energy.cost",
                    Component.literal(String.format("%.1f", energyCostPerSecond) + " ⚡/s").withColor(0xFFAA00)));
        }
    }
}