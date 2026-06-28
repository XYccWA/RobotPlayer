package org.xyccwa.robot_player.dataGen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import org.xyccwa.robot_player.RobotPlayer;
import org.xyccwa.robot_player.modItem.RobotPlayerItem;
import org.xyccwa.robot_player.tags.RobotPlayerItemTags;

import java.util.concurrent.CompletableFuture;

public class RobotPlayerItemTagsProvider extends ItemTagsProvider {
    public RobotPlayerItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, RobotPlayer.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(RobotPlayerItemTags.ROBOT_CORE_TAGS)
                .add(RobotPlayerItem.BASIC_RADAR.get())
                .add(RobotPlayerItem.BASIC_CPU.get())
                .add(RobotPlayerItem.BASIC_THRUSTER.get())
                .add(RobotPlayerItem.BASIC_FRAME.get())
                .add(RobotPlayerItem.BASIC_BATTERY.get());

        tag(RobotPlayerItemTags.RADAR_TAGS)
                .add(RobotPlayerItem.BASIC_RADAR.get());

        tag(RobotPlayerItemTags.CPU_TAGS)
                .add(RobotPlayerItem.BASIC_CPU.get());

        tag(RobotPlayerItemTags.THRUSTER_TAGS)
                .add(RobotPlayerItem.BASIC_THRUSTER.get());

        tag(RobotPlayerItemTags.FRAME_TAGS)
                .add(RobotPlayerItem.BASIC_FRAME.get());

        tag(RobotPlayerItemTags.BATTERY_TAG)
                .add(RobotPlayerItem.BASIC_BATTERY.get());
    }
}
