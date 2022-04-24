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
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SphereOperation.SCAN_BLOCKS;

public class GeoScannerPeripheral extends BasePeripheral<IPeripheralOwner> {
	/*
	Highly inspired by https://github.com/SquidDev-CC/plethora/ BlockScanner
	*/

    public static final String TYPE = "geoScanner";

    protected GeoScannerPeripheral(IPeripheralOwner owner) {
        super(TYPE, owner);
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

    private static List<Map<String, ?>> scan(Level level, BlockPos center, int radius) {
        List<Map<String, ?>> result = new ArrayList<>();
        ScanUtils.relativeTraverseBlocks(level, center, radius, (state, pos) -> {
            HashMap<String, Object> data = new HashMap<>(6);

            data.put("x", pos.getX());
            data.put("y", pos.getY());
            data.put("z", pos.getZ());

            Block block = state.getBlock();
            ResourceLocation name = block.getRegistryName();
            data.put("name", name == null ? "unknown" : name.toString());
            data.put("tags", LuaConverter.tagsToList(() -> block.builtInRegistryHolder().tags()));

            result.add(data);
        });

        return result;
    }

    private static int estimateCost(int radius) {
        if (radius > SCAN_BLOCKS.getMaxCostRadius())
            return -1;

        return SCAN_BLOCKS.getCost(SphereOperationContext.of(radius));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.ENABLE_GEO_SCANNER.get();
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
            HashMap<String, Integer> data = new HashMap<>();
            for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); x++) {
                for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); z++) {
                    for (int y = 0; y < 256; y++) {
                        BlockState block = chunk.getBlockState(new BlockPos(x, y, z));
                        ResourceLocation name = block.getBlock().getRegistryName();
                        if (name != null) {
                            if (block.is(Tags.Blocks.ORES)) {
                                data.put(name.toString(), data.getOrDefault(name.toString(), 0) + 1);
                            }
                        }
                    }
                }
            }
            return MethodResult.of(data);
        }, null);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult scan(@Nonnull IArguments arguments) throws LuaException {
        int radius = arguments.getInt(0);
        return withOperation(SCAN_BLOCKS, new SphereOperationContext(radius), context -> {
            if (context.getRadius() > SCAN_BLOCKS.getMaxCostRadius()) {
                return MethodResult.of(null, "Radius is exceed max value");
            }
            return null;
        }, context -> MethodResult.of(scan(getLevel(), getPos(), context.getRadius())), null);
    }
}
