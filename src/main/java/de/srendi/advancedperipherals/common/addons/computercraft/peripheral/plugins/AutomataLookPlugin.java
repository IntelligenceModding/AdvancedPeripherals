package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.owner.TurtlePeripheralOwner;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

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
        RayTraceResult result = owner.withPlayer(APFakePlayer -> APFakePlayer.findHit(true, false));
        if (result.getType() == RayTraceResult.Type.MISS)
            return MethodResult.of(null, "No block find");
        BlockRayTraceResult blockHit = (BlockRayTraceResult) result;
        BlockState state = owner.getWorld().getBlockState(blockHit.getBlockPos());
        Map<String, Object> data = new HashMap<>();
        ResourceLocation blockName = state.getBlock().getRegistryName();
        if (blockName != null)
            data.put("name", blockName.toString());
        data.put("tags", LuaConverter.tagsToList(state.getBlock().getTags()));
        return MethodResult.of(data);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult lookAtEntity() {
        automataCore.addRotationCycle();
        RayTraceResult result = automataCore.getPeripheralOwner().withPlayer(APFakePlayer -> APFakePlayer.findHit(false, true));
        if (result.getType() == RayTraceResult.Type.MISS) {
            return MethodResult.of(null, "No entity find");
        }
        EntityRayTraceResult entityHit = (EntityRayTraceResult) result;
        return MethodResult.of(LuaConverter.entityToLua(entityHit.getEntity()));
    }

}
