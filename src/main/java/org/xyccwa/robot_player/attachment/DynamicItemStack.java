package org.xyccwa.robot_player.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 动态扩展容器 - 支持运行时增删槽位
 */
public class DynamicItemStack implements INBTSerializable<CompoundTag> {

    private final List<ItemStack> stacks = new ArrayList<>();

    /**
     * 获取当前槽位数量
     */
    public int getSlots() {
        return stacks.size();
    }

    /**
     * 设置槽位数量（会保留原有物品，新增槽位为空，删除槽位会丢弃物品）
     * @param newSize 新的槽位数量
     */
    public void setSlots(int newSize) {
        if (newSize < 0) return;

        int currentSize = stacks.size();

        if (newSize > currentSize) {
            // 增加槽位
            for (int i = currentSize; i < newSize; i++) {
                stacks.add(ItemStack.EMPTY);
            }
        } else if (newSize < currentSize) {
            // 减少槽位 - 丢弃超出部分的物品
            stacks.subList(newSize, currentSize).clear();
        }
    }

    /**
     * 获取指定槽位的物品
     */
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= stacks.size()) {
            return ItemStack.EMPTY;
        }
        return stacks.get(slot);
    }

    /**
     * 设置指定槽位的物品
     * @return 被替换掉的旧物品
     */
    public ItemStack setStackInSlot(int slot, ItemStack stack) {
        if (slot < 0 || slot >= stacks.size()) {
            return stack;
        }
        ItemStack old = stacks.get(slot).copy();
        stacks.set(slot, stack.copy());
        return old;
    }

    /**
     * 在末尾添加一个新槽位
     * @return 新槽位的索引
     */
    public int addSlot() {
        stacks.add(ItemStack.EMPTY);
        return stacks.size() - 1;
    }

    /**
     * 在指定位置插入新槽位
     * @param index 插入位置
     */
    public void insertSlot(int index) {
        if (index < 0 || index > stacks.size()) {
            index = stacks.size();
        }
        stacks.add(index, ItemStack.EMPTY);
    }

    /**
     * 移除指定槽位（槽位中的物品会被丢弃）
     * @return 被移除的物品
     */
    public ItemStack removeSlot(int slot) {
        if (slot < 0 || slot >= stacks.size()) {
            return ItemStack.EMPTY;
        }
        return stacks.remove(slot);
    }

    /**
     * 清空所有槽位
     */
    public void clear() {
        for (int i = 0; i < stacks.size(); i++) {
            stacks.set(i, ItemStack.EMPTY);
        }
    }

    /**
     * 检查是否为空
     */
    public boolean isEmpty() {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    /**
     * 获取所有槽位的不可修改视图
     */
    public List<ItemStack> getAllStacks() {
        return Collections.unmodifiableList(stacks);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();

        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) {
                list.add(new CompoundTag()); // 空槽位用空CompoundTag占位
            } else {
                list.add(stack.save(lookupProvider));
            }
        }

        tag.put("items", list);
        tag.putInt("size", stacks.size());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag tag) {
        stacks.clear();

        int size = tag.getInt("size");
        ListTag list = tag.getList("items", Tag.TAG_COMPOUND);

        for (int i = 0; i < size && i < list.size(); i++) {
            CompoundTag itemTag = list.getCompound(i);
            if (itemTag.isEmpty()) {
                stacks.add(ItemStack.EMPTY);
            } else {
                stacks.add(ItemStack.parse(lookupProvider, itemTag).orElse(ItemStack.EMPTY));
            }
        }

        // 处理数据不一致的情况
        while (stacks.size() < size) {
            stacks.add(ItemStack.EMPTY);
        }
    }
}