package de.srendi.advancedperipherals.common.container;

import dan200.computercraft.client.gui.widgets.ComputerSidebar;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.inventory.ContainerComputerBase;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import de.srendi.advancedperipherals.common.setup.APContainerTypes;
import de.srendi.advancedperipherals.common.smartglasses.SlotType;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SmartGlassesContainer extends ContainerComputerBase {
    public static final int BORDER = 8;
    public static final int PLAYER_START_Y = 134;
    public static final int PLAYER_START_X = ComputerSidebar.WIDTH + BORDER;

    public SmartGlassesContainer(int id, Predicate<Player> canUse, ServerComputer computer, Inventory playerInventory, IItemHandler inventory, ComputerContainerData data) {
        super(APContainerTypes.SMART_GLASSES_CONTAINER.get(), id, canUse, ComputerFamily.ADVANCED, computer, data);

        /**
         * Do player inventory before peripheral slots then quick move won't mixup
         */

        // Player hotbar
        for (var x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, PLAYER_START_X + x * 18, PLAYER_START_Y + 3 * 18 + 5));
        }

        // Player inventory
        for (var y = 0; y < 3; y++) {
            for (var x = 0; x < 9; x++) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, PLAYER_START_X + x * 18, PLAYER_START_Y + 1 + y * 18));
            }
        }

        // Glasses Peripherals
        addSlot(new SmartGlassesSlot(inventory, 0, 222, 148, SlotType.PERIPHERALS));
        addSlot(new SmartGlassesSlot(inventory, 1, 204, 166, SlotType.PERIPHERALS));
        addSlot(new SmartGlassesSlot(inventory, 2, 222, 166, SlotType.PERIPHERALS));
        addSlot(new SmartGlassesSlot(inventory, 3, 240, 166, SlotType.PERIPHERALS));
        addSlot(new SmartGlassesSlot(inventory, 4, 222, 184, SlotType.PERIPHERALS));

        // Glasses Modules
        addSlot(new SmartGlassesSlot(inventory, 5, 222, 148, SlotType.MODULES));
        addSlot(new SmartGlassesSlot(inventory, 6, 204, 166, SlotType.MODULES));
        addSlot(new SmartGlassesSlot(inventory, 7, 222, 166, SlotType.MODULES));
        addSlot(new SmartGlassesSlot(inventory, 8, 240, 166, SlotType.MODULES));
        addSlot(new SmartGlassesSlot(inventory, 9, 222, 184, SlotType.MODULES));
        addSlot(new SmartGlassesSlot(inventory, 10, 240, 184, SlotType.MODULES));
    }

    public SmartGlassesContainer(int id, Predicate<Player> predicate, ServerComputer computer, ComputerContainerData data, Inventory player, ItemStack glasses) {
        this(id, predicate, computer, player, glasses.getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(NullPointerException::new), data);
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
            } else {
                if (!this.moveItemStackTo(itemstack1, 36, 36 + 11, true)) {
                    return ItemStack.EMPTY;
                }
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
