package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import appeng.api.config.Actionable;
import com.sun.corba.se.spi.ior.ObjectKey;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.blocks.tileentity.InventoryManagerTileEntity;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class InventoryManagerPeripheral extends BasePeripheral {

    //addItemToPlayer
    //removeItemFromPlayer
    //getItems

    public InventoryManagerPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @LuaFunction
    public final String getOwner() {
        return ((InventoryManagerTileEntity) tileEntity).getOwnerPlayer().getName().getString();
    }

    @LuaFunction
    public final int addItemToPlayer(String inventoryBlock, int count, Optional<String> item) {
        ItemStack stack = ItemStack.EMPTY;
        if (item.isPresent()) {
            Item item1 = ItemUtil.getRegistryEntry(item.get(), ForgeRegistries.ITEMS);
            stack = new ItemStack(item1, count);
        }

        Direction direction = Direction.valueOf(inventoryBlock.toUpperCase(Locale.ROOT));

        TileEntity targetEntity = tileEntity.getWorld().getTileEntity(tileEntity.getPos().offset(direction));
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve().orElse(null) : null;
        if (inventory == null) {
            AdvancedPeripherals.Debug("Debug2");
            return 0;
        }

        int amount = count;
        int transferableAmount = 0;

        for (int i = 0; i < inventory.getSlots(); i++) {
            if (!stack.isEmpty())
                if (inventory.getStackInSlot(i).isItemEqual(stack)) {
                    if (inventory.getStackInSlot(i).getCount() >= amount) {
                        transferableAmount += amount;
                        getOwnerPlayer().inventory.addItemStackToInventory(inventory.extractItem(i, amount, false));
                        break;
                    } else {
                        amount = count - inventory.getStackInSlot(i).getCount();
                        transferableAmount += inventory.getStackInSlot(i).getCount();
                        getOwnerPlayer().inventory.addItemStackToInventory(inventory.extractItem(i, inventory.getStackInSlot(i).getCount(), false));
                    }
                }
            if (stack.isEmpty())
                if (inventory.getStackInSlot(i).getCount() >= amount) {
                    transferableAmount += amount;
                    getOwnerPlayer().inventory.addItemStackToInventory(inventory.extractItem(i, amount, false));
                    break;
                } else {
                    amount = count - inventory.getStackInSlot(i).getCount();
                    transferableAmount += inventory.getStackInSlot(i).getCount();
                    getOwnerPlayer().inventory.addItemStackToInventory(inventory.extractItem(i, inventory.getStackInSlot(i).getCount(), false));
                }
        }
        return transferableAmount;
    }

    @LuaFunction
    public final Map<Integer, Object> getItems() {
        Map<Integer, Object> items = new HashMap<>();
        int i = 0;
        for (ItemStack stack : ((InventoryManagerTileEntity) tileEntity).getOwnerPlayer().inventory.mainInventory) {
            if (!stack.isEmpty()) {
                HashMap<Object, Object> map = new HashMap<>();
                String displayName = stack.getDisplayName().getString();
                CompoundNBT nbt = stack.getOrCreateTag();
                map.put("name", stack.getItem().getRegistryName().toString());
                map.put("amount", stack.getCount());
                map.put("displayName", displayName);
                map.put("nbt", getMapFromNBT(nbt));
                map.put("tags", getListFromTags(stack.getItem().getTags()));
                items.put(i, map);
                i++;
            }
        }
        return items;
    }

    private Map<Object, Object> getMapFromNBT(CompoundNBT nbt) {
        Map<Object, Object> map = new HashMap<>();
        for (String value : nbt.keySet()) {
            map.put(value, String.valueOf(nbt.get(value)));
        }
        return map;
    }

    private List<String> getListFromTags(Set<ResourceLocation> tags) {
        List<String> list = new ArrayList<>();
        for (ResourceLocation value : tags) {
            list.add(value.getNamespace() + ":" + value.getPath());
        }
        return list;
    }

    private PlayerEntity getOwnerPlayer() {
        return ((InventoryManagerTileEntity) tileEntity).getOwnerPlayer();
    }

}
