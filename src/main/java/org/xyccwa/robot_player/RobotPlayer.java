package org.xyccwa.robot_player;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(RobotPlayer.MODID)
public class RobotPlayer {
    public static final String MODID = "robot_player";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RobotPlayer(IEventBus modEventBus) {

    }
}
