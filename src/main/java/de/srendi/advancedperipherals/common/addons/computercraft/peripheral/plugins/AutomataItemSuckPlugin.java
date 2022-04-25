package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.squiddev.cobalt.compiler.LuaC;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.SUCK;

public class AutomataItemSuckPlugin extends AutomataCorePlugin {

    public AutomataItemSuckPlugin(AutomataCorePeripheral automataCore) {
        super(automataCore);
    }

    @Override
    public IPeripheralOperation<?>[] getOperations() {
        return new IPeripheralOperation[]{SUCK};
    }

    protected AABB getBox(BlockPos pos) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        int interactionRadius = automataCore.getInteractionRadius();
        return new AABB(
                x - interactionRadius, y - interactionRadius, z - interactionRadius,
                x + interactionRadius, y + interactionRadius, z + interactionRadius
        );
    }

    protected List<ItemEntity> getItems() {
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        return owner.getLevel().getEntitiesOfClass(ItemEntity.class, getBox(owner.getPos()));
    }

    protected int suckItem(ItemEntity entity, int requiredQuantity) {
        ItemStack stack = entity.getItem().copy();

        ItemStack storeStack;
        ItemStack leaveStack;
        if (stack.getCount() > requiredQuantity) {
            storeStack = stack.split(requiredQuantity);
            leaveStack = stack;
        } else {
            storeStack = stack;
            leaveStack = ItemStack.EMPTY;
        }

        ItemStack remainder = automataCore.getPeripheralOwner().storeItem(storeStack);

        if (remainder != storeStack) {
            if (remainder.isEmpty() && leaveStack.isEmpty()) {
                entity.remove(Entity.RemovalReason.KILLED);
            } else if (remainder.isEmpty()) {
                entity.setItem(leaveStack);
            } else if (leaveStack.isEmpty()) {
                entity.setItem(remainder);
            } else {
                leaveStack.grow(remainder.getCount());
                entity.setItem(leaveStack);
            }
        }
        requiredQuantity -= storeStack.getCount();
        return requiredQuantity;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult scanItems() {
        automataCore.addRotationCycle();
        List<ItemEntity> items = getItems();
        Map<Integer, Map<String, Object>> data = new HashMap<>();
        int index = 1;
        for (ItemEntity item : items) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("entity_id", item.getId());
            itemData.put("name", item.getItem().getDisplayName().getString());
            ResourceLocation itemName = item.getItem().getItem().getRegistryName();
            if (itemName != null)
                itemData.put("technicalName", itemName.toString());
            itemData.put("count", item.getItem().getCount());
            itemData.put("tags", LuaConverter.tagsToList(() -> item.getItem().getItem().builtInRegistryHolder().tags()));
            data.put(index, itemData);
            index++;
        }
        return MethodResult.of(data);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult collectSpecificItem(@Nonnull IArguments arguments) throws LuaException {
        String technicalName = arguments.getString(0);
        int requiredQuantityArg = arguments.optInt(1, Integer.MAX_VALUE);
        return automataCore.withOperation(SUCK, context -> {
            List<ItemEntity> items = getItems();
            int requiredQuantity = requiredQuantityArg;
            for (ItemEntity item : items) {
                ResourceLocation itemName = item.getItem().getItem().getRegistryName();
                if (itemName == null) continue;
                if (itemName.toString().equals(technicalName)) {
                    requiredQuantity -= suckItem(item, requiredQuantity);
                }
                if (requiredQuantity <= 0)
                    break;
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult collectItems(@Nonnull IArguments arguments) throws LuaException {
        int requiredQuantityArg = arguments.optInt(0, Integer.MAX_VALUE);
        return automataCore.withOperation(SUCK, context -> {
            if (requiredQuantityArg == 0) {
                return MethodResult.of(true);
            }

            List<ItemEntity> items = getItems();
            if (items.isEmpty()) {
                return MethodResult.of(null, "Nothing to take");
            }
            int requiredQuantity = requiredQuantityArg;
            for (ItemEntity entity : items) {
                requiredQuantity -= suckItem(entity, requiredQuantity);
                if (requiredQuantity <= 0) {
                    break;
                }
            }
            return MethodResult.of(true);
        });
    }
}
