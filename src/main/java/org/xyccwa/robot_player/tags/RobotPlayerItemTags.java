package org.xyccwa.robot_player.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.xyccwa.robot_player.RobotPlayer;

public class RobotPlayerItemTags {

    public static final TagKey<Item> ROBOT_CORE_TAGS = create("robot_core");

    public static final TagKey<Item> RADAR_TAGS = create("radar_components");

    public static final TagKey<Item> CPU_TAGS = create("cpu_components");

    public static final TagKey<Item> THRUSTER_TAGS = create("thruster_components");

    public static final TagKey<Item> FRAME_TAGS = create("frame_components");

    public static final TagKey<Item> BATTERY_TAG = create("battery_components");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(RobotPlayer.MOD_ID, name));
    }
}
