package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.addons.APAddon;
// import de.srendi.advancedperipherals.common.addons.valkyrienskies.ValkyrienSkies;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class AutomataLookPlugin extends AutomataCorePlugin {

    public AutomataLookPlugin(AutomataCorePeripheral automataCore) {
        super(automataCore);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult lookAtBlock(@NotNull IArguments arguments) throws LuaException {
        LuaTable<?, ?> options = EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null));

        float yaw = options.optDouble("yaw").orElse(0d).floatValue();
        float pitch = options.optDouble( "pitch").orElse(0d).floatValue();

        automataCore.addRotationCycle();
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        HitResult result = owner.withPlayer(APFakePlayer.wrapActionWithRot(yaw, pitch, p -> p.findHit(true, false)));
        if (result.getType() == HitResult.Type.MISS) {
            return MethodResult.of(null, "No block find");
        }

        BlockHitResult blockHit = (BlockHitResult) result;
        BlockPos blockPos = blockHit.getBlockPos();
        BlockState state = owner.getLevel().getBlockState(blockPos);
        Map<String, Object> data = new HashMap<>();
        ResourceLocation blockName = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (blockName != null) {
            data.put("name", blockName.toString());
        }
        data.put("tags", LuaConverter.tagsToList(state.getBlock().builtInRegistryHolder().tags()));
        Vec3 pos = blockHit.getLocation();
        Vec3 origin = automataCore.getPhysicsPos();
        data.put("x", pos.x - origin.x);
        data.put("y", pos.y - origin.y);
        data.put("z", pos.z - origin.z);
        // if (APAddon.VALKYRIENSKIES) {
        //     ValkyrienSkies.encodeShipInfo(automataCore.getLevel(), blockPos, data);
        // }
        return MethodResult.of(data);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult lookAtEntity(@NotNull IArguments arguments) throws LuaException {
        LuaTable<?, ?> options = EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null));

        float yaw = options.optDouble("yaw").orElse(0d).floatValue();
        float pitch = options.optDouble( "pitch").orElse(0d).floatValue();

        automataCore.addRotationCycle();
        HitResult result = automataCore.getPeripheralOwner().withPlayer(APFakePlayer.wrapActionWithRot(yaw, pitch, p -> p.findHit(false, true)));
        if (result.getType() == HitResult.Type.MISS) {
            return MethodResult.of(null, "No entity find");
        }

        EntityHitResult entityHit = (EntityHitResult) result;
        Vec3 origin = automataCore.getPhysicsPos();
        return MethodResult.of(LuaConverter.completeEntityWithPositionToLua(entityHit.getEntity(), origin, true));
    }

}
