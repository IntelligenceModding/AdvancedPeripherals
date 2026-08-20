package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.InventoryManagerOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.PocketPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.InventoryManagerEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.EmptyLuaTable;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import de.srendi.advancedperipherals.common.util.inventory.PlayerStorageItemWrapper;
import de.srendi.advancedperipherals.common.util.inventory.InventoryUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralPlugin;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class InventoryManagerPeripheral extends BasePeripheral<IPeripheralOwner> {
    public static final String PLAYER_INV_MAGIC_NAME = "@";
    public static final String PERIPHERAL_TYPE = "inventory_manager";
    private static final List<Function<InventoryManagerPeripheral, IPeripheralPlugin>> PERIPHERAL_PLUGINS = new ArrayList<>();

    private final Set<IComputerAccess> computerAccesses = ConcurrentHashMap.newKeySet();

    protected InventoryManagerPeripheral(IPeripheralOwner owner) {
        super(PERIPHERAL_TYPE, owner);
        for (Function<InventoryManagerPeripheral, IPeripheralPlugin> plugin : PERIPHERAL_PLUGINS) {
            this.addPlugin(plugin.apply(this));
        }
    }

    public InventoryManagerPeripheral(InventoryManagerEntity tileEntity) {
        this(new InventoryManagerOwner(tileEntity));
    }

    public InventoryManagerPeripheral(IPocketAccess pocket) {
        this(PocketPeripheralOwner.of(pocket));
    }

    public static void addIntegrationPlugin(Function<InventoryManagerPeripheral, IPeripheralPlugin> plugin) {
        PERIPHERAL_PLUGINS.add(plugin);
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableInventoryManager.get();
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        super.attach(computer);
        this.computerAccesses.add(computer);
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        this.computerAccesses.remove(computer);
        super.detach(computer);
    }

    public boolean isAccessValid(IComputerAccess access) {
        return this.computerAccesses.contains(access);
    }

    @Override
    protected Map<String, Object> getPeripheralConfiguration() {
        Map<String, Object> configs = super.getPeripheralConfiguration();
        configs.put("itemsTransferEnabled", APConfig.PERIPHERALS_CONFIG.enableItemsTransfer.get());
        return configs;
    }

    public Player getOwnerPlayer() {
        return owner instanceof InventoryManagerOwner imOwner
            ? imOwner.getOwner()
            : owner.getHoldingEntity() instanceof Player player ? player : null;
    }

    public ServerPlayer getOwnerPlayerOrError() throws LuaException {
        Player player = this.getOwnerPlayer();
        if (player == null) {
            throw new LuaException("The Inventory Manager doesn't have a memory card or it isn't bound to a player.");
        }
        return (ServerPlayer) player;
    }

    public IItemHandler getPlayerInventory() throws LuaException {
        return new PlayerInvWrapper(this.getOwnerPlayerOrError().getInventory());
    }

    @LuaFunction
    public final MethodResult getOwner() throws LuaException {
        Player player = this.getOwnerPlayer();
        if (player == null) {
            return MethodResult.of();
        }
        return MethodResult.of(player.getUUID().toString(), player.getGameProfile().getName());
    }

    @LuaFunction(mainThread = true)
    public final int size() throws LuaException {
        return this.getOwnerPlayerOrError().getInventory().getContainerSize();
    }

    @LuaFunction(mainThread = true)
    public final Map<Integer, ?> list() throws LuaException {
        return InventoryUtil.list(this.getPlayerInventory());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult importItem(IComputerAccess computer, String fromName, Optional<Map<?, ?>> filterTable) throws LuaException {
        this.assertAllowItemTransfers();

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(filterTable.orElse(null)));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }

        IItemHandler inventoryTo = this.getPlayerInventory();
        IItemHandler inventoryFrom = this.getItemHandler(computer, fromName);
        return MethodResult.of(ItemUtil.moveItem(inventoryFrom, inventoryTo, filter.left()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult exportItem(IComputerAccess computer, String toName, Optional<Map<?, ?>> filterTable) throws LuaException {
        this.assertAllowItemTransfers();

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(filterTable.orElse(null)));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }

        IItemHandler inventoryFrom = this.getPlayerInventory();
        IItemHandler inventoryTo = this.getItemHandler(computer, toName);
        return MethodResult.of(ItemUtil.moveItem(inventoryFrom, inventoryTo, filter.left()));
    }

    @LuaFunction(mainThread = true)
    public final PlayerStorageItemWrapper wrapStorageItem(IComputerAccess computer, int slot) throws LuaException {
        return PlayerStorageItemWrapper.create(computer, this, this.getOwnerPlayerOrError(), slot - 1);
    }

    @LuaFunction(mainThread = true)
    public final boolean isWearing(int index) throws LuaException {
        index--;
        List<ItemStack> armor = this.getOwnerPlayerOrError().getInventory().armor;
        return 0 <= index && index < armor.size() && !armor.get(index).isEmpty();
    }

    @LuaFunction(mainThread = true)
    public final int getEmptySlots() throws LuaException {
        int count = 0;
        for (ItemStack stack : this.getOwnerPlayerOrError().getInventory().items) {
            if (stack.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    @LuaFunction(mainThread = true)
    public final int getFreeSlot() throws LuaException {
        return this.getOwnerPlayerOrError().getInventory().getFreeSlot() + 1;
    }

    @LuaFunction(mainThread = true)
    public final int getHandSlot() throws LuaException {
        return this.getOwnerPlayerOrError().getInventory().selected + 1;
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getItemInHand() throws LuaException {
        Player player = this.getOwnerPlayerOrError();
        return LuaConverter.itemStackToLuaWithSlot(player.getMainHandItem(), player.getInventory().selected);
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getItemInOffHand() throws LuaException {
        return LuaConverter.itemStackToLua(this.getOwnerPlayerOrError().getOffhandItem());
    }

    public void assertAllowItemTransfers() throws LuaException {
        if (!APConfig.PERIPHERALS_CONFIG.enableItemsTransfer.get()) {
            throw new LuaException("This function is disabled in the config [Inventory_Manager.enableItemsTransfer]. Activate it or ask admins if they can activate it.");
        }
    }

    @Override
    @NotNull
    public IItemHandler getItemHandler(IComputerAccess computer, String name) throws LuaException {
        if (name.equals(PLAYER_INV_MAGIC_NAME)) {
            return this.getPlayerInventory();
        }
        return super.getItemHandler(computer, name);
    }

    @Override
    @NotNull
    public IFluidHandler getFluidHandler(IComputerAccess computer, String name) throws LuaException {
        return super.getFluidHandler(computer, name);
    }
}
