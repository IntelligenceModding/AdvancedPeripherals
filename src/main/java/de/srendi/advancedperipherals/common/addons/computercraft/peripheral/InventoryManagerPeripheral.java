package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.blocks.tileentity.InventoryManagerTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class InventoryManagerPeripheral extends BasePeripheral {

	public InventoryManagerPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
		super(type, tileEntity);
	}

	@Override
	public boolean isEnabled() {
		return AdvancedPeripheralsConfig.enableInventoryManager;
	}

	@LuaFunction
	public final String getOwner() {
		if (getOwnerPlayer() == null)
			return null;
		return getOwnerPlayer().getName().getString();
	}

	@LuaFunction(mainThread = true)
	public final int addItemToPlayer(String inventoryBlock, int count, Optional<String> item) throws LuaException {
		ensurePlayerLinked();
		ItemStack stack = ItemStack.EMPTY;
		if (item.isPresent()) {
			Item item1 = ItemUtil.getRegistryEntry(item.get(), ForgeRegistries.ITEMS);
			stack = new ItemStack(item1, count);
		}

		Direction direction = Direction.valueOf(inventoryBlock.toUpperCase(Locale.ROOT));

		TileEntity targetEntity = tileEntity.getWorld().getTileEntity(tileEntity.getPos().offset(direction));
		IItemHandler inventoryFrom = targetEntity != null ? targetEntity
				.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve().orElse(null) : null;
		PlayerInventory inventoryTo = getOwnerPlayer().inventory;
		if (inventoryFrom == null) {
			AdvancedPeripherals.debug("Debug2");
			return 0;
		}

		int amount = count;
		int transferableAmount = 0;

		for (int i = 0; i < inventoryFrom.getSlots(); i++) {
			if (!stack.isEmpty())
				if (inventoryFrom.getStackInSlot(i).isItemEqual(stack)) {
					if (inventoryFrom.getStackInSlot(i).getCount() >= amount) {
						if (inventoryTo.addItemStackToInventory(inventoryFrom.extractItem(i, amount, true))) {
							inventoryFrom.extractItem(i, amount, false);
							transferableAmount += amount;
						}
						break;
					} else {
						int subcount = inventoryFrom.getStackInSlot(i).getCount();
						if (inventoryTo.addItemStackToInventory(inventoryFrom.extractItem(i, subcount, true))) {
							inventoryFrom.extractItem(i, subcount, false);
							amount = count - subcount;
							transferableAmount += subcount;
						}
					}
				}
			if (stack.isEmpty())
				if (inventoryFrom.getStackInSlot(i).getCount() >= amount) {
					if (inventoryTo.addItemStackToInventory(inventoryFrom.extractItem(i, amount, true))) {
						inventoryFrom.extractItem(i, amount, false);
						transferableAmount += amount;
					}
					break;
				} else {
					int subcount = inventoryFrom.getStackInSlot(i).getCount();
					if (inventoryTo.addItemStackToInventory(inventoryFrom.extractItem(i, subcount, true))) {
						inventoryFrom.extractItem(i, subcount, false);
						amount = count - subcount;
						transferableAmount += subcount;
					}
				}
		}
		return transferableAmount;
	}

	@LuaFunction(mainThread = true)
	public final int removeItemFromPlayer(String inventoryBlock, int count, Optional<String> item) throws LuaException {
		ensurePlayerLinked();
		ItemStack stack = ItemStack.EMPTY;
		if (item.isPresent()) {
			Item item1 = ItemUtil.getRegistryEntry(item.get(), ForgeRegistries.ITEMS);
			stack = new ItemStack(item1, count);
		}

		Direction direction = Direction.valueOf(inventoryBlock.toUpperCase(Locale.ROOT));

		TileEntity targetEntity = tileEntity.getWorld().getTileEntity(tileEntity.getPos().offset(direction));
		PlayerInventory inventoryFrom = getOwnerPlayer().inventory;
		IItemHandler inventoryTo = targetEntity != null ? targetEntity
				.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve().orElse(null) : null;
		if (inventoryFrom == null) {
			AdvancedPeripherals.debug("Debug2");
			return 0;
		}

		int amount = count;
		int transferableAmount = 0;

		ItemStack rest = ItemStack.EMPTY;
		for (int i = 0; i < inventoryFrom.getSizeInventory(); i++) {
			if (!stack.isEmpty())
				if (inventoryFrom.getStackInSlot(i).isItemEqual(stack)) {
					if (inventoryFrom.getStackInSlot(i).getCount() >= amount) {
						rest = insertItem(inventoryTo, inventoryFrom.decrStackSize(i, amount));
						transferableAmount += amount - rest.getCount();
						break;
					} else {
						int subcount = inventoryFrom.getStackInSlot(i).getCount();
						rest = insertItem(inventoryTo, inventoryFrom.decrStackSize(i, subcount));
						amount = count - subcount;
						transferableAmount += subcount - rest.getCount();
						if (!rest.isEmpty())
							break;
					}
				}
			if (stack.isEmpty())
				if (inventoryFrom.getStackInSlot(i).getCount() >= amount) {
					rest = insertItem(inventoryTo, inventoryFrom.decrStackSize(i, amount));
					transferableAmount += amount - rest.getCount();
					break;
				} else {
					int subcount = inventoryFrom.getStackInSlot(i).getCount();
					rest = insertItem(inventoryTo, inventoryFrom.decrStackSize(i, subcount));
					amount = count - subcount;
					transferableAmount += subcount - rest.getCount();
					if (!rest.isEmpty())
						break;
				}
		}
		if (!rest.isEmpty()) {
			inventoryFrom.addItemStackToInventory(rest);
		}
		return transferableAmount;
	}

	@LuaFunction
	public final Map<Integer, Object> getItems() throws LuaException {
		ensurePlayerLinked();
		Map<Integer, Object> items = new HashMap<>();
		int i = 0;
		for (ItemStack stack : getOwnerPlayer().inventory.mainInventory) {
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

	@LuaFunction
	public final Map<Integer, Object> getArmor() throws LuaException {
		ensurePlayerLinked();
		Map<Integer, Object> items = new HashMap<>();
		int i = 0;
		for (ItemStack stack : getOwnerPlayer().inventory.armorInventory) {
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

	@LuaFunction
	public final boolean isPlayerEquipped() throws LuaException {
		ensurePlayerLinked();
		for (ItemStack stack : getOwnerPlayer().inventory.armorInventory) {
			if (!stack.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@LuaFunction
	public final boolean isWearing(int index) throws LuaException {
		ensurePlayerLinked();
		int i = 0;
		for (ItemStack stack : getOwnerPlayer().inventory.armorInventory) {
			if (!stack.isEmpty()) {
				if (index == i)
					return true;
				i++;
			}
		}
		return false;
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

	private void ensurePlayerLinked() throws LuaException {
		if (getOwnerPlayer() == null) {
			throw new LuaException("The Inventory Manager doesn't have a memory card or it isn't bound to a player.");
		}
	}

	private ItemStack insertItem(IItemHandler inventoryTo, ItemStack stack) {
		for (int i = 0; i < inventoryTo.getSlots(); i++) {
			if (stack.isEmpty())
				break;
			stack = inventoryTo.insertItem(i, stack, false);
		}
		return stack;
	}
}
