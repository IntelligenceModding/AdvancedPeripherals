package de.srendi.advancedperipherals.common.addons.botania;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.common.block.block_entity.mana.ManaSpreaderBlockEntity;

public class SpreaderIntegration implements APGenericPeripheral {

    @Override
    public String getPeripheralType() {
        return "manaSpreader";
    }

    @LuaFunction(mainThread = true)
    public final int getMana(ManaSpreaderBlockEntity blockEntity) {
        return blockEntity.getCurrentMana();
    }

    @LuaFunction(mainThread = true)
    public final int getMaxMana(ManaSpreaderBlockEntity blockEntity) {
        return blockEntity.getMaxMana();
    }

    @LuaFunction(mainThread = true)
    public final Object getBounding(ManaSpreaderBlockEntity blockEntity) {
        if (blockEntity.getBinding() == null) return null;
        return LuaConverter.posToObject(blockEntity.getBinding());
    }

    @LuaFunction(mainThread = true)
    public final String getVariant(ManaSpreaderBlockEntity blockEntity) {
        return blockEntity.getVariant().toString();
    }

    @LuaFunction(mainThread = true)
    public final boolean isFull(ManaSpreaderBlockEntity blockEntity) {
        return blockEntity.isFull();
    }

    @LuaFunction(mainThread = true)
    public final boolean isEmpty(ManaSpreaderBlockEntity blockEntity) {
        return blockEntity.getCurrentMana() == 0;
    }

    @LuaFunction(mainThread = true)
    public final boolean hasLens(ManaSpreaderBlockEntity blockEntity) {
        return blockEntity.getItem(0) != ItemStack.EMPTY;
    }

    @LuaFunction(mainThread = true)
    public final Object getLens(ManaSpreaderBlockEntity blockEntity) {
        if(blockEntity.getItem(0) == ItemStack.EMPTY)
            return null;
        return LuaConverter.stackToObject(blockEntity.getItem(0));
    }

}
