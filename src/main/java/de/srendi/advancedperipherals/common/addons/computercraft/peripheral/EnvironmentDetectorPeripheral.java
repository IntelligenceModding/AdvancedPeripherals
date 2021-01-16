package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.client.Minecraft;
import net.minecraft.item.BucketItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.LightType;
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

    @Nullable
    @Override
    public Object getTarget() {
        return null;
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return iPeripheral == this;
    }

    @LuaFunction(mainThread = true)
    public String getBiome() {
        String biomeName = entity.getWorld().getBiome(entity.getPos()).getRegistryName().toString();
        String[] biome = biomeName.split(":");
        return biome[1];
    }

    @LuaFunction(mainThread = true)
    public int getLightLevel() {
        return entity.getWorld().getLightFor(LightType.SKY, entity.getPos().add(0, 1, 0));
    }

    @LuaFunction(mainThread = true)
    public long getTime() {
        return entity.getWorld().getDayTime();
    }

    @LuaFunction(mainThread = true)
    public boolean isSlimeChunk() {
        ChunkPos chunkPos = new ChunkPos(entity.getPos());
        return (SharedSeedRandom.seedSlimeChunk(chunkPos.x, chunkPos.z, ((ISeedReader) entity.getWorld()).getSeed(), 987234911L).nextInt(10) == 0);
    }

    @LuaFunction(mainThread = true)
    public String getDimensionProvider() {
        return entity.getWorld().getDimensionKey().getLocation().getNamespace();
    }

    @LuaFunction(mainThread = true)
    public String getDimensionName() {
        return entity.getWorld().getDimensionKey().getLocation().getPath();
    }

    @LuaFunction(mainThread = true)
    public String getDimensionPaN() {
        return entity.getWorld().getDimensionKey().getLocation().getNamespace() + ":" + entity.getWorld().getDimensionKey().getLocation().getPath();
    }

    @LuaFunction(mainThread = true)
    public boolean isDimension(String dimension) {
        return entity.getWorld().getDimensionKey().getLocation().getPath().equals(dimension);
    }

    @LuaFunction(mainThread = true)
    public Set<String> listDimensions() {
        Set<String> dimensions = new HashSet<>();
        ServerLifecycleHooks.getCurrentServer().getWorlds().forEach(serverWorld->dimensions.add(serverWorld.getDimensionKey().getLocation().getPath()));
        return dimensions;
    }

    @LuaFunction(mainThread = true)
    public Object getMoon() {
        return getCurrentMoonPhase();
    }

    private HashMap<Integer, String> getCurrentMoonPhase() {
        HashMap<Integer, String> moon = new HashMap<>();
        Minecraft.getInstance().player.sendMessage(new StringTextComponent("" +  entity.getWorld().getMoonPhase()), UUID.randomUUID());
        if (entity.getWorld().getDimensionKey().getLocation().getPath().equals("overworld")) {
            switch (entity.getWorld().getMoonPhase()) {
                case 0:
                    moon.put(8, "Full moon");
                    break;
                case 1:
                    moon.put(1, "Waning gibbous");
                    break;
                case 2:
                    moon.put(2, "Third guarter");
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
                    moon.put(0, "What is a moon");
            }
        } else {
            //Yay, easter egg
            moon.put(0, "Moon.exe not found...");
        }
        return moon;
    }

    @LuaFunction(mainThread = true)
    public boolean isMoon(int phase) {
        return getCurrentMoonPhase().containsKey(phase);
    }

    @LuaFunction(mainThread = true)
    public boolean isMoon(String phase) {
        return getCurrentMoonPhase().containsValue(phase);
    }

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
    public boolean isRaining() {
        return entity.getWorld().getRainStrength(0) > 0;
    }

    @LuaFunction(mainThread = true)
    public boolean isThunder() {
        return entity.getWorld().getThunderStrength(0) > 0;
    }

    @LuaFunction(mainThread = true)
    public boolean isSunny() {
        return entity.getWorld().getThunderStrength(0) < 1 && entity.getWorld().getRainStrength(0) < 1;
    }
}