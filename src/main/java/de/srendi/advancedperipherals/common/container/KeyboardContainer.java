package de.srendi.advancedperipherals.common.container;

import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.menu.ComputerMenu;
import dan200.computercraft.shared.computer.menu.ServerInputHandler;
import dan200.computercraft.shared.computer.menu.ServerInputState;
import dan200.computercraft.shared.computer.terminal.TerminalState;
import de.srendi.advancedperipherals.common.container.base.BaseContainer;
import de.srendi.advancedperipherals.common.items.KeyboardItem;
import de.srendi.advancedperipherals.common.setup.APContainerTypes;
import de.srendi.advancedperipherals.common.util.NBTUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class KeyboardContainer extends BaseContainer implements ComputerMenu {

    private final ServerInputState<KeyboardContainer> input;
    private final BlockPos computerPos;
    @Nullable
    private ServerComputer computer = null;

    public KeyboardContainer(int id, Inventory inventory, BlockPos pos, Level level, ItemStack keyboardItem) {
        super(APContainerTypes.KEYBOARD_CONTAINER.get(), id, inventory, pos, level);
        this.input = new ServerInputState<>( this );
        this.computerPos = NBTUtil.blockPosFromNBT(keyboardItem.getOrCreateTag().getCompound(KeyboardItem.BIND_TAG));
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return true;
    }

    @Override
    public ServerComputer getComputer() {
        if (computer != null)
            return computer;

        for (ServerComputer computer : ServerContext.get(ServerLifecycleHooks.getCurrentServer()).registry().getComputers()) {
            if (computer.getPosition().equals(computerPos)) {
                this.computer = computer;
            }
        }

        return computer;
    }

    @Override
    public ServerInputHandler getInput() {
        return input;
    }

    @Override
    public void updateTerminal(TerminalState state) {

    }
}
