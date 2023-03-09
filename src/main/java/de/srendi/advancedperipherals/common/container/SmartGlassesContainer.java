package de.srendi.advancedperipherals.common.container;

import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import de.srendi.advancedperipherals.common.setup.APContainerTypes;
import de.srendi.advancedperipherals.common.setup.APItems;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SmartGlassesContainer extends AbstractComputerMenu {
    public static final int BORDER = 8;
    public static final int PLAYER_START_Y = 134;
    public static final int TURTLE_START_X = SIDEBAR_WIDTH + 175;
    public static final int PLAYER_START_X = SIDEBAR_WIDTH + BORDER;

    public SmartGlassesContainer(int id, Predicate<Player> canUse, ServerComputer computer, Inventory playerInventory, IItemHandler inventory, ComputerContainerData data) {
        super(APContainerTypes.SMART_GLASSES_CONTAINER.get(), id, canUse, ComputerFamily.ADVANCED, computer, data);
        // Turtle inventory
        for (var y = 0; y < 4; y++) {
            for (var x = 0; x < 4; x++) {
                addSlot(new SlotItemHandler(inventory, x + y * 4, TURTLE_START_X + 1 + x * 18, PLAYER_START_Y + 1 + y * 18));
            }
        }

        // Player inventory
        for (var y = 0; y < 3; y++) {
            for (var x = 0; x < 9; x++) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, PLAYER_START_X + x * 18, PLAYER_START_Y + 1 + y * 18));
            }
        }

        // Player hotbar
        for (var x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, PLAYER_START_X + x * 18, PLAYER_START_Y + 3 * 18 + 5));
        }
    }

    public SmartGlassesContainer(int id, Predicate<Player> predicate, ServerComputer computer, ComputerContainerData data, Inventory player) {
        this( id, predicate, computer, player, player.getSelected().getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get(), data);

        //getTerminal().resize(48, 48);
    }

    @NotNull
    @Override
    public ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index >= 36) {
                if (!this.moveItemStackTo(itemstack1, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index <= 35) {
                if (itemstack1.getItem().equals(APItems.MEMORY_CARD.get())) {
                    if (!this.moveItemStackTo(itemstack1, 36, 37, true)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
