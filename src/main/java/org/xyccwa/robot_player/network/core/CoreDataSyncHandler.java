package org.xyccwa.robot_player.network.core;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.xyccwa.robot_player.attachment.RobotCoreData;
import org.xyccwa.robot_player.attachment.RobotPlayerAttachments;

public class CoreDataSyncHandler {

    public static void handleData(final CoreDataSyncPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                RobotCoreData coreData = player.getData(RobotPlayerAttachments.ROBOT_CORE);
                data.applyTo(coreData, player.registryAccess());
            }
        });
    }
}