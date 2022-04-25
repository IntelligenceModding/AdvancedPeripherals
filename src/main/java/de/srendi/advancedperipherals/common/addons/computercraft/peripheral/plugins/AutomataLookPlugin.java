package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.HashMap;
import java.util.Map;

public class AutomataLookPlugin extends AutomataCorePlugin {

    public AutomataLookPlugin(AutomataCorePeripheral automataCore) {
        super(automataCore);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult lookAtBlock() {
        automataCore.addRotationCycle();
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        HitResult result = owner.withPlayer(APFakePlayer -> APFakePlayer.findHit(true, false));
        if (result.getType() == HitResult.Type.MISS)
            return MethodResult.of(null, "No block find");
        BlockHitResult blockHit = (BlockHitResult) result;
        BlockState state = owner.getLevel().getBlockState(blockHit.getBlockPos());
        Map<String, Object> data = new HashMap<>();
        ResourceLocation blockName = state.getBlock().getRegistryName();
        if (blockName != null)
            data.put("name", blockName.toString());
        data.put("tags", LuaConverter.tagsToList(() -> state.getBlock().builtInRegistryHolder().tags()));
        return MethodResult.of(data);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult lookAtEntity() {
        automataCore.addRotationCycle();
        HitResult result = automataCore.getPeripheralOwner().withPlayer(APFakePlayer -> APFakePlayer.findHit(false, true));
        if (result.getType() == HitResult.Type.MISS) {
            return MethodResult.of(null, "No entity find");
        }
        EntityHitResult entityHit = (EntityHitResult) result;
        return MethodResult.of(LuaConverter.entityToLua(entityHit.getEntity()));
    }

}
