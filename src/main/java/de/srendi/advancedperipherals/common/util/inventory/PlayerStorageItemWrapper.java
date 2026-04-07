package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.InventoryManagerPeripheral;
import de.srendi.advancedperipherals.common.util.EmptyLuaTable;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.PlayerInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Optional;

// TODO: fluid variant?
public class PlayerStorageItemWrapper {
    private final IComputerAccess computer;
    private final WeakReference<InventoryManagerPeripheral> peripheral;
    private final WeakReference<ServerPlayer> player;
    private final int slot;
    private final ItemStack stack;
    private final IItemHandler handler;

    protected PlayerStorageItemWrapper(IComputerAccess computer, InventoryManagerPeripheral peripheral, ServerPlayer player, int slot, ItemStack stack, IItemHandler handler) {
        this.computer = computer;
        this.peripheral = new WeakReference<>(peripheral);
        this.player = new WeakReference<>(player);
        this.slot = slot;
        this.stack = stack;
        this.handler = handler;
    }

    @Nullable
    public static PlayerStorageItemWrapper create(IComputerAccess computer, InventoryManagerPeripheral peripheral, @NotNull ServerPlayer player, int slot) {
        ItemStack stack = player.getInventory().getItem(slot);
        if (stack.isEmpty()) {
            return null;
        }
        IItemHandler handler = stack.getCapability(Capabilities.ItemHandler.ITEM);
        if (handler == null) {
            return null;
        }
        return new PlayerStorageItemWrapper(computer, peripheral, player, slot, stack, handler);
    }

    public boolean isValid() {
        ServerPlayer player = this.player.get();
        if (player == null || player.isRemoved()) {
            return false;
        }
        if (player.getInventory().getItem(this.slot) != this.stack) {
            return false;
        }
        InventoryManagerPeripheral peripheral = this.peripheral.get();
        if (peripheral == null || !peripheral.isAccessValid(this.computer)) {
            return false;
        }
        return peripheral.getOwnerPlayer() == player;
    }

    protected final void assertValid() throws LuaException {
        if (!this.isValid()) {
            throw new LuaException("Storage item outdate");
        }
    }

    @LuaFunction(value = "isValid", mainThread = true)
    public final boolean isValidLua() {
        return this.isValid();
    }

    @LuaFunction(mainThread = true)
    public final int size() throws LuaException {
        this.assertValid();
        return this.handler.getSlots();
    }

    @LuaFunction(mainThread = true)
    public final Map<Integer, Object> list() throws LuaException {
        this.assertValid();
        return InventoryUtil.list(this.handler);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult pushItems(String toName, Optional<Map<?, ?>> filterTable) throws LuaException {
        this.assertValid();

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(filterTable.orElse(null)));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }

        IItemHandler inventoryTo = this.getInventoryHandler(toName);

        return MethodResult.of(ItemUtil.moveItem(this.handler, inventoryTo, filter.left()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult pullItems(String fromName, Optional<Map<?, ?>> filterTable) throws LuaException {
        this.assertValid();

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(filterTable.orElse(null)));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }

        IItemHandler inventoryFrom = this.getInventoryHandler(fromName);

        return MethodResult.of(ItemUtil.moveItem(inventoryFrom, this.handler, filter.left()));
    }

    @NotNull
    private IItemHandler getInventoryHandler(String name) throws LuaException {
        if (name.equals(InventoryManagerPeripheral.PLAYER_INV_MAGIC_NAME)) {
            ServerPlayer player = this.player.get();
            if (player == null || player.isRemoved()) {
                throw new LuaException("Storage item outdate");
            }
            return new PlayerInvWrapper(player.getInventory());
        }
        IPeripheral toPeripheral = this.computer.getAvailablePeripheral(name);
        if (toPeripheral == null) {
            throw new LuaException("Target '" + name + "' does not exist");
        }
        IItemHandler inventory = ItemUtil.extractHandler(toPeripheral);
        if (inventory == null) {
            throw new LuaException("Target '" + name + "' is not an inventory");
        }
        return inventory;
    }
}
