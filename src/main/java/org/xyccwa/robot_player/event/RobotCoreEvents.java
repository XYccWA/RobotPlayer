package org.xyccwa.robot_player.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.xyccwa.robot_player.attachment.*;
import org.xyccwa.robot_player.modItem.RobotPlayerItem;
import org.xyccwa.robot_player.modItem.coreItem.BatteryComponentItem;
import org.xyccwa.robot_player.modItem.coreItem.FrameComponentItem;
import org.xyccwa.robot_player.modItem.coreItem.ThrusterComponentItem;
import org.xyccwa.robot_player.network.energy.EnergySyncPayload;
import org.xyccwa.robot_player.network.fuel.FuelSyncPayload;

@EventBusSubscriber
public class RobotCoreEvents {

    // 结构框架生命值修饰符 ID
    private static final ResourceLocation FRAME_HEALTH_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("space_simulation", "frame_health_bonus");

    // 保存上一次的核心数据
    private static RobotCoreData lastCoreData = null;

    // 用于标记是否已经给过初始组件的 NBT 键
    private static final String HAS_INITIAL_COMPONENTS = "RobotCoreHasInitialComponents";

    /**
     * 将电量数据同步到客户端
     */
    private static void syncEnergyToClient(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            RobotEnergyData energy = player.getData(RobotPlayerAttachments.ROBOT_ENERGY);

            // 发送数据包到该玩家
            PacketDistributor.sendToPlayer(
                    serverPlayer,
                    new EnergySyncPayload(energy.getEnergy(), energy.getMaxEnergy())
            );
        }
    }

    /**
     * 将燃料数据同步到客户端
     */
    private static void syncFuelToClient(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            RobotFuelData fuel = player.getData(RobotPlayerAttachments.ROBOT_FUEL);

            // 发送数据包到该玩家
            PacketDistributor.sendToPlayer(
                    serverPlayer,
                    new FuelSyncPayload(fuel.getFuel(), fuel.getMaxFuel())
            );
        }
    }

    /**
     * 玩家登录时，应用所有效果
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            applyFrameEffects(player);
            applyExpansionSlots(player);
            applyBatteryEffects(player);
            syncEnergyToClient(player);
            applyThrusterEffects(player);
            syncFuelToClient(player);
        }
    }

    /**
     * 玩家重生时，应用所有效果
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            applyFrameEffects(player);
            applyExpansionSlots(player);
            applyBatteryEffects(player);
            syncEnergyToClient(player);
            applyThrusterEffects(player);
            syncFuelToClient(player);
        }
    }

    /**
     * 玩家进入世界时（包括首次进入和每次登录），检查并初始化组件
     */
    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        // 检查是否已经初始化过
        boolean hasInitialized = player.getPersistentData().getBoolean(HAS_INITIAL_COMPONENTS);

        if (!hasInitialized) {
            // 首次进入，安装所有基础组件
            initializePlayerComponents(player);
            // 标记已初始化
            player.getPersistentData().putBoolean(HAS_INITIAL_COMPONENTS, true);

            RobotEnergyData energyData = player.getData(RobotPlayerAttachments.ROBOT_ENERGY);

            applyFrameEffects(player);
            applyExpansionSlots(player);
            applyBatteryEffects(player);
            syncEnergyToClient(player);
            applyThrusterEffects(player);
            syncFuelToClient(player);

            energyData.setEnergy((int) (energyData.getMaxEnergy() * (0.5 + Math.random() * 0.5)));
        }else{
            applyFrameEffects(player);
            applyExpansionSlots(player);
            applyBatteryEffects(player);
            syncEnergyToClient(player);
            applyThrusterEffects(player);
            syncFuelToClient(player);
        }
    }

    /**
     * 初始化玩家的所有核心组件为基础版本
     */
    private static void initializePlayerComponents(Player player) {
        RobotCoreData coreData = player.getData(RobotPlayerAttachments.ROBOT_CORE);

        // ----- 1. 安装基础核心组件 -----
        coreData.setItemInternal(RobotCoreData.SLOT_RADAR, new ItemStack(RobotPlayerItem.BASIC_RADAR.get()));
        coreData.setItemInternal(RobotCoreData.SLOT_CPU, new ItemStack(RobotPlayerItem.BASIC_CPU.get()));
        coreData.setItemInternal(RobotCoreData.SLOT_THRUSTER, new ItemStack(RobotPlayerItem.BASIC_THRUSTER.get()));
        coreData.setItemInternal(RobotCoreData.SLOT_FRAME, new ItemStack(RobotPlayerItem.BASIC_FRAME.get()));
        coreData.setItemInternal(RobotCoreData.SLOT_BATTERY, new ItemStack(RobotPlayerItem.BASIC_BATTERY.get()));

    }

    /**
     * 每 tick 检测核心组件是否变化
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        RobotEnergyData energy = player.getData(RobotPlayerAttachments.ROBOT_ENERGY);

        // 处理组件耗电（每秒执行一次）
        if (player.tickCount % 2 == 0) {
            handleEnergyConsumption(player, player.getData(RobotPlayerAttachments.ROBOT_CORE), energy);
            // 耗电后也同步
            syncEnergyToClient(player);
        }

        RobotCoreData coreData = player.getData(RobotPlayerAttachments.ROBOT_CORE);

        if (lastCoreData == null || !lastCoreData.matches(coreData)) {
            lastCoreData = coreData.copy();
            applyFrameEffects(player);
            applyExpansionSlots(player);
            applyBatteryEffects(player);
            syncEnergyToClient(player);
            applyThrusterEffects(player);
            syncFuelToClient(player);
        }
    }

    /**
     * 处理各组件每秒电量消耗
     */
    private static void handleEnergyConsumption(Player player, RobotCoreData coreData, RobotEnergyData energy) {
        float totalCost = 0f;

        // 雷达耗电
        var radarStack = coreData.getRadar();
        if (!radarStack.isEmpty() && radarStack.getItem() instanceof org.xyccwa.robot_player.modItem.coreItem.RadarComponentItem radar) {
            totalCost += radar.getEnergyCostPerSecond();
        }

        // 主控芯片耗电
        var cpuStack = coreData.getCpu();
        if (!cpuStack.isEmpty() && cpuStack.getItem() instanceof org.xyccwa.robot_player.modItem.coreItem.CpuComponentItem cpu) {
            totalCost += cpu.getEnergyCostPerSecond();
        }

        // 推进系统耗电
        var thrusterStack = coreData.getThruster();
        if (!thrusterStack.isEmpty() && thrusterStack.getItem() instanceof org.xyccwa.robot_player.modItem.coreItem.ThrusterComponentItem thruster) {
            totalCost += thruster.getEnergyCostPerSecond();
        }

        if (totalCost > 0) {
            int cost = (int) Math.ceil(totalCost);  // 向上取整
            boolean hasEnough = energy.consumeEnergy(cost);

            //电量不足直接死亡
            if (!hasEnough) {

            }
        }
    }

    /**
     * 应用结构框架的生命值加成
     */
    private static void applyFrameEffects(Player player) {
        RobotCoreData coreData = player.getData(RobotPlayerAttachments.ROBOT_CORE);
        ItemStack frameStack = coreData.getFrame();

        AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr == null) return;

        AttributeModifier oldModifier = healthAttr.getModifier(FRAME_HEALTH_MODIFIER_ID);
        if (oldModifier != null) {
            healthAttr.removeModifier(oldModifier);
        }

        if (!frameStack.isEmpty() && frameStack.getItem() instanceof FrameComponentItem frameItem) {
            int bonusHealth = frameItem.getBonusMaxHealth();
            if (bonusHealth > 0) {
                AttributeModifier modifier = new AttributeModifier(
                        FRAME_HEALTH_MODIFIER_ID,
                        bonusHealth,
                        AttributeModifier.Operation.ADD_VALUE
                );
                healthAttr.addPermanentModifier(modifier);
            }
        }

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    /**
     * 应用推进系统的燃料容量加成
     */
    private static void applyThrusterEffects(Player player) {
        RobotCoreData coreData = player.getData(RobotPlayerAttachments.ROBOT_CORE);
        ItemStack thrusterStack = coreData.getThruster();

        RobotFuelData fuel = player.getData(RobotPlayerAttachments.ROBOT_FUEL);

        int bonusFuelCapacity = 0;
        if (!thrusterStack.isEmpty() && thrusterStack.getItem() instanceof ThrusterComponentItem thrusterItem) {
            bonusFuelCapacity = thrusterItem.getFuelCapacity();
        }

        int baseMaxFuel = 0;
        int newMaxFuel = baseMaxFuel + bonusFuelCapacity;

        if (fuel.getMaxFuel() != newMaxFuel) {
            float oldPercent = fuel.getFuelPercent();
            fuel.setMaxFuel(newMaxFuel);
            fuel.setFuel((int)(newMaxFuel * oldPercent));
        }
    }

    /**
     * 应用结构框架的扩展槽位数量加成
     */
    private static void applyExpansionSlots(Player player) {
        RobotCoreData coreData = player.getData(RobotPlayerAttachments.ROBOT_CORE);
        ItemStack frameStack = coreData.getFrame();

        DynamicItemStack expansions = player.getData(RobotPlayerAttachments.ROBOT_EXPANSIONS);

        int baseSlots = 0;
        int bonusSlots = 0;

        if (!frameStack.isEmpty() && frameStack.getItem() instanceof FrameComponentItem frameItem) {
            bonusSlots = frameItem.getBonusExpansionSlots();
        }

        int totalSlots = baseSlots + bonusSlots;

        if (expansions.getSlots() != totalSlots) {
            expansions.setSlots(totalSlots);
        }
    }

    /**
     * 应用主电池组的电量加成
     */
    private static void applyBatteryEffects(Player player) {
        RobotCoreData coreData = player.getData(RobotPlayerAttachments.ROBOT_CORE);
        ItemStack batteryStack = coreData.getBattery();

        RobotEnergyData energy = player.getData(RobotPlayerAttachments.ROBOT_ENERGY);

        int bonusMaxEnergy = 0;
        if (!batteryStack.isEmpty() && batteryStack.getItem() instanceof BatteryComponentItem batteryItem) {
            bonusMaxEnergy = batteryItem.getBonusMaxEnergy();
        }

        // 默认基础电量为 50
        int baseMaxEnergy = 0;
        int newMaxEnergy = baseMaxEnergy + bonusMaxEnergy;

        // 只有最大电量变化时才更新
        if (energy.getMaxEnergy() != newMaxEnergy) {
            float oldPercent = energy.getEnergyPercent();
            energy.setMaxEnergy(newMaxEnergy);
            // 按比例恢复电量
            energy.setEnergy((int)(newMaxEnergy * oldPercent));
        }
    }


    /**
     * 玩家死亡时保留数据
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        Player original = event.getOriginal();
        Player player = event.getEntity();

        // 复制核心数据
        RobotCoreData oldData = original.getData(RobotPlayerAttachments.ROBOT_CORE);
        RobotCoreData newData = player.getData(RobotPlayerAttachments.ROBOT_CORE);
        for (int i = 0; i < RobotCoreData.TOTAL_SLOTS; i++) {
            newData.setItemInternal(i, oldData.getItem(i));
        }

        // 复制扩展数据
        DynamicItemStack oldExp = original.getData(RobotPlayerAttachments.ROBOT_EXPANSIONS);
        DynamicItemStack newExp = player.getData(RobotPlayerAttachments.ROBOT_EXPANSIONS);
        newExp.setSlots(oldExp.getSlots());
        for (int i = 0; i < oldExp.getSlots(); i++) {
            newExp.setStackInSlot(i, oldExp.getStackInSlot(i));
        }

        // 复制电量数据
        RobotEnergyData oldEnergy = original.getData(RobotPlayerAttachments.ROBOT_ENERGY);
        RobotEnergyData newEnergy = player.getData(RobotPlayerAttachments.ROBOT_ENERGY);
        newEnergy.setMaxEnergy(oldEnergy.getMaxEnergy());
        newEnergy.setEnergy(oldEnergy.getEnergy());
    }
}