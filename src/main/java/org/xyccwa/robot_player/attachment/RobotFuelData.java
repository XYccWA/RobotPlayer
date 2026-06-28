package org.xyccwa.robot_player.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

/**
 * 机器人燃料数据
 * 用于推进系统的独立资源
 */
public class RobotFuelData implements INBTSerializable<CompoundTag> {

    private float fuel;           // 当前燃料量
    private float maxFuel;        // 最大燃料量

    public RobotFuelData() {
        this.fuel = 0;
        this.maxFuel = 100;
    }

    public RobotFuelData(float initialFuel, float maxFuel) {
        this.fuel = Math.min(initialFuel, maxFuel);
        this.maxFuel = maxFuel;
    }

    // ========== Getter / Setter ==========

    public float getFuel() {
        return fuel;
    }

    public float getMaxFuel() {
        return maxFuel;
    }

    public void setFuel(float fuel) {
        this.fuel = Math.max(0, Math.min(fuel, maxFuel));
    }

    public void setMaxFuel(float maxFuel) {
        this.maxFuel = Math.max(1, maxFuel);
        if (this.fuel > this.maxFuel) {
            this.fuel = this.maxFuel;
        }
    }

    // ========== 燃料操作 ==========

    /**
     * 添加燃料
     * @return 实际添加的量
     */
    public float addFuel(float amount) {
        float actualAdded = Math.min(amount, maxFuel - fuel);
        fuel += actualAdded;
        return actualAdded;
    }

    /**
     * 消耗燃料
     * @return 是否成功消耗（燃料足够）
     */
    public boolean consumeFuel(float amount) {
        if (fuel >= amount) {
            fuel -= amount;
            return true;
        }
        return false;
    }

    /**
     * 是否有足够的燃料
     */
    public boolean hasEnough(float amount) {
        return fuel >= amount;
    }

    /**
     * 获取燃料百分比 (0.0 - 1.0)
     */
    public float getFuelPercent() {
        return (float) fuel / maxFuel;
    }

    /**
     * 完全加满
     */
    public void fullyFill() {
        fuel = maxFuel;
    }

    /**
     * 是否已满
     */
    public boolean isFull() {
        return fuel >= maxFuel;
    }

    /**
     * 是否为空
     */
    public boolean isEmpty() {
        return fuel <= 0;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("fuel", fuel);
        tag.putFloat("maxFuel", maxFuel);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag tag) {
        this.fuel = tag.getFloat("fuel");
        this.maxFuel = tag.getFloat("maxFuel");
        if (maxFuel <= 0) maxFuel = 100;
        if (fuel > maxFuel) fuel = maxFuel;
    }

    @Override
    public String toString() {
        return fuel + "/" + maxFuel;
    }
}