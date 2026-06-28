package org.xyccwa.robot_player.network.fuel;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.xyccwa.robot_player.attachment.RobotPlayerAttachments;

public class FuelSyncHandler {

    public static void handleData(final FuelSyncPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                var fuel = player.getData(RobotPlayerAttachments.ROBOT_FUEL);
                fuel.setFuel(data.fuel());
                fuel.setMaxFuel(data.maxFuel());
            }
        });
    }
}