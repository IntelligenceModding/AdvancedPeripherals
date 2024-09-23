package de.srendi.advancedperipherals.common.container;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.container.base.BaseContainer;
import de.srendi.advancedperipherals.common.setup.APContainerTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class KeyboardContainer extends BaseContainer {

    public KeyboardContainer(int id, Inventory inventory, BlockPos pos, Level level) {
        super(APContainerTypes.KEYBOARD_CONTAINER.get(), id, inventory, pos, level);
        AdvancedPeripherals.debug("test");
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return true;
    }

}
