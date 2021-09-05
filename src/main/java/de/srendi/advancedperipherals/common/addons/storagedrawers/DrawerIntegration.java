package de.srendi.advancedperipherals.common.addons.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.util.InventoryUtil;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class DrawerIntegration extends TileEntityIntegrationPeripheral<TileEntityDrawers> {

    public DrawerIntegration(TileEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "standardDrawer";
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
        return InventoryUtil.pushItems(arguments, access, new DrawerItemHandler(tileEntity.getGroup()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult pullItems(@Nonnull IComputerAccess access, @Nonnull IArguments arguments) throws LuaException {
        return InventoryUtil.pullItems(arguments, access, new DrawerItemHandler(tileEntity.getGroup()));
    }
}
