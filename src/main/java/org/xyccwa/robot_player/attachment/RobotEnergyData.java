package org.xyccwa.robot_player.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

/**
 * 机器人电量数据
 */
public class RobotEnergyData implements INBTSerializable<CompoundTag> {

    private int energy;           // 当前电量
    private int maxEnergy;        // 最大电量

    public RobotEnergyData() {
        this.energy = 0;
        this.maxEnergy = 100;
    }

    public RobotEnergyData(int initialEnergy, int maxEnergy) {
        this.energy = Math.min(initialEnergy, maxEnergy);
        this.maxEnergy = maxEnergy;
    }

    // ========== Getter / Setter ==========

    public int getEnergy() {
        return energy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(energy, maxEnergy));
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = Math.max(1, maxEnergy);
        if (this.energy > this.maxEnergy) {
            this.energy = this.maxEnergy;
        }
    }

    // ========== 能量操作 ==========

    /**
     * 增加电量
     * @return 实际增加的量
     */
    public int addEnergy(int amount) {
        int newEnergy = energy + amount;
        int actualAdded = Math.min(amount, maxEnergy - energy);
        setEnergy(newEnergy);
        return actualAdded;
    }

    /**
     * 消耗电量
     * @return 是否成功消耗（电量足够）
     */
    public boolean consumeEnergy(int amount) {
        if (energy >= amount) {
            energy -= amount;
            return true;
        }
        return false;
    }

    /**
     * 是否有足够的电量
     */
    public boolean hasEnough(int amount) {
        return energy >= amount;
    }

    /**
     * 获取电量百分比 (0.0 - 1.0)
     */
    public float getEnergyPercent() {
        return (float) energy / maxEnergy;
    }

    /**
     * 完全充能
     */
    public void fullyCharge() {
        energy = maxEnergy;
    }

    /**
     * 清空电量
     */
    public void empty() {
        energy = 0;
    }

    /**
     * 是否已满
     */
    public boolean isFull() {
        return energy >= maxEnergy;
    }

    /**
     * 是否为空
     */
    public boolean isEmpty() {
        return energy <= 0;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("energy", energy);
        tag.putInt("maxEnergy", maxEnergy);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag tag) {
        this.energy = tag.getInt("energy");
        this.maxEnergy = tag.getInt("maxEnergy");
        if (maxEnergy <= 0) maxEnergy = 100;
        if (energy > maxEnergy) energy = maxEnergy;
    }

    @Override
    public String toString() {
        return energy + "/" + maxEnergy;
    }
}