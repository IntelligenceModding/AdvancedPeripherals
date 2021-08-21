package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.FuelConsumingPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.IPeripheralOperation;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SphereOperationContext;
import de.srendi.advancedperipherals.common.addons.mekanism.Mekanism;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.*;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SphereOperation.SCAN_ENTITIES;

public class EnvironmentDetectorPeripheral extends FuelConsumingPeripheral {

    public static final String TYPE = "environmentDetector";

    public EnvironmentDetectorPeripheral(PeripheralTileEntity<?> tileEntity) {
        super(TYPE, tileEntity);
    }

    public EnvironmentDetectorPeripheral(ITurtleAccess turtle, TurtleSide side) {
        super(TYPE, turtle, side);
    }

    public EnvironmentDetectorPeripheral(IPocketAccess pocket) {
        super(TYPE, pocket);
    }

    private static int estimateCost(int radius) {
        if (radius <= SCAN_ENTITIES.getMaxFreeRadius()) {
            return 0;
        }
        if (radius > SCAN_ENTITIES.getMaxCostRadius()) {
            return -1;
        }
        return SCAN_ENTITIES.getCost(SphereOperationContext.of(radius));
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

    @Override
    public List<IPeripheralOperation<?>> possibleOperations() {
        return Collections.singletonList(SCAN_ENTITIES);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableEnvironmentDetector;
    }

    @LuaFunction(mainThread = true)
    public final String getBiome() {
        String biomeName = getWorld().getBiome(getPos()).getRegistryName().toString();
        String[] biome = biomeName.split(":");
        return biome[1];
    }

    @LuaFunction(mainThread = true)
    public final int getSkyLightLevel() {
        return getWorld().getBrightness(LightType.SKY, getPos().offset(0, 1, 0));
    }

    @LuaFunction(mainThread = true)
    public final int getBlockLightLevel() {
        return getWorld().getBrightness(LightType.BLOCK, getPos().offset(0, 1, 0));
    }

    @LuaFunction(mainThread = true)
    public final int getDayLightLevel() {
        World world = getWorld();
        int i = world.getBrightness(LightType.SKY, getPos().offset(0, 1, 0)) - world.getSkyDarken();
        float f = world.getSunAngle(1.0F);
        if (i > 0) {
            float f1 = f < (float) Math.PI ? 0.0F : ((float) Math.PI * 2F);
            f = f + (f1 - f) * 0.2F;
            i = Math.round((float) i * MathHelper.cos(f));
        }
        i = MathHelper.clamp(i, 0, 15);
        return i;
    }

    @LuaFunction(mainThread = true)
    public final long getTime() {
        return getWorld().getDayTime();
    }

    @LuaFunction(mainThread = true)
    public final boolean isSlimeChunk() {
        ChunkPos chunkPos = new ChunkPos(getPos());
        return (SharedSeedRandom.seedSlimeChunk(chunkPos.x, chunkPos.z, ((ISeedReader) getWorld()).getSeed(), 987234911L).nextInt(10) == 0);
    }

    @LuaFunction(mainThread = true)
    public final String getDimensionProvider() {
        return getWorld().dimension().getRegistryName().getNamespace();
    }

    @LuaFunction(mainThread = true)
    public final String getDimensionName() {
        return getWorld().dimension().getRegistryName().getPath();
    }

    @LuaFunction(mainThread = true)
    public final String getDimensionPaN() {
        return getWorld().dimension().getRegistryName().toString();
    }

    @LuaFunction(mainThread = true)
    public final boolean isDimension(String dimension) {
        return getWorld().dimension().getRegistryName().getPath().equals(dimension);
    }

    @LuaFunction(mainThread = true)
    public final Set<String> listDimensions() {
        Set<String> dimensions = new HashSet<>();
        ServerLifecycleHooks.getCurrentServer().getAllLevels().forEach(serverWorld -> dimensions.add(serverWorld.dimension().getRegistryName().getPath()));
        return dimensions;
    }

    @LuaFunction(mainThread = true)
    public final int getMoonId() {
        return getCurrentMoonPhase().keySet().toArray(new Integer[0])[0];
    }

    @LuaFunction(mainThread = true)
    public final String getMoonName() {
        String[] name = getCurrentMoonPhase().values().toArray(new String[0]);
        return name[0];
    }

   /* @LuaFunction(mainThread = true)
    public final boolean isMoon(int phase) {
        return getCurrentMoonPhase().containsKey(phase);
    }*/

    private Map<Integer, String> getCurrentMoonPhase() {
        Map<Integer, String> moon = new HashMap<>();
        if (getWorld().dimension().getRegistryName().getPath().equals("overworld")) {
            switch (getWorld().getMoonPhase()) {
                case 0:
                    moon.put(0, "Full moon");
                    break;
                case 1:
                    moon.put(1, "Waning gibbous");
                    break;
                case 2:
                    moon.put(2, "Third quarter");
                    break;
                case 3:
                    moon.put(3, "Wanning crescent");
                    break;
                case 4:
                    moon.put(4, "New moon");
                    break;
                case 5:
                    moon.put(5, "Waxing crescent");
                    break;
                case 6:
                    moon.put(6, "First quarter");
                    break;
                case 7:
                    moon.put(7, "Waxing gibbous");
                    break;
                default:
                    //should never happen
                    moon.put(0, "What is a moon");
            }
        } else {
            //Yay, easter egg
            //Returns when the function is not used in the overworld
            moon.put(0, "Moon.exe not found...");
        }
        return moon;
    }

    @LuaFunction(mainThread = true)
    public final boolean isRaining() {
        return getWorld().getRainLevel(0) > 0;
    }

    @LuaFunction(mainThread = true)
    public final boolean isThunder() {
        return getWorld().getRainLevel(0) > 0;
    }

    @LuaFunction(mainThread = true)
    public final boolean isSunny() {
        return getWorld().getThunderLevel(0) < 1 && getWorld().getRainLevel(0) < 1;
    }

    @LuaFunction(mainThread = true)
    public final Object getRadiation() {
        return ModList.get().isLoaded("mekanism") ? Mekanism.getRadiation(getWorld(), getPos()) : null;
    }

    @LuaFunction(mainThread = true)
    public final double getRadiationRaw() {
        return ModList.get().isLoaded("mekanism") ? Mekanism.getRadiationRaw(getWorld(), getPos()) : 0D;
    }

    @LuaFunction
    public final MethodResult scanEntities(@Nonnull IComputerAccess access, @Nonnull IArguments arguments) throws LuaException {
        int radius = arguments.getInt(0);
        Optional<MethodResult> checkResult = cooldownCheck(SCAN_ENTITIES);
        if (checkResult.isPresent()) return checkResult.get();
        BlockPos pos = getPos();
        if (radius > SCAN_ENTITIES.getMaxCostRadius()) {
            return MethodResult.of(null, "Radius is exceed max value");
        }
        if (radius > SCAN_ENTITIES.getMaxFreeRadius()) {
            if (owner.getFuelMaxCount() == 0) {
                return MethodResult.of(null, "Radius is exceed max value");
            }
            int cost = estimateCost(radius);
            if (!consumeFuel(cost, false)) {
                return MethodResult.of(null, String.format("Not enough fuel, %d needed", cost));
            }
        }
        AxisAlignedBB box = new AxisAlignedBB(pos);
        List<Map<String, Object>> entities = new ArrayList<>();
        getWorld().getEntities((Entity) null, box.inflate(radius), entity -> entity instanceof LivingEntity).forEach(
                entity -> entities.add(LuaConverter.completeEntityWithPositionToLua(entity, ItemStack.EMPTY, pos))
        );
        trackOperation(SCAN_ENTITIES, SCAN_ENTITIES.free());
        return MethodResult.of(entities);
    }

    @LuaFunction
    public final MethodResult scanCost(int radius) {
        int estimatedCost = estimateCost(radius);
        if (estimatedCost < 0) {
            return MethodResult.of(null, "Radius is exceed max value");
        }
        return MethodResult.of(estimatedCost);
    }
}