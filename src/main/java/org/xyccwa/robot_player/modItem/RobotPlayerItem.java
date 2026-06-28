package org.xyccwa.robot_player.modItem;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.xyccwa.robot_player.RobotPlayer;
import org.xyccwa.robot_player.modItem.coreItem.*;

public class RobotPlayerItem {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RobotPlayer.MOD_ID);

    // 雷达
    public static final DeferredItem<RadarComponentItem> BASIC_RADAR =
            ITEMS.register("basic_radar",
                    () -> new RadarComponentItem(new Item.Properties(), 2.0f));

    // 高级雷达
    public static final DeferredItem<RadarComponentItem> ADVANCED_RADAR =
            ITEMS.register("advanced_radar",
                    () -> new RadarComponentItem(new Item.Properties(), 5.0f));

    // 主控芯片
    public static final DeferredItem<CpuComponentItem> BASIC_CPU =
            ITEMS.register("basic_cpu",
                    () -> new CpuComponentItem(new Item.Properties(), 1.5f));

    // 高级主控芯片
    public static final DeferredItem<CpuComponentItem> ADVANCED_CPU =
            ITEMS.register("advanced_cpu",
                    () -> new CpuComponentItem(new Item.Properties(), 3.0f));

    // ========== 推进系统 ==========
    // 基础推进器: 最大加速度 20格/秒², 5档, 燃料容量 100, 消耗倍率 0.02, 耗电 3.0/s
    public static final DeferredItem<ThrusterComponentItem> BASIC_THRUSTER =
            ITEMS.register("basic_thruster",
                    () -> new ThrusterComponentItem(
                            new Item.Properties(),
                            20.0f,      // 最大加速度
                            4,          // 最大档位
                            100,        // 燃料容量
                            0.02f,      // 燃料消耗倍率
                            2.0f        // 每秒耗电量
                    ));

    // 高级推进器: 最大加速度 40格/秒², 8档, 燃料容量 300, 消耗倍率 0.025, 耗电 6.0/s
    public static final DeferredItem<ThrusterComponentItem> ADVANCED_THRUSTER =
            ITEMS.register("advanced_thruster",
                    () -> new ThrusterComponentItem(
                            new Item.Properties(),
                            40.0f,      // 最大加速度
                            8,          // 最大档位
                            300,        // 燃料容量
                            0.025f,     // 燃料消耗倍率
                            4.0f        // 每秒耗电量
                    ));

    // 结构框架
    public static final DeferredItem<FrameComponentItem> BASIC_FRAME =
            ITEMS.register("basic_frame",
                    () -> new FrameComponentItem(new Item.Properties(), 20, 3));

    // 高级结构框架
    public static final DeferredItem<FrameComponentItem> ADVANCED_FRAME =
            ITEMS.register("advanced_frame",
                    () -> new FrameComponentItem(new Item.Properties(), 40, 6));

    // 主电池组
    public static final DeferredItem<BatteryComponentItem> BASIC_BATTERY =
            ITEMS.register("basic_battery",
                    () -> new BatteryComponentItem(new Item.Properties(), 100000));

    // 高级电池组
    public static final DeferredItem<BatteryComponentItem> ADVANCED_BATTERY =
            ITEMS.register("advanced_battery",
                    () -> new BatteryComponentItem(new Item.Properties(), 300000));
}
