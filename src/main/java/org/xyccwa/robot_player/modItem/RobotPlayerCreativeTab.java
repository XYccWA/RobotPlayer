package org.xyccwa.robot_player.modItem;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.xyccwa.robot_player.RobotPlayer;

import java.util.function.Supplier;

public class RobotPlayerCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RobotPlayer.MOD_ID);

    public static final Supplier<CreativeModeTab> ROBOT_PLAYER_TAB = CREATIVE_MODE_TABS.register("robot_player_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.robot_player.creative_tab"))
            .icon(() -> new ItemStack(Items.IRON_ORE))
            .displayItems((parameters, output) -> {
                output.accept(RobotPlayerItem.BASIC_FRAME.get());
                output.accept(RobotPlayerItem.ADVANCED_FRAME.get());
                output.accept(RobotPlayerItem.BASIC_CPU.get());
                output.accept(RobotPlayerItem.ADVANCED_CPU.get());
                output.accept(RobotPlayerItem.BASIC_RADAR.get());
                output.accept(RobotPlayerItem.ADVANCED_RADAR.get());
                output.accept(RobotPlayerItem.BASIC_THRUSTER.get());
                output.accept(RobotPlayerItem.ADVANCED_THRUSTER.get());
                output.accept(RobotPlayerItem.BASIC_BATTERY.get());
                output.accept(RobotPlayerItem.ADVANCED_BATTERY.get());

            })
            .build());
}
