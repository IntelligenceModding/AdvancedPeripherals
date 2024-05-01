package de.srendi.advancedperipherals.common.addons.botania;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.common.block.block_entity.mana.ManaSpreaderBlockEntity;

public class SpreaderIntegration extends BlockEntityIntegrationPeripheral<ManaSpreaderBlockEntity> {

    public SpreaderIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "mana_spreader";
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
    public final Object getBounding() {
        if (blockEntity.getBinding() == null) return null;
        return LuaConverter.posToObject(blockEntity.getBinding());
    }

    @LuaFunction(mainThread = true)
    public final String getVariant() {
        return blockEntity.getVariant().toString();
    }

    @LuaFunction(mainThread = true)
    public final boolean isFull() {
        return blockEntity.isFull();
    }

    @LuaFunction(mainThread = true)
    public final boolean isEmpty() {
        return blockEntity.isEmpty();
    }

}
