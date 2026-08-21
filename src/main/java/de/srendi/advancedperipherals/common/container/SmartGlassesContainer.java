package de.srendi.advancedperipherals.common.container;

import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import de.srendi.advancedperipherals.common.setup.APContainerTypes;
import de.srendi.advancedperipherals.common.smartglasses.SlotType;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSlot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SmartGlassesContainer extends AbstractComputerMenu {
    public static final int BORDER = 8;
    public static final int PLAYER_START_Y = 134;
    public static final int PLAYER_START_X = AbstractComputerMenu.SIDEBAR_WIDTH + BORDER;

    protected SmartGlassesContainer(int id, Predicate<Player> canUse, SmartGlassesComputer computer, Inventory inventory, IItemHandlerModifiable handler, ComputerContainerData data) {
        super(APContainerTypes.SMART_GLASSES_CONTAINER.get(), id, canUse, ComputerFamily.ADVANCED, computer, data);

        /*
         * Do player inventory before peripheral slots then quick move won't mixup
         */

        // Player hotbar
        for (var x = 0; x < 9; x++) {
            addSlot(new Slot(inventory, x, PLAYER_START_X + x * 18, PLAYER_START_Y + 3 * 18 + 5));
        }

        // Player inventory
        for (var y = 0; y < 3; y++) {
            for (var x = 0; x < 9; x++) {
                addSlot(new Slot(inventory, x + y * 9 + 9, PLAYER_START_X + x * 18, PLAYER_START_Y + 1 + y * 18));
            }
        }

        // Glasses Peripherals
        addSlot(new SmartGlassesSlot(handler, 0, 222, 148, SlotType.PERIPHERALS));
        addSlot(new SmartGlassesSlot(handler, 1, 204, 166, SlotType.PERIPHERALS));
        addSlot(new SmartGlassesSlot(handler, 2, 222, 166, SlotType.PERIPHERALS));
        addSlot(new SmartGlassesSlot(handler, 3, 240, 166, SlotType.PERIPHERALS));
        addSlot(new SmartGlassesSlot(handler, 4, 222, 184, SlotType.PERIPHERALS));

        // Glasses Modules
        addSlot(new SmartGlassesSlot(handler, 5, 222, 148, SlotType.MODULES));
        addSlot(new SmartGlassesSlot(handler, 6, 204, 166, SlotType.MODULES));
        addSlot(new SmartGlassesSlot(handler, 7, 222, 166, SlotType.MODULES));
        addSlot(new SmartGlassesSlot(handler, 8, 240, 166, SlotType.MODULES));
        addSlot(new SmartGlassesSlot(handler, 9, 222, 184, SlotType.MODULES));
        addSlot(new SmartGlassesSlot(handler, 10, 240, 184, SlotType.MODULES));

        // Player inventory
        for (var y = 0; y < 3; y++) {
            for (var x = 0; x < 9; x++) {
                addSlot(new Slot(inventory, x + y * 9 + 9, PLAYER_START_X + x * 18, PLAYER_START_Y + 1 + y * 18));
            }
        }

        // Player hotbar
        for (var x = 0; x < 9; x++) {
            addSlot(new Slot(inventory, x, PLAYER_START_X + x * 18, PLAYER_START_Y + 3 * 18 + 5));
        }
    }

    public SmartGlassesContainer(int id, Predicate<Player> canUse, SmartGlassesComputer computer, Inventory inventory, IItemHandlerModifiable handler) {
        this(id, canUse, computer, inventory, handler, null);
    }

    public SmartGlassesContainer(int id, Predicate<Player> canUse, ComputerContainerData data, Inventory inventory) {
        this(
            id,
            canUse,
            null,
            inventory,
            new InvWrapper(new SimpleContainer(SmartGlassesSlot.SLOTS)),
            data
        );
    }

    @Override
    @NotNull
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack srcStack = slot.getItem();
        ItemStack left = srcStack.copy();
        ItemStack wasMoving = srcStack.copy();

        if (index >= Inventory.INVENTORY_SIZE) {
            this.moveItemStackTo(left, 0, Inventory.INVENTORY_SIZE, false);
        } else {
            this.moveItemStackTo(left, Inventory.INVENTORY_SIZE, Inventory.INVENTORY_SIZE + 11, false);
        }

        if (ItemStack.isSameItemSameComponents(wasMoving, left)) {
            return ItemStack.EMPTY;
        }

        slot.setByPlayer(left.isEmpty() ? ItemStack.EMPTY : left);
        slot.setChanged();

        return wasMoving;
    }
}
