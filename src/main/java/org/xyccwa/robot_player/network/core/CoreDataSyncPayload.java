package org.xyccwa.robot_player.network.core;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.xyccwa.robot_player.RobotPlayer;
import org.xyccwa.robot_player.attachment.RobotCoreData;

/**
 * 核心组件同步数据包
 * 同步5个核心槽位的数据
 */
public record CoreDataSyncPayload(
        ItemStack radar,
        ItemStack cpu,
        ItemStack thruster,
        ItemStack frame,
        ItemStack battery
) implements CustomPacketPayload {

    public static final Type<CoreDataSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RobotPlayer.MOD_ID, "core_data_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CoreDataSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.OPTIONAL_STREAM_CODEC, CoreDataSyncPayload::radar,
                    ItemStack.OPTIONAL_STREAM_CODEC, CoreDataSyncPayload::cpu,
                    ItemStack.OPTIONAL_STREAM_CODEC, CoreDataSyncPayload::thruster,
                    ItemStack.OPTIONAL_STREAM_CODEC, CoreDataSyncPayload::frame,
                    ItemStack.OPTIONAL_STREAM_CODEC, CoreDataSyncPayload::battery,
                    CoreDataSyncPayload::new
            );

    /**
     * 从 RobotCoreData 创建数据包
     */
    public static CoreDataSyncPayload fromData(RobotCoreData data) {
        return new CoreDataSyncPayload(
                data.getRadar().copy(),
                data.getCpu().copy(),
                data.getThruster().copy(),
                data.getFrame().copy(),
                data.getBattery().copy()
        );
    }

    /**
     * 将数据包应用到 RobotCoreData
     */
    public void applyTo(RobotCoreData data, HolderLookup.Provider lookupProvider) {
        data.setItemInternal(RobotCoreData.SLOT_RADAR, radar.copy());
        data.setItemInternal(RobotCoreData.SLOT_CPU, cpu.copy());
        data.setItemInternal(RobotCoreData.SLOT_THRUSTER, thruster.copy());
        data.setItemInternal(RobotCoreData.SLOT_FRAME, frame.copy());
        data.setItemInternal(RobotCoreData.SLOT_BATTERY, battery.copy());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}