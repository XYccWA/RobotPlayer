package org.xyccwa.robot_player.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.xyccwa.robot_player.attachment.*;

@EventBusSubscriber
public class RobotCoreCommand {

    // 槽位名称列表（新增 battery）
    private static final String[] SLOT_NAMES = {"radar", "cpu", "thruster", "frame", "battery"};

    // 物品名称补全建议提供者
    private static final SuggestionProvider<CommandSourceStack> ITEM_SUGGESTION = (context, builder) -> {
        String slotArg = "";
        try {
            slotArg = StringArgumentType.getString(context, "slot");
        } catch (Exception e) {
            return SharedSuggestionProvider.suggestResource(BuiltInRegistries.ITEM.keySet(), builder);
        }

        int slot = getSlotIndex(slotArg);
        if (slot >= 0 && slot < RobotCoreData.TOTAL_SLOTS) {
            var tag = RobotCoreData.getTagForSlot(slot);
            if (tag != null) {
                BuiltInRegistries.ITEM.getTag(tag).ifPresent(holders -> {
                    for (var holder : holders) {
                        ResourceLocation id = holder.value().builtInRegistryHolder().key().location();
                        builder.suggest(id.toString());
                    }
                });
            }
        }

        SharedSuggestionProvider.suggestResource(BuiltInRegistries.ITEM.keySet(), builder);
        return builder.buildFuture();
    };

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("robotcore")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("show")
                        .executes(context -> {
                            Player player = context.getSource().getPlayerOrException();
                            RobotCoreData data = player.getData(RobotPlayerAttachments.ROBOT_CORE);

                            context.getSource().sendSuccess(
                                    () -> net.minecraft.network.chat.Component.literal(
                                            "§6=== 机器人核心组件 ===\n" +
                                                    "§e雷达: §f" + getItemDisplay(data.getRadar()) + "\n" +
                                                    "§e主控芯片: §f" + getItemDisplay(data.getCpu()) + "\n" +
                                                    "§e推进系统: §f" + getItemDisplay(data.getThruster()) + "\n" +
                                                    "§e结构框架: §f" + getItemDisplay(data.getFrame()) + "\n" +
                                                    "§e主电池组: §f" + getItemDisplay(data.getBattery())
                                    ),
                                    false
                            );
                            return 1;
                        })
                )
                .then(Commands.literal("set")
                        .then(Commands.argument("slot", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    for (String name : SLOT_NAMES) {
                                        builder.suggest(name);
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("item", StringArgumentType.greedyString())
                                        .suggests(ITEM_SUGGESTION)
                                        .executes(context -> {
                                            Player player = context.getSource().getPlayerOrException();
                                            String slotName = StringArgumentType.getString(context, "slot");
                                            String itemName = StringArgumentType.getString(context, "item");

                                            int slot = getSlotIndex(slotName);
                                            if (slot < 0 || slot >= RobotCoreData.TOTAL_SLOTS) {
                                                context.getSource().sendFailure(
                                                        net.minecraft.network.chat.Component.literal(
                                                                "§c无效的槽位名称！可用: radar, cpu, thruster, frame, battery"
                                                        )
                                                );
                                                return 0;
                                            }

                                            ResourceLocation itemId = ResourceLocation.tryParse(itemName);
                                            if (itemId == null || !BuiltInRegistries.ITEM.containsKey(itemId)) {
                                                context.getSource().sendFailure(
                                                        net.minecraft.network.chat.Component.literal("§c无效的物品名称！")
                                                );
                                                return 0;
                                            }

                                            Item item = BuiltInRegistries.ITEM.get(itemId);
                                            ItemStack stack = new ItemStack(item);
                                            RobotCoreData data = player.getData(RobotPlayerAttachments.ROBOT_CORE);

                                            boolean success = data.trySetItem(slot, stack);

                                            if (success) {
                                                context.getSource().sendSuccess(
                                                        () -> net.minecraft.network.chat.Component.literal(
                                                                "§a已将 §6" + stack.getHoverName().getString() + " §a放入 §e" + RobotCoreData.getSlotName(slot)
                                                        ),
                                                        false
                                                );
                                            } else {
                                                var requiredTag = RobotCoreData.getTagForSlot(slot);
                                                String tagInfo = requiredTag != null ? requiredTag.location().toString() : "未知";

                                                context.getSource().sendFailure(
                                                        net.minecraft.network.chat.Component.literal(
                                                                "§c" + stack.getHoverName().getString() + " §c不能放入 §e" + RobotCoreData.getSlotName(slot) + " §c槽位！\n" +
                                                                        "§7该槽位只能放入带有 §e" + tagInfo + " §7标签的物品\n" +
                                                                        "§7使用 §e/robotcore list " + RobotCoreData.getSlotEnglishName(slot) + " §7查看可用物品"
                                                        )
                                                );
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("clear")
                        .executes(context -> {
                            Player player = context.getSource().getPlayerOrException();
                            RobotCoreData data = player.getData(RobotPlayerAttachments.ROBOT_CORE);

                            for (int i = 0; i < RobotCoreData.TOTAL_SLOTS; i++) {
                                data.clearSlot(i);
                            }

                            context.getSource().sendSuccess(
                                    () -> net.minecraft.network.chat.Component.literal("§a已清空所有核心组件"),
                                    false
                            );
                            return 1;
                        })
                )
                .then(Commands.literal("list")
                        .then(Commands.argument("slot", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    for (String name : SLOT_NAMES) {
                                        builder.suggest(name);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String slotName = StringArgumentType.getString(context, "slot");
                                    int slot = getSlotIndex(slotName);

                                    if (slot < 0 || slot >= RobotCoreData.TOTAL_SLOTS) {
                                        context.getSource().sendFailure(
                                                net.minecraft.network.chat.Component.literal("§c无效的槽位名称！")
                                        );
                                        return 0;
                                    }

                                    var tag = RobotCoreData.getTagForSlot(slot);
                                    if (tag == null) {
                                        context.getSource().sendFailure(
                                                net.minecraft.network.chat.Component.literal("§c该槽位没有配置标签！")
                                        );
                                        return 0;
                                    }

                                    StringBuilder sb = new StringBuilder();
                                    sb.append("§6=== ").append(RobotCoreData.getSlotName(slot)).append(" 可用物品 ===\n");

                                    var items = BuiltInRegistries.ITEM.getTag(tag);
                                    if (items.isEmpty()) {
                                        sb.append("§7暂无物品，请在数据包中添加标签: ").append(tag.location());
                                    } else {
                                        int count = 0;
                                        for (var holder : items.get()) {
                                            if (count > 0) sb.append("§7, ");
                                            sb.append("§f").append(holder.value().getDescription().getString());
                                            count++;
                                            if (count >= 20) {
                                                sb.append("§7... 等共 ").append(count).append(" 个");
                                                break;
                                            }
                                        }
                                    }

                                    context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal(sb.toString()), false);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("help")
                        .executes(context -> {
                            context.getSource().sendSuccess(
                                    () -> net.minecraft.network.chat.Component.literal(
                                            "§6=== 机器人核心命令帮助 ===\n" +
                                                    "§e/robotcore show §f- 查看当前安装的组件\n" +
                                                    "§e/robotcore set <槽位> <物品> §f- 设置指定槽位的组件\n" +
                                                    "§e/robotcore clear §f- 清空所有组件\n" +
                                                    "§e/robotcore list <槽位> §f- 查看该槽位可用的物品列表\n" +
                                                    "§e/robotcore help §f- 显示此帮助\n" +
                                                    "§6=== 扩展组件命令 ===\n" +
                                                    "§e/robotcore exp list §f- 查看扩展组件区\n" +
                                                    "§e/robotcore exp addSlot §f- 添加一个新扩展槽位\n" +
                                                    "§e/robotcore exp removeSlot <槽位> §f- 移除指定扩展槽位\n" +
                                                    "§e/robotcore exp setSize <数量> §f- 设置扩展槽位数量\n" +
                                                    "§e/robotcore exp set <槽位> <物品> §f- 设置扩展槽位物品\n" +
                                                    "§6=== 电量命令 ===\n" +
                                                    "§e/robotcore energy show §f- 查看当前电量\n" +
                                                    "§e/robotcore energy add <数量> §f- 增加电量\n" +
                                                    "§e/robotcore energy consume <数量> §f- 消耗电量\n" +
                                                    "§e/robotcore energy set <数量> §f- 设置电量\n"
                                    ),
                                    false
                            );
                            return 1;
                        })
                )
                // 添加扩展槽相关命令
                .then(Commands.literal("exp")
                        .then(Commands.literal("list")
                                .executes(context -> {
                                    Player player = context.getSource().getPlayerOrException();
                                    DynamicItemStack expansions = player.getData(RobotPlayerAttachments.ROBOT_EXPANSIONS);

                                    StringBuilder sb = new StringBuilder();
                                    sb.append("§6=== 扩展组件区 ===\n");
                                    sb.append("§7槽位数量: §e").append(expansions.getSlots()).append("\n");

                                    for (int i = 0; i < expansions.getSlots(); i++) {
                                        ItemStack stack = expansions.getStackInSlot(i);
                                        String itemName = stack.isEmpty() ? "§7[空]" : stack.getHoverName().getString();
                                        sb.append("§e[").append(i).append("] §f").append(itemName).append("\n");
                                    }

                                    context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal(sb.toString()), false);
                                    return 1;
                                })
                        )
                        .then(Commands.literal("addSlot")
                                .executes(context -> {
                                    Player player = context.getSource().getPlayerOrException();
                                    DynamicItemStack expansions = player.getData(RobotPlayerAttachments.ROBOT_EXPANSIONS);

                                    int newSlot = expansions.addSlot();
                                    context.getSource().sendSuccess(
                                            () -> net.minecraft.network.chat.Component.literal(
                                                    "§a已添加扩展槽位，当前共 §e" + expansions.getSlots() + " §a个槽位"
                                            ),
                                            false
                                    );
                                    return 1;
                                })
                        )
                        .then(Commands.literal("removeSlot")
                                .then(Commands.argument("slot", IntegerArgumentType.integer(0, 100))
                                        .executes(context -> {
                                            Player player = context.getSource().getPlayerOrException();
                                            int slot = IntegerArgumentType.getInteger(context, "slot");
                                            DynamicItemStack expansions = player.getData(RobotPlayerAttachments.ROBOT_EXPANSIONS);

                                            if (slot >= expansions.getSlots()) {
                                                context.getSource().sendFailure(
                                                        net.minecraft.network.chat.Component.literal("§c槽位索引超出范围！当前最大槽位: " + (expansions.getSlots() - 1))
                                                );
                                                return 0;
                                            }

                                            ItemStack removed = expansions.removeSlot(slot);
                                            context.getSource().sendSuccess(
                                                    () -> net.minecraft.network.chat.Component.literal(
                                                            "§a已移除槽位 " + slot + (removed.isEmpty() ? "" : "，丢掉了其中的物品")
                                                    ),
                                                    false
                                            );
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("setSize")
                                .then(Commands.argument("size", IntegerArgumentType.integer(0, 54))
                                        .executes(context -> {
                                            Player player = context.getSource().getPlayerOrException();
                                            int newSize = IntegerArgumentType.getInteger(context, "size");
                                            DynamicItemStack expansions = player.getData(RobotPlayerAttachments.ROBOT_EXPANSIONS);

                                            int oldSize = expansions.getSlots();
                                            expansions.setSlots(newSize);

                                            context.getSource().sendSuccess(
                                                    () -> net.minecraft.network.chat.Component.literal(
                                                            "§a已将扩展槽位从 §e" + oldSize + " §a调整为 §e" + expansions.getSlots()
                                                    ),
                                                    false
                                            );
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("set")
                                .then(Commands.argument("slot", IntegerArgumentType.integer(0, 100))
                                        .then(Commands.argument("item", StringArgumentType.greedyString())
                                                .suggests(ITEM_SUGGESTION)
                                                .executes(context -> {
                                                    Player player = context.getSource().getPlayerOrException();
                                                    int slot = IntegerArgumentType.getInteger(context, "slot");
                                                    String itemName = StringArgumentType.getString(context, "item");

                                                    DynamicItemStack expansions = player.getData(RobotPlayerAttachments.ROBOT_EXPANSIONS);

                                                    if (slot >= expansions.getSlots()) {
                                                        context.getSource().sendFailure(
                                                                net.minecraft.network.chat.Component.literal("§c槽位索引超出范围！当前最大槽位: " + (expansions.getSlots() - 1))
                                                        );
                                                        return 0;
                                                    }

                                                    ResourceLocation itemId = ResourceLocation.tryParse(itemName);
                                                    if (itemId == null || !BuiltInRegistries.ITEM.containsKey(itemId)) {
                                                        context.getSource().sendFailure(
                                                                net.minecraft.network.chat.Component.literal("§c无效的物品名称！")
                                                        );
                                                        return 0;
                                                    }

                                                    ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(itemId));
                                                    expansions.setStackInSlot(slot, stack);

                                                    context.getSource().sendSuccess(
                                                            () -> net.minecraft.network.chat.Component.literal(
                                                                    "§a已将 §6" + stack.getHoverName().getString() + " §a放入扩展槽位 §e" + slot
                                                            ),
                                                            false
                                                    );
                                                    return 1;
                                                })
                                        )
                                )
                        )
                )
                // 在 registerCommands 方法中添加
                .then(Commands.literal("energy")
                        .then(Commands.literal("show")
                                .executes(context -> {
                                    Player player = context.getSource().getPlayerOrException();
                                    RobotEnergyData energy = player.getData(RobotPlayerAttachments.ROBOT_ENERGY);

                                    context.getSource().sendSuccess(
                                            () -> net.minecraft.network.chat.Component.literal(
                                                    "§6=== 机器人电量 ===\n" +
                                                            "§e当前电量: §f" + energy.getEnergy() + " §7/ §e" + energy.getMaxEnergy() + "\n" +
                                                            "§e电量比例: §f" + String.format("%.1f", energy.getEnergyPercent() * 100) + "%"
                                            ),
                                            false
                                    );
                                    return 1;
                                })
                        )
                        .then(Commands.literal("add")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 10000))
                                        .executes(context -> {
                                            Player player = context.getSource().getPlayerOrException();
                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            RobotEnergyData energy = player.getData(RobotPlayerAttachments.ROBOT_ENERGY);

                                            int added = energy.addEnergy(amount);
                                            context.getSource().sendSuccess(
                                                    () -> net.minecraft.network.chat.Component.literal(
                                                            "§a已添加 " + added + " 电量，当前电量: " + energy.getEnergy() + "/" + energy.getMaxEnergy()
                                                    ),
                                                    false
                                            );
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("consume")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 10000))
                                        .executes(context -> {
                                            Player player = context.getSource().getPlayerOrException();
                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            RobotEnergyData energy = player.getData(RobotPlayerAttachments.ROBOT_ENERGY);

                                            boolean success = energy.consumeEnergy(amount);
                                            context.getSource().sendSuccess(
                                                    () -> net.minecraft.network.chat.Component.literal(
                                                            success ? "§a消耗 " + amount + " 电量成功" : "§c电量不足！"
                                                    ),
                                                    false
                                            );
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, 10000))
                                        .executes(context -> {
                                            Player player = context.getSource().getPlayerOrException();
                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            RobotEnergyData energy = player.getData(RobotPlayerAttachments.ROBOT_ENERGY);

                                            energy.setEnergy(amount);
                                            context.getSource().sendSuccess(
                                                    () -> net.minecraft.network.chat.Component.literal(
                                                            "§a已设置电量为: " + energy.getEnergy() + "/" + energy.getMaxEnergy()
                                                    ),
                                                    false
                                            );
                                            return 1;
                                        })
                                )
                        )
                )
                .then((Commands.literal("fuel")
                        .requires(source -> source.hasPermission(2))  // 需要 OP 权限
                        // ===== 显示燃料信息 =====
                        .then(Commands.literal("show")
                                .executes(context -> {
                                    Player player = context.getSource().getPlayerOrException();
                                    RobotFuelData fuel = player.getData(RobotPlayerAttachments.ROBOT_FUEL);

                                    context.getSource().sendSuccess(
                                            () -> net.minecraft.network.chat.Component.literal(
                                                    "§6=== 机器人燃料信息 ===\n" +
                                                            "§e当前燃料: §f" + fuel.getFuel() + " §7/ §e" + fuel.getMaxFuel() + "\n" +
                                                            "§e燃料比例: §f" + String.format("%.1f", fuel.getFuelPercent() * 100) + "%\n" +
                                                            "§e状态: " + (fuel.isEmpty() ? "§c已耗尽" : fuel.isFull() ? "§a已满" : "§e正常")
                                            ),
                                            false
                                    );
                                    return 1;
                                })
                        )
                        // ===== 添加燃料 =====
                        .then(Commands.literal("add")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 10000))
                                        .executes(context -> {
                                            Player player = context.getSource().getPlayerOrException();
                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            RobotFuelData fuel = player.getData(RobotPlayerAttachments.ROBOT_FUEL);

                                            float added = fuel.addFuel(amount);
                                            context.getSource().sendSuccess(
                                                    () -> net.minecraft.network.chat.Component.literal(
                                                            "§a已添加 " + added + " 燃料，当前燃料: " + fuel.getFuel() + "/" + fuel.getMaxFuel()
                                                    ),
                                                    false
                                            );
                                            return 1;
                                        })
                                )
                        )
                        // ===== 消耗燃料 =====
                        .then(Commands.literal("consume")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 10000))
                                        .executes(context -> {
                                            Player player = context.getSource().getPlayerOrException();
                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            RobotFuelData fuel = player.getData(RobotPlayerAttachments.ROBOT_FUEL);

                                            boolean success = fuel.consumeFuel(amount);
                                            context.getSource().sendSuccess(
                                                    () -> net.minecraft.network.chat.Component.literal(
                                                            success ? "§a消耗 " + amount + " 燃料成功，剩余: " + fuel.getFuel() + "/" + fuel.getMaxFuel()
                                                                    : "§c燃料不足！当前燃料: " + fuel.getFuel() + "/" + fuel.getMaxFuel()
                                                    ),
                                                    false
                                            );
                                            return 1;
                                        })
                                )
                        )
                        // ===== 设置燃料 =====
                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, 10000))
                                        .executes(context -> {
                                            Player player = context.getSource().getPlayerOrException();
                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            RobotFuelData fuel = player.getData(RobotPlayerAttachments.ROBOT_FUEL);

                                            fuel.setFuel(amount);
                                            context.getSource().sendSuccess(
                                                    () -> net.minecraft.network.chat.Component.literal(
                                                            "§a已设置燃料为: " + fuel.getFuel() + "/" + fuel.getMaxFuel()
                                                    ),
                                                    false
                                            );
                                            return 1;
                                        })
                                )
                        )
                        // ===== 加满燃料 =====
                        .then(Commands.literal("fill")
                                .executes(context -> {
                                    Player player = context.getSource().getPlayerOrException();
                                    RobotFuelData fuel = player.getData(RobotPlayerAttachments.ROBOT_FUEL);

                                    fuel.fullyFill();
                                    context.getSource().sendSuccess(
                                            () -> net.minecraft.network.chat.Component.literal(
                                                    "§a燃料已加满！当前燃料: " + fuel.getFuel() + "/" + fuel.getMaxFuel()
                                            ),
                                            false
                                    );
                                    return 1;
                                })
                        )
                        .then(Commands.literal("help")
                                .executes(context -> {
                                    context.getSource().sendSuccess(
                                            () -> net.minecraft.network.chat.Component.literal(
                                                    "§6=== 燃料命令帮助 ===\n" +
                                                            "§e/fuel show §f- 查看当前燃料信息\n" +
                                                            "§e/fuel add <数量> §f- 添加燃料\n" +
                                                            "§e/fuel consume <数量> §f- 消耗燃料\n" +
                                                            "§e/fuel set <数量> §f- 设置燃料量\n" +
                                                            "§e/fuel fill §f- 加满燃料\n" +
                                                            "§e/fuel help §f- 显示此帮助"
                                            ),
                                            false
                                    );
                                    return 1;
                                })
                        )
                )
            )
        );
    }

    private static int getSlotIndex(String slotName) {
        return switch (slotName.toLowerCase()) {
            case "radar" -> RobotCoreData.SLOT_RADAR;
            case "cpu" -> RobotCoreData.SLOT_CPU;
            case "thruster" -> RobotCoreData.SLOT_THRUSTER;
            case "frame" -> RobotCoreData.SLOT_FRAME;
            case "battery" -> RobotCoreData.SLOT_BATTERY;
            default -> -1;
        };
    }

    private static String getItemDisplay(ItemStack stack) {
        if (stack.isEmpty()) {
            return "§7[空]";
        }
        return stack.getHoverName().getString();
    }
}