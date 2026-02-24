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
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KeyboardContainer extends BaseContainer implements ComputerMenu {

    @Nullable
    private final ServerInputState<KeyboardContainer> input;
    private final ItemStack keyboardItem;
    @Nullable
    private ServerComputer computer;

    public KeyboardContainer(int id, Inventory inventory, BlockPos pos, Level level, ItemStack keyboardItem) {
        this(id, inventory, pos, level, keyboardItem, null);
    }

    public KeyboardContainer(int id, Inventory inventory, BlockPos pos, Level level, ItemStack keyboardItem, ServerComputer computer) {
        super(APContainerTypes.KEYBOARD_CONTAINER.get(), id, inventory, pos, level);
        this.keyboardItem = keyboardItem;

        if (!(level instanceof final ServerLevel serverLevel)) {
            this.input = null;
            this.computer = null;
            return;
        }
        this.input = new ServerInputState<>(this, computer);
        this.computer = computer;
        if (computer != null) {
            return;
        }
        if (!keyboardItem.has(APDataComponents.BINDING_COMPUTER.get())) {
            return;
        }
        // Cannot use instance ID here since they will change after reload the block
        int computerId = keyboardItem.get(APDataComponents.BINDING_COMPUTER.get());
        for (ServerComputer computr : ServerContext.get(serverLevel.getServer()).registry().getComputers()) {
            if (computr.getID() == computerId) {
                this.computer = computr;
                break;
            }
        }
    }

    public ItemStack getKeyboardItem() {
        return this.keyboardItem;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player instanceof ServerPlayer) {
            computer.queueEvent("keyboard_closed");
        }
    }

    @Nullable
    @Override
    public ServerComputer getComputer() {
        return computer;
    }

    @Nullable
    @Override
    public ServerInputHandler getInput() {
        return input;
    }

    @Override
    public void updateTerminal(TerminalState state) {

    }
}
