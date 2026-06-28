package org.xyccwa.robot_player.attachment;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.xyccwa.robot_player.RobotPlayer;

import java.util.function.Supplier;

public class RobotPlayerAttachments {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, RobotPlayer.MOD_ID);

    /**
     * 机器人核心组件数据附件
     * 直接附加在玩家实体上，随玩家存档保存
     */
    public static final Supplier<AttachmentType<RobotCoreData>> ROBOT_CORE =
            ATTACHMENT_TYPES.register(
                    "robot_core",
                    () -> AttachmentType.serializable(RobotCoreData::new).build()
            );

    /**
     * 机器人扩展组件区附件 - 动态槽位
     * 槽位数量可以在游戏内动态变化
     */
    public static final Supplier<AttachmentType<DynamicItemStack>> ROBOT_EXPANSIONS =
            ATTACHMENT_TYPES.register(
                    "robot_expansions",
                    () -> AttachmentType.serializable(DynamicItemStack::new).build()
            );

    /**
     * 机器人电量数据附件
     */
    public static final Supplier<AttachmentType<RobotEnergyData>> ROBOT_ENERGY =
            ATTACHMENT_TYPES.register(
                    "robot_energy",
                    () -> AttachmentType.serializable(RobotEnergyData::new).build()
            );

    /**
     * 机器人燃料数据附件
     */
    public static final Supplier<AttachmentType<RobotFuelData>> ROBOT_FUEL =
            ATTACHMENT_TYPES.register(
                    "robot_fuel",
                    () -> AttachmentType.serializable(RobotFuelData::new).build()
            );

    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
    }
}