package org.xyccwa.robot_player.modItem.coreItem;

import net.minecraft.world.item.Item;

/**
 * 核心组件物品基类
 */
public abstract class CoreComponentItem extends Item {

    public CoreComponentItem(Properties properties) {
        super(properties.stacksTo(1));  // 核心组件只能堆叠1个
    }

    /**
     * 获取组件类型名称，用于判断是哪种核心组件
     */
    public abstract ComponentType getComponentType();

    /**
     * 组件类型枚举
     */
    public enum ComponentType {
        RADAR,
        CPU,
        THRUSTER,
        FRAME,
        BATTERY
    }
}