package org.xyccwa.robot_player;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.xyccwa.robot_player.attachment.RobotPlayerAttachments;
import org.xyccwa.robot_player.config.RobotPlayerConfig;
import org.xyccwa.robot_player.modBlock.RobotPlayerBlock;
import org.xyccwa.robot_player.modItem.RobotPlayerCreativeTab;
import org.xyccwa.robot_player.modItem.RobotPlayerItem;
import org.xyccwa.robot_player.network.core.CoreDataSyncHandler;
import org.xyccwa.robot_player.network.core.CoreDataSyncPayload;
import org.xyccwa.robot_player.network.energy.EnergySyncHandler;
import org.xyccwa.robot_player.network.energy.EnergySyncPayload;
import org.xyccwa.robot_player.network.expansion.ExpansionSyncHandler;
import org.xyccwa.robot_player.network.expansion.ExpansionSyncPayload;
import org.xyccwa.robot_player.network.fuel.FuelSyncHandler;
import org.xyccwa.robot_player.network.fuel.FuelSyncPayload;

@Mod(RobotPlayer.MOD_ID)
public class RobotPlayer {
    public static final String MOD_ID = "robot_player";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RobotPlayer(IEventBus modEventBus) {
        ModContainer container = ModLoadingContext.get().getActiveContainer();
        //配置
        container.registerConfig(ModConfig.Type.STARTUP, RobotPlayerConfig.SPEC);
        //方块
        RobotPlayerBlock.BLOCKS.register(modEventBus);
        //物品
        RobotPlayerItem.ITEMS.register(modEventBus);
        //创造模式物品栏
        RobotPlayerCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
        //注册数据
        RobotPlayerAttachments.register(modEventBus);
        // 注册网络包
        modEventBus.addListener(this::registerPayloadHandlers);
    }

    private void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MOD_ID);
        registrar.playToClient(
                EnergySyncPayload.TYPE,
                EnergySyncPayload.STREAM_CODEC,
                EnergySyncHandler::handleData
        );
        registrar.playToClient(
                FuelSyncPayload.TYPE,
                FuelSyncPayload.STREAM_CODEC,
                FuelSyncHandler::handleData
        );
        // 核心组件同步
        registrar.playToClient(
                CoreDataSyncPayload.TYPE,
                CoreDataSyncPayload.STREAM_CODEC,
                CoreDataSyncHandler::handleData
        );
        // 扩展组件同步
        registrar.playToClient(
                ExpansionSyncPayload.TYPE,
                ExpansionSyncPayload.STREAM_CODEC,
                ExpansionSyncHandler::handleData
        );
    }
}

