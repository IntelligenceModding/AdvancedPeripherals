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

    private final ServerInputState<KeyboardContainer> input;
    private final ItemStack keyboardItem;
    @Nullable
    private ServerComputer computer = null;

    public KeyboardContainer(int id, Inventory inventory, BlockPos pos, Level level, ItemStack keyboardItem) {
        super(APContainerTypes.KEYBOARD_CONTAINER.get(), id, inventory, pos, level);
        this.input = new ServerInputState<>(this);
        this.keyboardItem = keyboardItem;

        CompoundTag data = keyboardItem.getOrCreateTag();

        if (!data.getBoolean(KeyboardItem.BOUND_TYPE_TAG)) {
            // Cannot use instance ID here since they will change after reload the block
            int computerId = keyboardItem.getOrCreateTag().getInt(KeyboardItem.BIND_TAG);

            for (ServerComputer computer : ServerContext.get(ServerLifecycleHooks.getCurrentServer()).registry().getComputers()) {
                if (computer.getID() == computerId) {
                    this.computer = computer;
                    break;
                }
            }
        } else if (data.contains(KeyboardItem.GLASSES_BIND_TAG)) {
            computer = ServerContext.get(ServerLifecycleHooks.getCurrentServer()).registry().get(data.getInt(KeyboardItem.GLASSES_BIND_TAG));
        }

    }

    public ItemStack getKeyboardItem() {
        return this.keyboardItem;
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
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

    @Override
    public ServerInputHandler getInput() {
        return input;
    }

    @Override
    public void updateTerminal(TerminalState state) {

    }
}
