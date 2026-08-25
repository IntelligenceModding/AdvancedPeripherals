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
import de.srendi.advancedperipherals.common.util.LuaArgsHelper;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralPlugin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SphereOperation.SCAN_ENTITIES;

public class EnvironmentDetectorPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "environment_detector";
    private static final List<Function<IPeripheralOwner, IPeripheralPlugin>> PERIPHERAL_PLUGINS = new ArrayList<>();

    protected EnvironmentDetectorPeripheral(IPeripheralOwner owner) {
        super(PERIPHERAL_TYPE, owner);
        owner.attachOperation(SCAN_ENTITIES);
        for (Function<IPeripheralOwner, IPeripheralPlugin> plugin : PERIPHERAL_PLUGINS) {
            addPlugin(plugin.apply(owner));
        }
    }

    public EnvironmentDetectorPeripheral(PeripheralBlockEntity<?> tileEntity) {
        this(new BlockEntityPeripheralOwner<>(tileEntity).attachFuel());
    }

    public EnvironmentDetectorPeripheral(ITurtleAccess turtle, TurtleSide side) {
        this(new TurtlePeripheralOwner(turtle, side).attachFuel(1));
    }

    public EnvironmentDetectorPeripheral(IPocketAccess pocket) {
        this(PocketPeripheralOwner.of(pocket));
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
        Optional<ResourceKey<Biome>> biome = getLevel().getBiome(this.getPhysicsBlockPos()).unwrapKey();
        return biome.map(biomeResourceKey -> biomeResourceKey.location().toString()).orElse("unknown");
    }

    @LuaFunction(mainThread = true)
    public final int getBlockLightLevel() {
        return getLevel().getBrightness(LightLayer.BLOCK, this.getPhysicsBlockPos().offset(0, 1, 0));
    }

    @LuaFunction(mainThread = true)
    public final int getSkyLightLevel() {
        return getLevel().getBrightness(LightLayer.SKY, this.getPhysicsBlockPos().offset(0, 1, 0));
    }

    @LuaFunction(mainThread = true)
    public final int getDayLightLevel() {
        Level level = getLevel();
        int i = level.getBrightness(LightLayer.SKY, this.getPhysicsBlockPos().offset(0, 1, 0)) - level.getSkyDarken();
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
        ChunkPos chunkPos = new ChunkPos(this.getPhysicsBlockPos());
        return WorldgenRandom.seedSlimeChunk(chunkPos.x, chunkPos.z, ((WorldGenLevel) getLevel()).getSeed(), 987234911L).nextInt(10) == 0;
    }

    @LuaFunction(mainThread = true)
    public final String getDimension() {
        return getLevel().dimension().location().toString();
    }

    @LuaFunction(mainThread = true)
    public final List<String> listDimensions() {
        return getLevel().getServer().levelKeys().stream().map(ResourceKey::location).map(ResourceLocation::toString).toList();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getMoon() {
        Pair<Integer, String> moon = getCurrentMoonPhase();
        return MethodResult.of(moon.left(), moon.right());
    }

    private Pair<Integer, String> getCurrentMoonPhase() {
        if (getLevel().dimension() == Level.OVERWORLD) {
            return switch (getLevel().getMoonPhase()) {
                case 0 -> Pair.of(0, "Full moon");
                case 1 -> Pair.of(1, "Waning gibbous");
                case 2 -> Pair.of(2, "Third quarter");
                case 3 -> Pair.of(3, "Wanning crescent");
                case 4 -> Pair.of(4, "New moon");
                case 5 -> Pair.of(5, "Waxing crescent");
                case 6 -> Pair.of(6, "First quarter");
                case 7 -> Pair.of(7, "Waxing gibbous");
                default -> Pair.of(0, "What is a moon");
            };
        }
        // aren't we in the overworld?
        return Pair.of(0, "Moon.exe not found...");
    }

    @LuaFunction(mainThread = true)
    public final boolean isRaining() {
        return getLevel().getRainLevel(1) > 0;
    }

    @LuaFunction(mainThread = true)
    public final boolean isThunder() {
        return getLevel().getThunderLevel(1) > 0;
    }

    @LuaFunction(mainThread = true)
    public final boolean isSunny() {
        return getLevel().getThunderLevel(1) < 1 && getLevel().getRainLevel(1) < 1;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult scanEntities(@NotNull IArguments arguments) throws LuaException {
        int radius = arguments.getInt(0);
        LuaArgsHelper.Args uargs = LuaArgsHelper.getUnorderedArgs(arguments, 1, Boolean.class, String.class);
        boolean detailed = uargs.get(Boolean.class, false);
        String filter = uargs.get(String.class);

        Predicate<Entity> entityTester = (entity) -> entity.isAlive() && entity instanceof LivingEntity;
        if (filter != null) {
            if (filter.length() > 0 && filter.charAt(0) == '#') {
                ResourceLocation id = ResourceLocation.tryParse(filter.substring(1));
                if (id == null) {
                    throw new LuaException("argument #1 is an invalid tag ID");
                }
                TagKey<EntityType<?>> tag = TagKey.create(Registries.ENTITY_TYPE, id);
                entityTester = (entity) -> entity.isAlive() && entity.getType().is(tag);
            } else {
                ResourceLocation id = ResourceLocation.tryParse(filter);
                if (id == null) {
                    throw new LuaException("argument #1 is an invalid block ID");
                }
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(id);
                entityTester = entityType == null ? null : (entity) -> entity.isAlive() && entity.getType() == entityType;
            }
        }
        final Predicate<Entity> finalEntityTester = entityTester;

        return withOperation(SCAN_ENTITIES, new SphereOperationContext(radius), context -> {
            return context.getRadius() > SCAN_ENTITIES.getMaxCostRadius() ? MethodResult.of(null, "Radius exceeds max value") : null;
        }, context -> {
            IPeripheralOwner owner = this.getPeripheralOwner();
            Vec3 pos = owner.getPhysicsPos();
            AABB box = new AABB(pos, pos).inflate(context.getRadius() + 0.5);
            LuaConverter.EntityConverter.Context convContext = LuaConverter.entityContextBuilder()
                .detailed(detailed)
                .position(pos)
                .orientation(owner.getOrientation())
                .build();
            List<Map<String, Object>> entities = getLevel()
                .getEntities((Entity) null, box, finalEntityTester)
                .stream()
                .map(entity -> LuaConverter.entityToLua(entity, convContext))
                .toList();
            return MethodResult.of(entities);
        }, null);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult canSleepHere() {
        return MethodResult.of(!getLevel().isDay());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult canPlayerSleep(String playername) {
        Player player = getLevel().getServer().getPlayerList().getPlayerByName(playername);
        if (player == null) {
            return MethodResult.of(false, "player_not_online");
        }

        if (!player.level().dimensionType().bedWorks()) {
            return MethodResult.of(false, "not_allowed_in_dimension");
        }

        CanContinueSleepingEvent evt = new CanContinueSleepingEvent(player, null);
        NeoForge.EVENT_BUS.post(evt);

        if (evt.mayContinueSleeping()) {
            return MethodResult.of(!player.level().isDay());
        } else {
            return MethodResult.of(true);
        }
    }
}
