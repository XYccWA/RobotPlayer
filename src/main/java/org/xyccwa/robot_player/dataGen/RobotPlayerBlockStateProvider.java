package org.xyccwa.robot_player.dataGen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.xyccwa.robot_player.RobotPlayer;

public class RobotPlayerBlockStateProvider extends BlockStateProvider {
    public RobotPlayerBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, RobotPlayer.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

    }
}
