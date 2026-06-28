package org.xyccwa.robot_player.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.xyccwa.robot_player.tags.RobotPlayerItemTags;

public class RobotCoreData implements INBTSerializable<CompoundTag> {

    // 槽位索引常量
    public static final int SLOT_RADAR = 0;
    public static final int SLOT_CPU = 1;
    public static final int SLOT_THRUSTER = 2;
    public static final int SLOT_FRAME = 3;
    public static final int SLOT_BATTERY = 4;      // 新增：主电池组
    public static final int TOTAL_SLOTS = 5;        // 改为5



    private final ItemStack[] items = new ItemStack[TOTAL_SLOTS];

    public RobotCoreData() {
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            items[i] = ItemStack.EMPTY;
        }
    }

    // ========== 验证方法 ==========

    /**
     * 检查物品是否可以放入指定槽位
     */
    public static boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (stack.isEmpty()) return true;

        return switch (slot) {
            case SLOT_RADAR -> stack.is(RobotPlayerItemTags.RADAR_TAGS);
            case SLOT_CPU -> stack.is(RobotPlayerItemTags.CPU_TAGS);
            case SLOT_THRUSTER -> stack.is(RobotPlayerItemTags.THRUSTER_TAGS);
            case SLOT_FRAME -> stack.is(RobotPlayerItemTags.FRAME_TAGS);
            case SLOT_BATTERY -> stack.is(RobotPlayerItemTags.BATTERY_TAG);
            default -> false;
        };
    }

    /**
     * 获取槽位对应的标签（用于显示提示）
     */
    public static TagKey<Item> getTagForSlot(int slot) {
        return switch (slot) {
            case SLOT_RADAR -> RobotPlayerItemTags.RADAR_TAGS;
            case SLOT_CPU -> RobotPlayerItemTags.CPU_TAGS;
            case SLOT_THRUSTER -> RobotPlayerItemTags.THRUSTER_TAGS;
            case SLOT_FRAME -> RobotPlayerItemTags.FRAME_TAGS;
            case SLOT_BATTERY -> RobotPlayerItemTags.BATTERY_TAG;
            default -> null;
        };
    }

    /**
     * 获取槽位名称
     */
    public static String getSlotName(int slot) {
        return switch (slot) {
            case SLOT_RADAR -> "雷达";
            case SLOT_CPU -> "主控芯片";
            case SLOT_THRUSTER -> "推进系统";
            case SLOT_FRAME -> "结构框架";
            case SLOT_BATTERY -> "主电池组";
            default -> "未知";
        };
    }

    /**
     * 获取槽位的英文名称（用于命令）
     */
    public static String getSlotEnglishName(int slot) {
        return switch (slot) {
            case SLOT_RADAR -> "radar";
            case SLOT_CPU -> "cpu";
            case SLOT_THRUSTER -> "thruster";
            case SLOT_FRAME -> "frame";
            case SLOT_BATTERY -> "battery";
            default -> "unknown";
        };
    }

    // ========== 基础操作（带验证） ==========

    /**
     * 尝试设置指定槽位的物品
     * @return 设置成功返回 true，失败返回 false
     */
    public boolean trySetItem(int slot, ItemStack stack) {
        if (!isItemValidForSlot(slot, stack)) {
            return false;
        }
        items[slot] = stack.copy();
        return true;
    }

    /**
     * 强制设置物品（绕过验证，用于内部数据恢复）
     */
    public void setItemInternal(int slot, ItemStack stack) {
        if (slot >= 0 && slot < TOTAL_SLOTS) {
            items[slot] = stack.copy();
        }
    }

    /**
     * 获取物品（不复制，谨慎使用）
     */
    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= TOTAL_SLOTS) return ItemStack.EMPTY;
        return items[slot];
    }

    /**
     * 获取物品的副本
     */
    public ItemStack getItemCopy(int slot) {
        return items[slot].copy();
    }

    /**
     * 清空指定槽位
     */
    public void clearSlot(int slot) {
        if (slot >= 0 && slot < TOTAL_SLOTS) {
            items[slot] = ItemStack.EMPTY;
        }
    }

    // ========== 便捷方法（按组件类型） ==========

    public ItemStack getRadar() { return items[SLOT_RADAR]; }
    public boolean trySetRadar(ItemStack stack) { return trySetItem(SLOT_RADAR, stack); }
    public boolean hasRadar() { return !items[SLOT_RADAR].isEmpty(); }

    public ItemStack getCpu() { return items[SLOT_CPU]; }
    public boolean trySetCpu(ItemStack stack) { return trySetItem(SLOT_CPU, stack); }
    public boolean hasCpu() { return !items[SLOT_CPU].isEmpty(); }

    public ItemStack getThruster() { return items[SLOT_THRUSTER]; }
    public boolean trySetThruster(ItemStack stack) { return trySetItem(SLOT_THRUSTER, stack); }
    public boolean hasThruster() { return !items[SLOT_THRUSTER].isEmpty(); }

    public ItemStack getFrame() { return items[SLOT_FRAME]; }
    public boolean trySetFrame(ItemStack stack) { return trySetItem(SLOT_FRAME, stack); }
    public boolean hasFrame() { return !items[SLOT_FRAME].isEmpty(); }

    public ItemStack getBattery() { return items[SLOT_BATTERY]; }
    public boolean trySetBattery(ItemStack stack) { return trySetItem(SLOT_BATTERY, stack); }
    public boolean hasBattery() { return !items[SLOT_BATTERY].isEmpty(); }

    // ========== 序列化 ==========

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag tag = new CompoundTag();
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            if (!items[i].isEmpty()) {
                tag.put("slot_" + i, items[i].save(lookupProvider));
            }
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag tag) {
        // 重置为空
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            items[i] = ItemStack.EMPTY;
        }
        // 读取数据（使用内部方法绕过验证）
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            if (tag.contains("slot_" + i, Tag.TAG_COMPOUND)) {
                ItemStack stack = ItemStack.parse(lookupProvider, tag.getCompound("slot_" + i))
                        .orElse(ItemStack.EMPTY);
                setItemInternal(i, stack);
            }
        }
    }

    // ========== 其他辅助方法 ==========

    public RobotCoreData copy() {
        RobotCoreData copy = new RobotCoreData();
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            copy.setItemInternal(i, this.items[i].copy());
        }
        return copy;
    }

    public boolean matches(RobotCoreData other) {
        if (other == null) return false;
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            if (!ItemStack.matches(this.items[i], other.items[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("RobotCoreData[radar=%s, cpu=%s, thruster=%s, frame=%s, battery=%s]",
                getRadar().getItem().getDescriptionId(),
                getCpu().getItem().getDescriptionId(),
                getThruster().getItem().getDescriptionId(),
                getFrame().getItem().getDescriptionId(),
                getBattery().getItem().getDescriptionId()
        );
    }

    public boolean isAllEmpty() {
        for (int i = 0; i < TOTAL_SLOTS; i++)
            if (!items[i].isEmpty())
                return false;
        return true;
    }
}