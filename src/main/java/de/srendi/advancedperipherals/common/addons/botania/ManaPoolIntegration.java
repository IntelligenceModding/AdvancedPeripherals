package de.srendi.advancedperipherals.common.addons.botania;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ManaPoolIntegration implements APGenericPeripheral {

    @Override
    public String getPeripheralType() {
        return "manaPool";
    }

    @LuaFunction(mainThread = true)
    public final int getMana(ManaPoolBlockEntity blockEntity) {
        return blockEntity.getCurrentMana();
    }

    @LuaFunction(mainThread = true)
    public final int getMaxMana(ManaPoolBlockEntity blockEntity) {
        return blockEntity.getMaxMana();
    }

    @LuaFunction(mainThread = true)
    public final int getManaNeeded(ManaPoolBlockEntity blockEntity) {
        return blockEntity.getAvailableSpaceForMana();
    }

    @LuaFunction(mainThread = true)
    public final boolean isFull(ManaPoolBlockEntity blockEntity) {
        return blockEntity.isFull();
    }

    @LuaFunction(mainThread = true)
    public final boolean isEmpty(ManaPoolBlockEntity blockEntity) {
        return blockEntity.getCurrentMana() == 0;
    }

    @LuaFunction(mainThread = true)
    public final boolean canChargeItem(ManaPoolBlockEntity blockEntity) {
        return blockEntity.isOutputtingPower();
    }

    @LuaFunction(mainThread = true)
    public final boolean hasItems(ManaPoolBlockEntity blockEntity) {
        return !getPoolItems(blockEntity).isEmpty();
    }

    @LuaFunction(mainThread = true)
    public final Object getItems(ManaPoolBlockEntity blockEntity) {
        List<ItemStack> items = getPoolItems(blockEntity);
        if(items.isEmpty())
            return null;
        Object[] luaStacks = new Object[items.size()];

        for (int item = 0; item < items.size(); item++) {
            luaStacks[item] = LuaConverter.stackToObject(items.get(item));
        }

        return luaStacks;
    }

    private List<ItemStack> getPoolItems(ManaPoolBlockEntity blockEntity) {
        BlockPos position = blockEntity.getBlockPos();
        return blockEntity.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(position, position.offset(1, 1, 1)))
                .stream()
                .map(ItemEntity::getItem)
                .collect(Collectors.toList());
    }

}
