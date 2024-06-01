package de.srendi.advancedperipherals.test.mixin;

import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.block.BlockDirection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Used to fix the orientation of RS Blocks when they are loaded from a structure block.
 */
@Mixin(BaseBlock.class)
public class RsBaseBlockMixin {

    @Shadow
    public BlockDirection getDirection() {
        return null;
    }

    @Overwrite
    public BlockState rotate(BlockState state, Rotation rot) {
        BlockDirection dir = this.getDirection();
        if (dir == BlockDirection.NONE) return state;

        Direction newDirection = switch (rot) {
            case NONE -> state.getValue(dir.getProperty());
            case CLOCKWISE_90 -> dir.cycle(state.getValue(dir.getProperty()).getClockWise().getClockWise());
            case CLOCKWISE_180 -> dir.cycle(state.getValue(dir.getProperty()).getClockWise().getClockWise().getClockWise());
            case COUNTERCLOCKWISE_90 -> dir.cycle(state.getValue(dir.getProperty()));
        };

        return state.setValue(dir.getProperty(), newDirection);
    }

}
