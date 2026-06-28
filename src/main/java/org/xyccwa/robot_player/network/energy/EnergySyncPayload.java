package org.xyccwa.robot_player.network.energy;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.xyccwa.robot_player.RobotPlayer;

public record EnergySyncPayload(int energy, int maxEnergy) implements CustomPacketPayload {

    public static final Type<EnergySyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RobotPlayer.MOD_ID, "energy_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnergySyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, EnergySyncPayload::energy,
                    ByteBufCodecs.INT, EnergySyncPayload::maxEnergy,
                    EnergySyncPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}