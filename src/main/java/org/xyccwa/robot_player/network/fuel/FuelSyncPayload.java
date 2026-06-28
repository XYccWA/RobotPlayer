package org.xyccwa.robot_player.network.fuel;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.xyccwa.robot_player.RobotPlayer;

public record FuelSyncPayload(float fuel, float maxFuel) implements CustomPacketPayload {

    public static final Type<FuelSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RobotPlayer.MOD_ID, "fuel_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FuelSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT, FuelSyncPayload::fuel,
                    ByteBufCodecs.FLOAT, FuelSyncPayload::maxFuel,
                    FuelSyncPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}