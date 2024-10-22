package dan200.computercraft.mixin.gametest.client;

import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Used to suppress the "Worlds using Experimental Settings are not supported" warning
 * when loading a world in GameTest.
 */
@Mixin(LevelSummary.class)
public class LevelSummaryMixin {

    @Overwrite
    public boolean isExperimental() {
        return false;
    }

}
