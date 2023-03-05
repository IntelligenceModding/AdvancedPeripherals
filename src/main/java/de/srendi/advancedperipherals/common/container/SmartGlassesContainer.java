package de.srendi.advancedperipherals.common.container;

import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import de.srendi.advancedperipherals.common.setup.APContainerTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SmartGlassesContainer extends AbstractComputerMenu {

    public SmartGlassesContainer(int id, Predicate<Player> canUse,
                                 ServerComputer computer
    ) {
        super(APContainerTypes.SMART_GLASSES_CONTAINER.get(), id, canUse, ComputerFamily.ADVANCED, computer, null);
    }

    public SmartGlassesContainer(int id, Predicate<Player> predicate, ServerComputer computer, ComputerContainerData data) {
        super(APContainerTypes.SMART_GLASSES_CONTAINER.get(), id, predicate, ComputerFamily.ADVANCED, computer, data);
        getTerminal().resize(48, 48);
    }

    @Override
    public ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
