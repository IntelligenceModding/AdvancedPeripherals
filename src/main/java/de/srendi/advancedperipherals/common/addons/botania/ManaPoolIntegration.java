package de.srendi.advancedperipherals.common.addons.botania;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ManaPoolIntegration extends BlockEntityIntegrationPeripheral<ManaPoolBlockEntity> {

    public ManaPoolIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "manaPool";
    }

    @LuaFunction(mainThread = true)
    public final int getMana() {
        return blockEntity.getCurrentMana();
    }

    @LuaFunction(mainThread = true)
    public final int getMaxMana() {
        return blockEntity.getMaxMana();
    }

    @LuaFunction(mainThread = true)
    public final int getManaNeeded() {
        return blockEntity.getAvailableSpaceForMana();
    }

    @LuaFunction(mainThread = true)
    public final boolean isFull() {
        return blockEntity.isFull();
    }

    @LuaFunction(mainThread = true)
    public final boolean isEmpty() {
        return blockEntity.getMana() == 0;
    }

    @LuaFunction(mainThread = true)
    public final boolean hasItems() {
        return !getPoolItems().isEmpty();
    }

    @LuaFunction(mainThread = true)
    public final boolean canChargeItem() {
        return blockEntity.isOutputtingPower();
    }

    @LuaFunction(mainThread = true)
    public final Object getItems() {
        List<ItemStack> items = getPoolItems();
        if(items.isEmpty())
            return null;
        Object[] luaStacks = new Object[items.size()];

        for (int item = 0; item < items.size(); item++) {
            luaStacks[item] = LuaConverter.stackToObject(items.get(item));
        }

        return luaStacks;
    }

    private List<ItemStack> getPoolItems() {
        BlockPos position = blockEntity.getBlockPos();
        return blockEntity.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(position, position.offset(1, 1, 1)))
                .stream()
                .map(ItemEntity::getItem)
                .collect(Collectors.toList());
    }

}
