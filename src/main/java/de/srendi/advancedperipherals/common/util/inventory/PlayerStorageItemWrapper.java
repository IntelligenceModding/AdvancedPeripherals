package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.InventoryManagerPeripheral;
import de.srendi.advancedperipherals.common.util.EmptyLuaTable;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.PlayerInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PlayerStorageItemWrapper {
    private final IComputerAccess computer;
    private final InventoryManagerPeripheral peripheral;
    private final WeakReference<ServerPlayer> player;
    private final Function<ServerPlayer, ItemStack> itemProvider;
    private final BiConsumer<ServerPlayer, ItemStack> itemUpdater;
    private ItemStack stack;
    private final IItemHandler itemHandler;
    private final IFluidHandler fluidHandler;

    protected PlayerStorageItemWrapper(
        IComputerAccess computer,
        InventoryManagerPeripheral peripheral,
        ServerPlayer player,
        Function<ServerPlayer, ItemStack> itemProvider,
        BiConsumer<ServerPlayer, ItemStack> itemUpdater,
        ItemStack stack,
        @Nullable IItemHandler itemHandler,
        @Nullable IFluidHandler fluidHandler
    ) {
        this.computer = computer;
        this.peripheral = peripheral;
        this.player = new WeakReference<>(player);
        this.itemProvider = itemProvider;
        this.itemUpdater = itemUpdater;
        this.stack = stack;
        this.itemHandler = itemHandler;
        this.fluidHandler = fluidHandler;
    }

    @Nullable
    public static PlayerStorageItemWrapper create(IComputerAccess computer, InventoryManagerPeripheral peripheral, @NotNull ServerPlayer player, int slot) {
        return create(computer, peripheral, player, (p) -> {
            Inventory inventory = p.getInventory();
            if (slot < 0 || slot >= inventory.getContainerSize()) {
                return ItemStack.EMPTY;
            }
            return inventory.getItem(slot);
        }, (p, stack) -> {
            Inventory inventory = p.getInventory();
            inventory.setItem(slot, stack);
            inventory.setChanged();
        });
    }

    @Nullable
    public static PlayerStorageItemWrapper create(
        IComputerAccess computer,
        InventoryManagerPeripheral peripheral,
        @NotNull ServerPlayer player,
        Function<ServerPlayer, ItemStack> itemProvider,
        BiConsumer<ServerPlayer, ItemStack> itemUpdater
    ) {
        ItemStack stack = itemProvider.apply(player);
        if (stack.isEmpty()) {
            return null;
        }
        IItemHandler itemHandler = stack.getCapability(Capabilities.ItemHandler.ITEM);
        IFluidHandler fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (itemHandler == null && fluidHandler == null) {
            return null;
        }
        return new PlayerStorageItemWrapper(computer, peripheral, player, itemProvider, itemUpdater, stack, itemHandler, fluidHandler);
    }

    public boolean isValid() {
        ServerPlayer player = this.player.get();
        if (player == null || player.isRemoved()) {
            return false;
        }
        if (this.itemProvider.apply(player) != this.stack) {
            return false;
        }
        if (!this.peripheral.isAccessValid(this.computer)) {
            return false;
        }
        return this.peripheral.getOwnerPlayer() == player;
    }

    protected final void assertValid() throws LuaException {
        if (!this.isValid()) {
            throw new LuaException("Storage item outdate");
        }
    }

    protected final void assertValidItemHandler() throws LuaException {
        if (this.itemHandler == null) {
            throw new LuaException("Storage item is not an item storage");
        }
        this.assertValid();
    }

    protected final void assertValidFluidHandler() throws LuaException {
        if (this.fluidHandler == null) {
            throw new LuaException("Storage item is not a fluid storage");
        }
        this.assertValid();
    }

    @LuaFunction(value = "isValid", mainThread = true)
    public final boolean isValidLua() {
        return this.isValid();
    }

    @LuaFunction
    public final boolean isItemStorage() {
        return this.itemHandler != null;
    }

    @LuaFunction
    public final boolean isFluidStorage() {
        return this.fluidHandler != null;
    }

    //// BEGIN ITEM METHODS ////

    @LuaFunction(mainThread = true)
    public final int size() throws LuaException {
        this.assertValidItemHandler();
        return this.itemHandler.getSlots();
    }

    @LuaFunction(mainThread = true)
    public final Map<Integer, ?> list() throws LuaException {
        this.assertValidItemHandler();
        return InventoryUtil.list(this.itemHandler);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult importItem(String fromName, Optional<Map<?, ?>> filterTable) throws LuaException {
        this.assertValidItemHandler();

        IItemHandler inventoryFrom = this.getItemHandler(fromName);

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(filterTable.orElse(null)));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }

        return MethodResult.of(ItemUtil.moveItem(inventoryFrom, this.itemHandler, filter.left()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult exportItem(String toName, Optional<Map<?, ?>> filterTable) throws LuaException {
        this.assertValidItemHandler();

        IItemHandler inventoryTo = this.getItemHandler(toName);

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(filterTable.orElse(null)));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }

        return MethodResult.of(ItemUtil.moveItem(this.itemHandler, inventoryTo, filter.left()));
    }

    //// END ITEM METHODS ////

    //// BEGIN FLUID METHODS ////

    @LuaFunction(mainThread = true)
    public final Map<Integer, ?> tanks() throws LuaException {
        this.assertValidFluidHandler();
        Map<Integer, Map<String, ?>> data = new HashMap<>();
        int size = this.fluidHandler.getTanks();
        for (int i = 0; i < size; i++) {
            data.put(i + 1, LuaConverter.fluidStackToLua(this.fluidHandler.getFluidInTank(i)));
        }
        return data;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult importFluid(String fromName, Optional<Map<?, ?>> filterTable) throws LuaException {
        this.assertValidFluidHandler();

        IFluidHandler inventoryFrom = this.peripheral.getFluidHandler(this.computer, fromName);

        Pair<FluidFilter, String> filter = FluidFilter.parse(EmptyLuaTable.orEmpty(filterTable.orElse(null)));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }

        int amount = FluidUtil.moveFluid(inventoryFrom, this.fluidHandler, filter.left());
        this.postFluidUpdate();
        return MethodResult.of(amount);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult exportFluid(String toName, Optional<Map<?, ?>> filterTable) throws LuaException {
        this.assertValidFluidHandler();

        IFluidHandler inventoryTo = this.peripheral.getFluidHandler(this.computer, toName);

        Pair<FluidFilter, String> filter = FluidFilter.parse(EmptyLuaTable.orEmpty(filterTable.orElse(null)));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }

        int amount = FluidUtil.moveFluid(this.fluidHandler, inventoryTo, filter.left());
        this.postFluidUpdate();
        return MethodResult.of(amount);
    }

    //// END FLUID METHODS ////

    @NotNull
    private IItemHandler getItemHandler(String name) throws LuaException {
        if (name.equals(InventoryManagerPeripheral.PLAYER_INV_MAGIC_NAME)) {
            ServerPlayer player = this.player.get();
            if (player == null || player.isRemoved()) {
                throw new LuaException("Storage item outdate");
            }
            return new PlayerInvWrapper(player.getInventory());
        }
        return this.peripheral.getItemHandler(this.computer, name);
    }

    protected void postFluidUpdate() {
        ServerPlayer player = this.player.get();
        if (this.fluidHandler instanceof IFluidHandlerItem handler) {
            ItemStack stack = handler.getContainer();
            if (this.itemProvider.apply(player) != stack) {
                this.stack = stack;
                this.itemUpdater.accept(player, stack);
            }
        }
    }
}
