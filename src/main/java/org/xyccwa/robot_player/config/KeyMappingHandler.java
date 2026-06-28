package org.xyccwa.robot_player.config;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import org.xyccwa.robot_player.RobotPlayer;

@EventBusSubscriber(modid = RobotPlayer.MOD_ID, value = Dist.CLIENT)
public class KeyMappingHandler {

    public static final KeyMapping TOGGLE_MODE_KEY = new KeyMapping(
            "key.robot_player.toggle_mode",
            GLFW.GLFW_KEY_R,
            "key.categories.robot_player"
    );

    public static final KeyMapping GEAR_UP_KEY = new KeyMapping(
            "key.robot_player.gear_up",
            GLFW.GLFW_KEY_LEFT_SHIFT,
            "key.categories.robot_player"
    );

    public static final KeyMapping GEAR_DOWN_KEY = new KeyMapping(
            "key.robot_player.gear_down",
            GLFW.GLFW_KEY_LEFT_CONTROL,
            "key.categories.robot_player"
    );

    public static final KeyMapping UP_KEY = new KeyMapping(
            "key.robot_player.space",
            GLFW.GLFW_KEY_C,
            "key.categories.robot_player"
    );

    public static final KeyMapping DOWN_KEY = new KeyMapping(
            "key.robot_player.c",
            GLFW.GLFW_KEY_SPACE,
            "key.categories.robot_player"
    );

    public static final KeyMapping FORWARD_KEY = new KeyMapping(
            "key.robot_player.forward",
            GLFW.GLFW_KEY_W,
            "key.categories.robot_player"
    );

    public static final KeyMapping BACKWARD_KEY = new KeyMapping(
            "key.robot_player.backward",
            GLFW.GLFW_KEY_S,
            "key.categories.robot_player"
    );

    public static final KeyMapping LEFT_KEY = new KeyMapping(
            "key.robot_player.left",
            GLFW.GLFW_KEY_LEFT,
            "key.categories.robot_player"
    );

    public static final KeyMapping RIGHT_KEY = new KeyMapping(
            "key.robot_player.right",
            GLFW.GLFW_KEY_RIGHT,
            "key.categories.robot_player"
    );

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_MODE_KEY);
        event.register(GEAR_UP_KEY);
        event.register(GEAR_DOWN_KEY);
        event.register(UP_KEY);
        event.register(DOWN_KEY);
        event.register(FORWARD_KEY);
        event.register(BACKWARD_KEY);
        event.register(LEFT_KEY);
        event.register(RIGHT_KEY);
    }

}