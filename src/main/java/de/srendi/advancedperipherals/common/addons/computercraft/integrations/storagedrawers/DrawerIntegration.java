package de.srendi.advancedperipherals.common.addons.computercraft.integrations.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.base.Integration;
import de.srendi.advancedperipherals.common.addons.storagedrawers.DrawerItemHandler;
import de.srendi.advancedperipherals.common.util.InventoryUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class DrawerIntegration extends Integration<TileEntityDrawers> {
    @Override
    protected Class<TileEntityDrawers> getTargetClass() {
        return TileEntityDrawers.class;
    }

    @Override
    public Integration<?> getNewInstance() {
        return new DrawerIntegration();
    }

    @Nullable
    @Override
    public Object getTarget() {
        return tileEntity;
    }

    @NotNull
    @Override
    public String getType() {
        return "standardDrawer";
    }

    private static IItemHandler extractHandler(@Nullable Object object) {
        if (object instanceof TileEntityDrawersStandard)
            return new DrawerItemHandler(((TileEntityDrawersStandard) object).getGroup());
        if (object instanceof ICapabilityProvider) {
            LazyOptional<IItemHandler> cap = ((ICapabilityProvider) object).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (cap.isPresent()) return cap.orElseThrow(NullPointerException::new);
        }
        if (object instanceof IItemHandler)
            return (IItemHandler) object;
        if (object instanceof IInventory)
            return new InvWrapper((IInventory) object);
        return null;
    }

    @LuaFunction
    public final MethodResult drawerAttributes() {
        Map<String, Object> data = new HashMap<>();
        IDrawerAttributes attributes = tileEntity.getDrawerAttributes();
        data.put("showingQuantity", attributes.isShowingQuantity());
        data.put("voiding", attributes.isVoid());
        data.put("unlimitedStorage", attributes.isUnlimitedStorage());
        return MethodResult.of(data);
    }

    @LuaFunction
    public final MethodResult size() {
        return MethodResult.of(tileEntity.getGroup().getDrawerCount());
    }

    @LuaFunction
    public final MethodResult list() {
        Map<Integer, Map<String, Object>> data = new HashMap<>();
        IDrawerGroup group = tileEntity.getGroup();
        for (int slot = 0; slot < group.getDrawerCount(); slot++) {
            IDrawer drawer = group.getDrawer(slot);
            if (drawer.isEmpty())
                continue;
            data.put(slot + 1, new HashMap<String, Object>() {{
                put("count", drawer.getStoredItemCount());
                ResourceLocation registryName = drawer.getStoredItemPrototype().getItem().getRegistryName();
                if (registryName != null) {
                    put("name", registryName.toString());
                } else {
                    put("name", "unknown");
                }
            }});
        }
        return MethodResult.of(data);
    }

    @LuaFunction
    public final MethodResult getItemDetail(int slot) {
        IDrawerGroup group = tileEntity.getGroup();
        int javaSlot = slot - 1;
        if (javaSlot >= group.getDrawerCount())
            return MethodResult.of(null, "Slot is out of range");
        IDrawer drawer = group.getDrawer(javaSlot);
        if (drawer.isEmpty())
            return MethodResult.of(new HashMap<>());
        Map<String, Object> data = new HashMap<>();
        ItemStack storedItem = drawer.getStoredItemPrototype();
        data.put("count", drawer.getStoredItemCount());
        data.put("displayName", storedItem.getDisplayName().getString());
        ResourceLocation registryName = drawer.getStoredItemPrototype().getItem().getRegistryName();
        if (registryName != null) {
            data.put("name", registryName.toString());
        } else {
            data.put("name", "unknown");
        }
        data.put("tags", storedItem.getItem().getTags());
        data.put("maxCount", drawer.getMaxCapacity(storedItem));
        return MethodResult.of(data);
    }

    @LuaFunction
    public final MethodResult getItemLimit(int slot) {
        IDrawerGroup group = tileEntity.getGroup();
        int javaSlot = slot - 1;
        if (javaSlot >= group.getDrawerCount())
            return MethodResult.of(null, "Slot is out of range");
        IDrawer drawer = group.getDrawer(javaSlot);
        if (drawer.isEmpty())
            return MethodResult.of(drawer.getMaxCapacity());
        return MethodResult.of(drawer.getMaxCapacity(drawer.getStoredItemPrototype()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult pushItems(@Nonnull IComputerAccess access, @Nonnull IArguments arguments) throws LuaException {
        // Parsing arguments
        String toName = arguments.getString(0);
        int fromSlot = arguments.getInt(1);
        int limit = arguments.optInt(2, Integer.MAX_VALUE);
        int toSlot = arguments.optInt(3, -1);
        if (fromSlot < 1 || fromSlot > tileEntity.getGroup().getDrawerCount())
            return MethodResult.of(null, "From slot is incorrect");
        // Find location to transfer to
        IPeripheral location = access.getAvailablePeripheral(toName);
        if (location == null) throw new LuaException("Target '" + toName + "' does not exist");

        IItemHandler to = extractHandler(location.getTarget());
        if (to == null) throw new LuaException("Target '" + toName + "' is not an inventory");
        if (toSlot != -1 && (toSlot < 1 || toSlot > to.getSlots()))
            return MethodResult.of(null, "To slot is incorrect");

        if (limit <= 0)
            return MethodResult.of(0);
        return MethodResult.of(InventoryUtil.moveItem(new DrawerItemHandler(tileEntity.getGroup()), fromSlot - 1, to, toSlot -1, limit));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult pullItems(@Nonnull IComputerAccess access, @Nonnull IArguments arguments) throws LuaException {
        // Parsing arguments
        String fromName = arguments.getString(0);
        int fromSlot = arguments.getInt(1);
        int limit = arguments.optInt(2, Integer.MAX_VALUE);
        int toSlot = arguments.optInt(3, -1);
        if (toSlot != -1 && (toSlot < 1 || toSlot > tileEntity.getGroup().getDrawerCount()))
            return MethodResult.of(null, "To slot is incorrect");
        // Find location to transfer to
        IPeripheral location = access.getAvailablePeripheral(fromName);
        if (location == null) throw new LuaException("Target '" + fromName + "' does not exist");

        IItemHandler from = extractHandler(location.getTarget());
        if (from == null) throw new LuaException("Target '" + fromName + "' is not an inventory");
        if (fromSlot < 1 || fromSlot > from.getSlots())
            return MethodResult.of(null, "From slot is incorrect");
        if (limit <= 0)
            return MethodResult.of(0);
        return MethodResult.of(InventoryUtil.moveItem(from, fromSlot - 1, new DrawerItemHandler(tileEntity.getGroup()), toSlot - 1, limit));
    }
}
