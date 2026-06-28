package org.xyccwa.robot_player.network.expansion;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.xyccwa.robot_player.RobotPlayer;
import org.xyccwa.robot_player.attachment.DynamicItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 扩展组件同步数据包
 * 同步动态扩展槽位的所有物品
 */
public record ExpansionSyncPayload(
        List<ItemStack> items
) implements CustomPacketPayload {

    public static final Type<ExpansionSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RobotPlayer.MOD_ID, "expansion_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExpansionSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()),
                    ExpansionSyncPayload::items,
                    ExpansionSyncPayload::new
            );

    /**
     * 从 DynamicItemStack 创建数据包
     */
    public static ExpansionSyncPayload fromData(DynamicItemStack expansions) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < expansions.getSlots(); i++) {
            items.add(expansions.getStackInSlot(i).copy());
        }
        return new ExpansionSyncPayload(items);
    }

    /**
     * 将数据包应用到 DynamicItemStack
     */
    public void applyTo(DynamicItemStack expansions) {
        expansions.setSlots(items.size());
        for (int i = 0; i < items.size(); i++) {
            expansions.setStackInSlot(i, items.get(i).copy());
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}