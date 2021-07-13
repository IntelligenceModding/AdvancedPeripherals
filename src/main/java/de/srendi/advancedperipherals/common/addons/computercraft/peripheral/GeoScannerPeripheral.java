package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.google.common.math.IntMath;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.FuelConsumingPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.base.OperationPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;

public class GeoScannerPeripheral extends FuelConsumingPeripheral {
	/*
	Highly inspired by https://github.com/SquidDev-CC/plethora/ BlockScanner
	*/

    private final static String SCAN_OPERATION = "scan";

    public GeoScannerPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    public GeoScannerPeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
    }

    public GeoScannerPeripheral(String type, IPocketAccess pocket) {
        super(type, pocket);
    }

    @Override
    protected int getRawCooldown(String name) {
        return AdvancedPeripheralsConfig.geoScannerMinScanPeriod;
    }

    @Override
    protected int getMaxFuelConsumptionRate() {
        return 1;
    }

    @Override
    protected int _getFuelConsumptionRate() {
        return 1;
    }

    @Override
    protected void _setFuelConsumptionRate(int rate) {
    }

    private static List<Map<String, ?>> scan(World world, int x, int y, int z, int radius) {
        List<Map<String, ?>> result = new ArrayList<>();
        for (int oX = x - radius; oX <= x + radius; oX++) {
            for (int oY = y - radius; oY <= y + radius; oY++) {
                for (int oZ = z - radius; oZ <= z + radius; oZ++) {
                    BlockPos subPos = new BlockPos(oX, oY, oZ);
                    BlockState blockState = world.getBlockState(subPos);
                    Block block = blockState.getBlock();

                    HashMap<String, Object> data = new HashMap<>(6);
                    data.put("x", oX - x);
                    data.put("y", oY - y);
                    data.put("z", oZ - z);

                    ResourceLocation name = block.getRegistryName();
                    data.put("name", name == null ? "unknown" : name.toString());

                    data.put("tags", block.getTags());

                    result.add(data);
                }
            }
        }

        return result;
    }

    private static int estimateCost(int radius) {
        if (radius <= AdvancedPeripheralsConfig.geoScannerMaxFreeRadius) {
            return 0;
        }
        if (radius > AdvancedPeripheralsConfig.geoScannerMaxCostRadius) {
            return -1;
        }
        int freeBlockCount = IntMath.pow(2 * AdvancedPeripheralsConfig.geoScannerMaxFreeRadius + 1, 3);
        int allBlockCount = IntMath.pow(2 * radius + 1, 3);
        return (int) Math.floor((allBlockCount - freeBlockCount) * AdvancedPeripheralsConfig.geoScannerExtraBlockCost);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableGeoScanner;
    }

    @LuaFunction
    public final MethodResult cost(int radius) {
        int estimatedCost = estimateCost(radius);
        if (estimatedCost < 0) {
            return MethodResult.of(null, "Radius is exceed max value");
        }
        return MethodResult.of(estimatedCost);
    }

    @LuaFunction
    public final long getScanCooldown() {
        return getCurrentCooldown(SCAN_OPERATION);
    }

    public Map<String, Object> getPeripheralConfiguration() {
        Map<String, Object> result = super.getPeripheralConfiguration();
        result.put("freeRadius", AdvancedPeripheralsConfig.geoScannerMaxCostRadius);
        result.put("scanPeriod", AdvancedPeripheralsConfig.geoScannerMinScanPeriod);
        result.put("costRadius", AdvancedPeripheralsConfig.geoScannerMaxCostRadius);
        result.put("blockCost", AdvancedPeripheralsConfig.geoScannerExtraBlockCost);
        return result;
    }

    @LuaFunction
    public final MethodResult chunkAnalyze() {
        Optional<MethodResult> checkResult = cooldownCheck(SCAN_OPERATION);
        if (checkResult.isPresent()) return checkResult.get();
        World world = getWorld();
        Chunk chunk = world.getChunkAt(getPos());
        ChunkPos chunkPos = chunk.getPos();
        HashMap<String, Integer> data = new HashMap<>();
        for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); x++) {
            for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); z++) {
                for (int y = 0; y < 256; y++) {
                    BlockState block = chunk.getBlockState(new BlockPos(x, y, z));
                    ResourceLocation name = block.getBlock().getRegistryName();
                    if (name != null) {
                        String localizedName = name.toString();
                        if (localizedName.toLowerCase().contains("ore")) {
                            data.put(name.toString(), data.getOrDefault(name.toString(), 0) + 1);
                        }
                    }
                }
            }
        }
        trackOperation(SCAN_OPERATION);
        return MethodResult.of(data);
    }

    @LuaFunction
    public final MethodResult scan(@Nonnull IArguments arguments) throws LuaException {
        int radius = arguments.getInt(0);
        Optional<MethodResult> checkResult = cooldownCheck(SCAN_OPERATION);
        if (checkResult.isPresent()) return checkResult.get();
        BlockPos pos = getPos();
        World world = getWorld();
        if (radius > AdvancedPeripheralsConfig.geoScannerMaxCostRadius) {
            return MethodResult.of(null, "Radius is exceed max value");
        }
        if (radius > AdvancedPeripheralsConfig.geoScannerMaxFreeRadius) {
            if (owner.getFuelMaxCount() == 0) {
                return MethodResult.of(null, "Radius is exceed max value");
            }
            int cost = estimateCost(radius);
            if (!consumeFuel(cost, false)) {
                return MethodResult.of(null, String.format("Not enough fuel, %d needed", cost));
            }
        }
        List<Map<String, ?>> scanResult = scan(world, pos.getX(), pos.getY(), pos.getZ(), radius);
        trackOperation(SCAN_OPERATION);
        return MethodResult.of(scanResult);
    }
}
