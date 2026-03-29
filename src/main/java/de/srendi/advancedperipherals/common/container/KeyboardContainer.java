package de.srendi.advancedperipherals.common.container;

import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.menu.ComputerMenu;
import dan200.computercraft.shared.computer.menu.ServerInputHandler;
import dan200.computercraft.shared.computer.menu.ServerInputState;
import dan200.computercraft.shared.computer.terminal.TerminalState;
import de.srendi.advancedperipherals.common.container.base.BaseContainer;
import de.srendi.advancedperipherals.common.setup.APContainerTypes;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.keyboard.KeyboardModule;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KeyboardContainer extends BaseContainer implements ComputerMenu {

    @Nullable
    private final ServerInputState input;
    @Nullable
    private final ServerComputer computer;
    @Nullable
    private final Runnable closeCallback;

    public KeyboardContainer(int id, Inventory inventory, Level level) {
        super(APContainerTypes.KEYBOARD_CONTAINER.get(), id, inventory, null, level);
        this.input = null;
        this.computer = null;
        this.closeCallback = null;
    }

    private KeyboardContainer(int id, Inventory inventory, Level level, @NotNull ServerComputer computer, Runnable closeCallback) {
        super(APContainerTypes.KEYBOARD_CONTAINER.get(), id, inventory, null, level);

        this.computer = computer;
        this.closeCallback = closeCallback;
        this.input = new ServerInputState(this, computer);
    }

    public KeyboardContainer(int id, Inventory inventory, Level level, @NotNull ServerComputer computer) {
        this(id, inventory, level, computer, null);
    }

    public KeyboardContainer(int id, Inventory inventory, Level level, SmartGlassesSideAccess access, KeyboardModule module) {
        this(id, inventory, level, access.getComputer(), () -> module.onKeyboardClosed(access));
    }

    @Override
    public boolean stillValid(Player player) {
        return this.computer == null || this.computer.checkUsable(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (this.closeCallback != null) {
            this.closeCallback.run();
        }
    }

    @Override
    @Nullable
    public ServerComputer getComputer() {
        return this.computer;
    }

    @Override
    @Nullable
    public ServerInputHandler getInput() {
        return this.input;
    }

    @Override
    public void updateTerminal(TerminalState state) {
    }
}
