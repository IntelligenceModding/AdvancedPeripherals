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
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
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

        if (level.isClientSide) {
            this.input = null;
            this.computer = null;
            return;
        }
        this.input = new ServerInputState<>(this);
        this.computer = computer;
        if (computer != null) {
            return;
        }
        CompoundTag data = keyboardItem.getOrCreateTag();
        if (!data.contains(KeyboardItem.BIND_TAG)) {
            return;
        }
        // Cannot use instance ID here since they will change after reload the block
        int computerId = data.getInt(KeyboardItem.BIND_TAG);
        for (ServerComputer computr : ServerContext.get(ServerLifecycleHooks.getCurrentServer()).registry().getComputers()) {
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
