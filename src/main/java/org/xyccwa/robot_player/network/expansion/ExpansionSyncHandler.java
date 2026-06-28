package org.xyccwa.robot_player.network.expansion;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.xyccwa.robot_player.attachment.DynamicItemStack;
import org.xyccwa.robot_player.attachment.RobotPlayerAttachments;

public class ExpansionSyncHandler {

    public static void handleData(final ExpansionSyncPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                DynamicItemStack expansions = player.getData(RobotPlayerAttachments.ROBOT_EXPANSIONS);
                data.applyTo(expansions);
            }
        });
    }
}