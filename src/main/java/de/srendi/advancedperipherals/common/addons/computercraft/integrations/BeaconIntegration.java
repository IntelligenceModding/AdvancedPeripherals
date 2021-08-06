package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.Integration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

public class BeaconIntegration extends Integration<BeaconBlockEntity> {
    @Override
    public Class<BeaconBlockEntity> getTargetClass() {
        return BeaconBlockEntity.class;
    }

    @Override
    public BeaconIntegration getNewInstance() {
        return new BeaconIntegration();
    }

    @Override
    public String getType() {
        return "beacon";
    }

    @LuaFunction
    public final int getLevel() {
        // because levels are now protected field .... why?
        CompoundTag savedData = getTileEntity().save(new CompoundTag());
        return savedData.getInt("Levels");
    }

    @LuaFunction
    public final String getPrimaryEffect() {
        return getTileEntity().primaryPower == null ? "none" : getTileEntity().primaryPower.getDescriptionId();
    }

    @LuaFunction
    public final String getSecondaryEffect() {
        return getTileEntity().secondaryPower == null ? "none" : getTileEntity().secondaryPower.getDescriptionId();
    }

}
