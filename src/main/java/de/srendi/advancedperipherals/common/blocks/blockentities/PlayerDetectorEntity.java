package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.PlayerDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class PlayerDetectorEntity extends PeripheralBlockEntity<PlayerDetectorPeripheral> {

    public PlayerDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.PLAYER_DETECTOR.get(), pos, state);
    }

    @NotNull
    @Override
    protected PlayerDetectorPeripheral createPeripheral() {
        return new PlayerDetectorPeripheral(this);
    }

}
