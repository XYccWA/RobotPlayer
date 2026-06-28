package org.xyccwa.robot_player.dataGen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.xyccwa.robot_player.RobotPlayer;

public class RobotPlayerItemModelsProvider extends ItemModelProvider {
    public RobotPlayerItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, RobotPlayer.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

    }
}
