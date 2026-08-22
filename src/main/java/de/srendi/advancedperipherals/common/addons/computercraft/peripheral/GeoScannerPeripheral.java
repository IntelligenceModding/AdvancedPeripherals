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
import de.srendi.advancedperipherals.common.util.CoordUtil;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.ScanUtil;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3dc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

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
        this(PocketPeripheralOwner.of(pocket));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableGeoScanner.get();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult scanBlocks(@NotNull IArguments arguments) throws LuaException {
        int radius = arguments.getInt(0);
        return withOperation(SCAN_BLOCKS, new SphereOperationContext(radius), context -> {
            if (context.getRadius() > SCAN_BLOCKS.getMaxCostRadius()) {
                return MethodResult.of(null, "Radius is exceed max value");
            }
            return null;
        }, context -> MethodResult.of(scan(getPeripheralOwner(), context.getRadius())), null);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult chunkAnalyze(Optional<String> optFilter) throws LuaException {
        Predicate<BlockState> blockTester = (b) -> b.is(Tags.Blocks.ORES);
        String filter = optFilter.orElse(null);
        if (filter != null) {
            if (filter.length() > 0 && filter.charAt(0) == '#') {
                ResourceLocation id = ResourceLocation.tryParse(filter.substring(1));
                if (id == null) {
                    throw new LuaException("argument #1 is an invalid tag ID");
                }
                TagKey<Block> tag = TagKey.create(Registries.BLOCK, id);
                blockTester = (b) -> b.is(tag);
            } else {
                ResourceLocation id = ResourceLocation.tryParse(filter);
                if (id == null) {
                    throw new LuaException("argument #1 is an invalid block ID");
                }
                Block block = BuiltInRegistries.BLOCK.get(id);
                blockTester = block == null ? null : (b) -> b.is(block);
            }
        }
        Predicate<BlockState> blockTesterFinal = blockTester;
        return withOperation(SCAN_BLOCKS, SCAN_BLOCKS.free(), null, ignored -> {
            if (blockTesterFinal == null) {
                return MethodResult.of(Map.of());
            }
            Level level = getLevel();
            LevelChunk chunk = level.getChunkAt(getPhysicsBlockPos());
            Object2IntOpenHashMap<ResourceLocation> data = new Object2IntOpenHashMap<>();
            for (LevelChunkSection section : chunk.getSections()) {
                if (section.hasOnlyAir()) {
                    continue;
                }
                section.getStates().count((block, count) -> {
                    if (!blockTesterFinal.test(block)) {
                        return;
                    }
                    ResourceLocation name = BuiltInRegistries.BLOCK.getKey(block.getBlock());
                    if (name != null) {
                        data.addTo(name, count);
                    }
                });
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

    private static List<Map<String, Object>> scan(IPeripheralOwner owner, int radius) {
        List<Map<String, Object>> result = new ArrayList<>();
        Vec3 center = owner.getCenterPos();
        Matrix3dc orientation = owner.getOrientation();
        ScanUtil.traverseBlocks(owner.getLevel(), center, radius, (state, pos) -> {
            Map<String, Object> data = new HashMap<>(LuaConverter.blockStateToLua(state));
            double x = pos.getX() + 0.5 - center.x;
            double y = pos.getY() + 0.5 - center.y;
            double z = pos.getZ() + 0.5 - center.z;
            CoordUtil.putRelativeCoords(data, x, y, z, orientation);
            result.add(data);
        });

        Vec3 center2 = owner.getPhysicsPos();
        if (!center2.equals(center)) {
            Matrix3dc orientation2 = owner.getPhysicsOrientation();
            ScanUtil.traverseBlocks(owner.getLevel(), center, radius, (state, pos) -> {
                Map<String, Object> data = new HashMap<>(LuaConverter.blockStateToLua(state));
                double x = pos.getX() + 0.5 - center.x;
                double y = pos.getY() + 0.5 - center.y;
                double z = pos.getZ() + 0.5 - center.z;
                CoordUtil.putRelativeCoords(data, x, y, z, orientation2);
                data.put("notOnShip", true);
                result.add(data);
            });
        }

        return result;
    }
}
