package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SphereOperationContext;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.PocketPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.ScanUtils;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniondc;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SphereOperation.SCAN_BLOCKS;

public class GeoScannerPeripheral extends BasePeripheral<IPeripheralOwner> {

    /**
     * Highly inspired by https://github.com/SquidDev-CC/plethora/ BlockScanner
     */

    public static final String PERIPHERAL_TYPE = "geo_scanner";

    protected GeoScannerPeripheral(IPeripheralOwner owner) {
        super(PERIPHERAL_TYPE, owner);
        owner.attachOperation(SCAN_BLOCKS);
    }

    public GeoScannerPeripheral(PeripheralBlockEntity<?> tileEntity) {
        this(new BlockEntityPeripheralOwner<>(tileEntity).attachFuel());
    }

    public GeoScannerPeripheral(ITurtleAccess turtle, TurtleSide side) {
        this(new TurtlePeripheralOwner(turtle, side).attachFuel(1));
    }

    public GeoScannerPeripheral(IPocketAccess pocket) {
        this(new PocketPeripheralOwner(pocket));
    }

    private static List<Map<String, Object>> scan(IPeripheralOwner owner, int radius) {
        List<Map<String, Object>> result = new ArrayList<>();
        Vec3 center = owner.getCenterPos();
        Quaterniondc orientation = owner.getOrientation();
        Vector3d rpos = new Vector3d();
        ScanUtils.traverseBlocks(owner.getLevel(), center, radius, (state, pos) -> {
            Map<String, Object> data = new HashMap<>(LuaConverter.blockStateToLua(state));
            double x = pos.getX() + 0.5 - center.x;
            double y = pos.getY() + 0.5 - center.y;
            double z = pos.getZ() + 0.5 - center.z;
            data.put("x", x);
            data.put("y", y);
            data.put("z", z);
            orientation.transform(rpos.set(x, y, z));
            data.put("r", rpos.x);
            data.put("u", rpos.y);
            data.put("f", -rpos.z);
            result.add(data);
        });

        return result;
    }

    private static int estimateCost(int radius) {
        if (radius > SCAN_BLOCKS.getMaxCostRadius()) return -1;

        return SCAN_BLOCKS.getCost(SphereOperationContext.of(radius));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableGeoScanner.get();
    }

    @LuaFunction
    public final MethodResult cost(int radius) {
        int estimatedCost = estimateCost(radius);
        if (estimatedCost < 0) {
            return MethodResult.of(null, "Radius is exceed max value");
        }
        return MethodResult.of(estimatedCost);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult chunkAnalyze() throws LuaException {
        return withOperation(SCAN_BLOCKS, SCAN_BLOCKS.free(), null, ignored -> {
            Level level = getLevel();
            LevelChunk chunk = level.getChunkAt(getPos());
            ChunkPos chunkPos = chunk.getPos();
            Object2IntMap<ResourceLocation> data = new Object2IntOpenHashMap<>();
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); x++) {
                for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); z++) {
                    for (int y = level.getMinBuildHeight(); y < level.getHeight(); y++) {
                        BlockState block = chunk.getBlockState(pos.set(x, y, z));
                        if (!block.is(Tags.Blocks.ORES)) {
                            continue;
                        }
                        ResourceLocation name = BuiltInRegistries.BLOCK.getKey(block.getBlock());
                        if (name != null) {
                            data.put(name, data.getInt(name) + 1);
                        }
                    }
                }
            }
            return MethodResult.of(
                Map.ofEntries(
                    data.object2IntEntrySet()
                        .stream()
                        .map((entry) -> Map.entry(entry.getKey().toString(), entry.getIntValue()))
                        .toArray(Map.Entry[]::new)
                )
            );
        }, null);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult scan(@NotNull IArguments arguments) throws LuaException {
        int radius = arguments.getInt(0);
        return withOperation(SCAN_BLOCKS, new SphereOperationContext(radius), context -> {
            if (context.getRadius() > SCAN_BLOCKS.getMaxCostRadius()) {
                return MethodResult.of(null, "Radius is exceed max value");
            }
            return null;
        }, context -> MethodResult.of(scan(getPeripheralOwner(), context.getRadius())), null);
    }
}
