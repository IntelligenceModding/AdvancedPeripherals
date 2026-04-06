package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.InventoryManagerOwner;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.PlayerInvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InventoryManagerPeripheral extends BasePeripheral<InventoryManagerOwner> {

    public static final String PERIPHERAL_TYPE = "inventory_manager";

    public InventoryManagerPeripheral(InventoryManagerEntity tileEntity) {
        super(PERIPHERAL_TYPE, new InventoryManagerOwner(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableInventoryManager.get();
    }

    @Override
    protected Map<String, Object> getPeripheralConfiguration() {
        Map<String, Object> configs = super.getPeripheralConfiguration();
        configs.put("itemsTransferEnabled", APConfig.PERIPHERALS_CONFIG.enableItemsTransfer.get());
        return configs;
    }

    private ServerPlayer getOwnerPlayerOrError() throws LuaException {
        Player player = owner.getOwner();
        if (player == null) {
            throw new LuaException("The Inventory Manager doesn't have a memory card or it isn't bound to a player.");
        }
        return (ServerPlayer) player;
    }

    private IItemHandler getPlayerInventory() throws LuaException {
        return new PlayerInvWrapper(this.getOwnerPlayerOrError().getInventory());
    }

    @LuaFunction
    public final MethodResult getOwner() throws LuaException {
        Player player = owner.getOwner();
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
    public final Map<Integer, Object> list() throws LuaException {
        return InventoryUtil.list(this.getPlayerInventory());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult pushItems(IComputerAccess computer, String toName, Optional<Map<?, ?>> filterTable) throws LuaException {
        this.assertAllowItemTransfers();

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(filterTable.orElse(null)));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }

        IItemHandler inventoryTo = this.getInventoryHandler(computer, toName);
        IItemHandler inventoryFrom = this.getPlayerInventory();
        return MethodResult.of(ItemUtil.moveItem(inventoryFrom, inventoryTo, filter.left()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult pullItems(IComputerAccess computer, String fromName, Optional<Map<?, ?>> filterTable) throws LuaException {
        this.assertAllowItemTransfers();

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(filterTable.orElse(null)));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }

        IItemHandler inventoryFrom = this.getInventoryHandler(computer, fromName);
        IItemHandler inventoryTo = this.getPlayerInventory();
        return MethodResult.of(ItemUtil.moveItem(inventoryFrom, inventoryTo, filter.left()));
    }

    @LuaFunction(mainThread = true)
    public final PlayerStorageItemWrapper wrapStorageItem(IComputerAccess computer, int slot) throws LuaException {
        return PlayerStorageItemWrapper.create(computer, this.getOwnerPlayerOrError(), slot - 1);
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

    private void assertAllowItemTransfers() throws LuaException {
        if (!APConfig.PERIPHERALS_CONFIG.enableItemsTransfer.get()) {
            throw new LuaException("This function is disabled in the config [Inventory_Manager.enableItemsTransfer]. Activate it or ask admins if they can activate it.");
        }
    }

    @NotNull
    private IItemHandler getInventoryHandler(IComputerAccess computer, String name) throws LuaException {
        IPeripheral toPeripheral = computer.getAvailablePeripheral(name);
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
