package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
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
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralPlugin;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SphereOperation.SCAN_ENTITIES;

public class EnvironmentDetectorPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "environment_detector";
    private static final List<Function<IPeripheralOwner, IPeripheralPlugin>> PERIPHERAL_PLUGINS = new LinkedList<>();

    protected EnvironmentDetectorPeripheral(IPeripheralOwner owner) {
        super(PERIPHERAL_TYPE, owner);
        owner.attachOperation(SCAN_ENTITIES);
        for (Function<IPeripheralOwner, IPeripheralPlugin> plugin : PERIPHERAL_PLUGINS)
            addPlugin(plugin.apply(owner));
    }

    public EnvironmentDetectorPeripheral(PeripheralBlockEntity<?> tileEntity) {
        this(new BlockEntityPeripheralOwner<>(tileEntity).attachFuel());
    }

    public EnvironmentDetectorPeripheral(ITurtleAccess turtle, TurtleSide side) {
        this(new TurtlePeripheralOwner(turtle, side).attachFuel(1));
    }

    public EnvironmentDetectorPeripheral(IPocketAccess pocket) {
        this(new PocketPeripheralOwner(pocket));
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

    public static void addIntegrationPlugin(Function<IPeripheralOwner, IPeripheralPlugin> plugin) {
        PERIPHERAL_PLUGINS.add(plugin);
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableEnergyDetector.get();
    }

    @LuaFunction(mainThread = true)
    public final String getBiome() {
        Optional<ResourceKey<Biome>> biome = getLevel().getBiome(getPos()).unwrapKey();
        return biome.map(biomeResourceKey -> biomeResourceKey.location().toString()).orElse("unknown");
    }

    @LuaFunction(mainThread = true)
    public final int getSkyLightLevel() {
        return getLevel().getBrightness(LightLayer.SKY, getPos().offset(0, 1, 0));
    }

    @LuaFunction(mainThread = true)
    public final int getBlockLightLevel() {
        return getLevel().getBrightness(LightLayer.BLOCK, getPos().offset(0, 1, 0));
    }

    @LuaFunction(mainThread = true)
    public final int getDayLightLevel() {
        Level level = getLevel();
        int i = level.getBrightness(LightLayer.SKY, getPos().offset(0, 1, 0)) - level.getSkyDarken();
        float f = level.getSunAngle(1.0F);
        if (i > 0) {
            float f1 = f < (float) Math.PI ? 0.0F : ((float) Math.PI * 2F);
            f = f + (f1 - f) * 0.2F;
            i = Math.round(i * Mth.cos(f));
        }
        i = Mth.clamp(i, 0, 15);
        return i;
    }

    @LuaFunction(mainThread = true)
    public final long getTime() {
        return getLevel().getDayTime();
    }

    @LuaFunction(mainThread = true)
    public final boolean isSlimeChunk() {
        ChunkPos chunkPos = new ChunkPos(getPos());
        return WorldgenRandom.seedSlimeChunk(chunkPos.x, chunkPos.z, ((WorldGenLevel) getLevel()).getSeed(), 987234911L).nextInt(10) == 0;
    }

    @LuaFunction(mainThread = true)
    public final String getDimension() {
        return getLevel().dimension().location().toString();
    }

    @LuaFunction(mainThread = true)
    public final boolean isDimension(String dimension) {
        return getLevel().dimension().location().toString().equals(dimension);
    }

    @LuaFunction(mainThread = true)
    public final Set<String> listDimensions() {
        Set<String> dimensions = new HashSet<>();
        ServerLifecycleHooks.getCurrentServer().getAllLevels().forEach(serverWorld -> dimensions.add(serverWorld.dimension().location().getPath()));
        return dimensions;
    }

    @LuaFunction(mainThread = true)
    public final int getMoonId() {
        return getCurrentMoonPhase().keySet().toArray(new Integer[0])[0];
    }

    @LuaFunction(mainThread = true)
    public final boolean isMoon(int phase) {
        return getCurrentMoonPhase().containsKey(phase);
    }

    @LuaFunction(mainThread = true)
    public final String getMoonName() {
        String[] name = getCurrentMoonPhase().values().toArray(new String[0]);
        return name[0];
    }

    private Map<Integer, String> getCurrentMoonPhase() {
        Map<Integer, String> moon = new HashMap<>();
        if (getLevel().dimension() == Level.OVERWORLD) {
            switch (getLevel().getMoonPhase()) {
                case 0 -> moon.put(0, "Full moon");
                case 1 -> moon.put(1, "Waning gibbous");
                case 2 -> moon.put(2, "Third quarter");
                case 3 -> moon.put(3, "Wanning crescent");
                case 4 -> moon.put(4, "New moon");
                case 5 -> moon.put(5, "Waxing crescent");
                case 6 -> moon.put(6, "First quarter");
                case 7 -> moon.put(7, "Waxing gibbous");
                default -> moon.put(0, "What is a moon");
            }
        } else {
            // aren't we in the overworld?
            moon.put(0, "Moon.exe not found...");
        }
        return moon;
    }

    @LuaFunction(mainThread = true)
    public final boolean isRaining() {
        return getLevel().getRainLevel(0) > 0;
    }

    @LuaFunction(mainThread = true)
    public final boolean isThunder() {
        return getLevel().getThunderLevel(0) > 0;
    }

    @LuaFunction(mainThread = true)
    public final boolean isSunny() {
        return getLevel().getThunderLevel(0) < 1 && getLevel().getRainLevel(0) < 1;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult scanEntities(@NotNull IComputerAccess access, @NotNull IArguments arguments) throws LuaException {
        int radius = arguments.getInt(0);
        return withOperation(SCAN_ENTITIES, new SphereOperationContext(radius), context -> {
            if (radius > SCAN_ENTITIES.getMaxCostRadius())
                return MethodResult.of(null, "Radius exceeds max value");
            return null;
        }, context -> {
            BlockPos pos = owner.getPos();
            AABB box = new AABB(pos);
            List<Map<String, Object>> entities = new ArrayList<>();
            getLevel().getEntities((Entity) null, box.inflate(radius), LivingEntity.class::isInstance).forEach(entity -> entities.add(LuaConverter.completeEntityWithPositionToLua(entity, ItemStack.EMPTY, pos)));
            return MethodResult.of(entities);
        }, null);
    }

    @LuaFunction
    public final MethodResult scanCost(int radius) {
        int estimatedCost = estimateCost(radius);
        if (estimatedCost < 0)
            return MethodResult.of(null, "Radius exceeds max value");
        return MethodResult.of(estimatedCost);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult canSleepHere() {
        return MethodResult.of(!getLevel().isDay());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult canSleepPlayer(String playername) {
        Player player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(playername);
        if (player == null)
            return MethodResult.of(false, "player_not_online");

        if (!player.level.dimensionType().bedWorks())
            return MethodResult.of(false, "not_allowed_in_dimension");

        SleepingTimeCheckEvent evt = new SleepingTimeCheckEvent(player, Optional.empty());
        MinecraftForge.EVENT_BUS.post(evt);

        Event.Result canContinueSleep = evt.getResult();
        if (canContinueSleep == Event.Result.DEFAULT) {
            return MethodResult.of(!player.level.isDay());
        } else {
            return MethodResult.of(canContinueSleep == Event.Result.ALLOW);
        }
    }
}
