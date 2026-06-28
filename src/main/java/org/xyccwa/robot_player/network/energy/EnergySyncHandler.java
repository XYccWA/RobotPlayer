package org.xyccwa.robot_player.network.energy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.xyccwa.robot_player.attachment.RobotPlayerAttachments;

public class EnergySyncHandler {

    public static void handleData(final EnergySyncPayload data, final IPayloadContext context) {
        // 在客户端执行
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                var energy = player.getData(RobotPlayerAttachments.ROBOT_ENERGY);
                energy.setEnergy(data.energy());
                energy.setMaxEnergy(data.maxEnergy());
            }
        });
    }
}