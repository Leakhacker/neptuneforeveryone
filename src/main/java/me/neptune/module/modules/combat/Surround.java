package me.neptune.module.modules.combat;

import me.neptune.module.Module;
import me.neptune.settings.BooleanSetting;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class Surround extends Module {
    private final BooleanSetting rotate = new BooleanSetting("Rotate", "surround_rotate",true);
    private final BooleanSetting airplace = new BooleanSetting("Airplace", "surround_airplace", true);

    public Surround() {
        super("Surround", Category.Combat);

        this.addSetting(rotate);
    }
    private final List<BlockPos> placePositions = new ArrayList<>();

    @Override
    public void onUpdate() {

    }
}
