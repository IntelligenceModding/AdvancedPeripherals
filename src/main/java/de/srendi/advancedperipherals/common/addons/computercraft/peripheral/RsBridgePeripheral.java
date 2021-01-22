package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorage;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorageNode;
import de.srendi.advancedperipherals.common.blocks.tileentity.RsBridgeTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class RsBridgePeripheral implements IPeripheral {

    private final List<IComputerAccess> connectedComputers = new ArrayList<>();
    private final RsBridgeTileEntity tileEntity;

    public RsBridgePeripheral(RsBridgeTileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    private RefinedStorageNode getNode() {
        return this.tileEntity.getNode();
    }

    private INetwork getNetwork() {
        return getNode().getNetwork();
    }

    public List<IComputerAccess> getConnectedComputers() {
        return connectedComputers;
    }

    @NotNull
    @Override
    public String getType() {
        return "rsBridge";
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        connectedComputers.add(computer);
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        connectedComputers.remove(computer);
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return this == iPeripheral;
    }

    @LuaFunction(mainThread = true)
    public final Object[] listItems() {

        /*HashMap<Integer, Object> items = new HashMap<>();
        Supplier<IStackList<ItemStack>> sup = () -> getNetwork().getItemStorageCache().getList();
        Collection<StackListEntry<ItemStack>> entries = sup.get().getStacks();
        List<ItemStack> result = new ArrayList<>(entries.size());
        for (StackListEntry<ItemStack> entry : entries) {
            result.add(entry.getStack());
        }
        int i = 1;
        for (ItemStack stack : result) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
            map.put("amount", stack.getCount());
            map.put("displayName", stack.getDisplayName());
            map.put("nbt", stack.getOrCreateTag());
            items.put(i, map);
            i++;
        }*/
        return new Object[]{"items"};
    }
}
