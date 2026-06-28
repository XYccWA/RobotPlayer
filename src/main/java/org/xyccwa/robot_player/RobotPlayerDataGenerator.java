package org.xyccwa.robot_player;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.xyccwa.robot_player.dataGen.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = RobotPlayer.MOD_ID)
public class RobotPlayerDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator =event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new RobotPlayerRecipesProvider(packOutput, lookupProvider));

        BlockTagsProvider blockTagsProvider = new RobotPlayerBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(),new RobotPlayerItemTagsProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(),existingFileHelper));

        generator.addProvider(event.includeClient(), new RobotPlayerItemModelsProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new RobotPlayerBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new RobotPlayerEnUsLangProvider(packOutput));
        generator.addProvider(event.includeClient(), new RobotPlayerZhCnLangProvider(packOutput));

        generator.addProvider(event.includeServer(),new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(RobotPlayerBlockLootTableProvider::new, LootContextParamSets.BLOCK)),lookupProvider));

    }
}
