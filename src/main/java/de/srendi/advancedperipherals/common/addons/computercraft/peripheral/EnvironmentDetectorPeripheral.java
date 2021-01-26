package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EnvironmentDetectorPeripheral implements IPeripheral {

    private String type;
    private TileEntity entity;

    List<IComputerAccess> connectedComputers = new ArrayList<>();

    public EnvironmentDetectorPeripheral(String type, TileEntity entity) {
        this.type = type;
        this.entity = entity;
    }

    @NotNull
    @Override
    public String getType() {
        return type;
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        connectedComputers.add(computer);
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        connectedComputers.remove(computer);
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return iPeripheral == this;
    }

    @LuaFunction(mainThread = true)
    public final String getBiome() {
        String biomeName = entity.getWorld().getBiome(entity.getPos()).getRegistryName().toString();
        String[] biome = biomeName.split(":");
        return biome[1];
    }

    @LuaFunction(mainThread = true)
    public final int getSkyLightLevel() {
        return entity.getWorld().getLightFor(LightType.SKY, entity.getPos().add(0, 1, 0));
    }

    @LuaFunction(mainThread = true)
    public final int getBlockLightLevel() {
        return entity.getWorld().getLightFor(LightType.BLOCK, entity.getPos().add(0, 1, 0));
    }

    @LuaFunction(mainThread = true)
    public final int getDayLightLevel() {
        World world = entity.getWorld();
        int i = world.getLightFor(LightType.SKY, entity.getPos().add(0, 1, 0)) - world.getSkylightSubtracted();
        float f = world.getCelestialAngleRadians(1.0F);
        if (i > 0) {
            float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
            f = f + (f1 - f) * 0.2F;
            i = Math.round((float)i * MathHelper.cos(f));
        }
        i = MathHelper.clamp(i, 0, 15);
        return i;
    }

    @LuaFunction(mainThread = true)
    public final long getTime() {
        return entity.getWorld().getDayTime();
    }

    @LuaFunction(mainThread = true)
    public final boolean isSlimeChunk() {
        ChunkPos chunkPos = new ChunkPos(entity.getPos());
        return (SharedSeedRandom.seedSlimeChunk(chunkPos.x, chunkPos.z, ((ISeedReader) entity.getWorld()).getSeed(), 987234911L).nextInt(10) == 0);
    }

    @LuaFunction(mainThread = true)
    public final String getDimensionProvider() {
        return entity.getWorld().getDimensionKey().getLocation().getNamespace();
    }

    @LuaFunction(mainThread = true)
    public final String getDimensionName() {
        return entity.getWorld().getDimensionKey().getLocation().getPath();
    }

    @LuaFunction(mainThread = true)
    public final String getDimensionPaN() {
        return entity.getWorld().getDimensionKey().getLocation().getNamespace() + ":" + entity.getWorld().getDimensionKey().getLocation().getPath();
    }

    @LuaFunction(mainThread = true)
    public final boolean isDimension(String dimension) {
        return entity.getWorld().getDimensionKey().getLocation().getPath().equals(dimension);
    }

    @LuaFunction(mainThread = true)
    public final Set<String> listDimensions() {
        Set<String> dimensions = new HashSet<>();
        ServerLifecycleHooks.getCurrentServer().getWorlds().forEach(serverWorld->dimensions.add(serverWorld.getDimensionKey().getLocation().getPath()));
        return dimensions;
    }

    @LuaFunction(mainThread = true)
    public final int getMoonId() {
        //for(int value : getCurrentMoonPhase().keySet()) {
          //  return value;
        //}
        return getCurrentMoonPhase().keySet().toArray(new Integer[0])[0];
    }

    @LuaFunction(mainThread = true)
    public final String getMoonName() {
        String[] name = getCurrentMoonPhase().values().toArray(new String[0]);
        return name[0];
    }

    private Map<Integer, String> getCurrentMoonPhase() {
        Map<Integer, String> moon = new HashMap<>();
        if (entity.getWorld().getDimensionKey().getLocation().getPath().equals("overworld")) {
            switch (entity.getWorld().getMoonPhase()) {
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

   /* @LuaFunction(mainThread = true)
    public final boolean isMoon(int phase) {
        return getCurrentMoonPhase().containsKey(phase);
    }*/

    private HashMap<Integer, String> getMoonPhases() {
        HashMap<Integer, String> moon = new HashMap<>();
        moon.put(1, "Waning gibbous");
        moon.put(2, "Third guarter");
        moon.put(3, "Wanning crescent");
        moon.put(4, "New moon");
        moon.put(5, "Waxing crescent");
        moon.put(6, "First quarter");
        moon.put(7, "Waxing gibbous");
        moon.put(8, "Full moon");
        return moon;
    }

    @LuaFunction(mainThread = true)
    public final boolean isRaining() {
        return entity.getWorld().getRainStrength(0) > 0;
    }

    @LuaFunction(mainThread = true)
    public final boolean isThunder() {
        return entity.getWorld().getThunderStrength(0) > 0;
    }

    @LuaFunction(mainThread = true)
    public final boolean isSunny() {
        return entity.getWorld().getThunderStrength(0) < 1 && entity.getWorld().getRainStrength(0) < 1;
    }
}