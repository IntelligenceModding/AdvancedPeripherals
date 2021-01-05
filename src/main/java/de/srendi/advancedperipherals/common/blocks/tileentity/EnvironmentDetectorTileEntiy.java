package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.ILuaMethodProvider;
import de.srendi.advancedperipherals.common.addons.computercraft.LuaMethod;
import de.srendi.advancedperipherals.common.addons.computercraft.LuaMethodRegistry;
import de.srendi.advancedperipherals.common.setup.ModTileEntityTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.LightType;

public class EnvironmentDetectorTileEntiy extends TileEntity implements ILuaMethodProvider {

    private final LuaMethodRegistry luaMethodRegistry = new LuaMethodRegistry(this);

    public EnvironmentDetectorTileEntiy() {
        this(ModTileEntityTypes.ENVIRONMENT_DETECTOR.get());
    }

    public EnvironmentDetectorTileEntiy(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void addLuaMethods(LuaMethodRegistry registry) {
        registry.registerLuaMethod(new LuaMethod("getBiome") {
            @Override
            public Object[] call(Object[] args) {
                String biomeName = getWorld().getBiome(getPos()).getRegistryName().toString();
                String[] biome = biomeName.split(":");
                return new Object[]{biome[1]};
            }
        });
        registry.registerLuaMethod(new LuaMethod("getLightLevel") {
            @Override
            public Object[] call(Object[] args) {
                getWorld().getLightFor(LightType.SKY, getPos().add(0, 1, 0));
                return new Object[]{getWorld().getLightFor(LightType.SKY, getPos().add(0, 1, 0))};
            }
        });
        registry.registerLuaMethod(new LuaMethod("getTime") {
            @Override
            public Object[] call(Object[] args) {
                return new Object[]{getWorld().getDayTime()};
            }
        });
    }


    @Override
    public LuaMethodRegistry getLuaMethodRegistry() {
        return luaMethodRegistry;
    }

    @Override
    public String getPeripheralType() {
        return "environmentDetector";
    }

}
