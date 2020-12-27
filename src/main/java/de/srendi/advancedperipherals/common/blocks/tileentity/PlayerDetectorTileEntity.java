package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.ILuaMethodProvider;
import de.srendi.advancedperipherals.common.addons.computercraft.LuaMethodRegistry;
import de.srendi.advancedperipherals.common.setup.ModTileEntityTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class PlayerDetectorTileEntity extends TileEntity implements ILuaMethodProvider {

    private final LuaMethodRegistry luaMethodRegistry = new LuaMethodRegistry(this);

    public PlayerDetectorTileEntity() {
        this(ModTileEntityTypes.PLAYER_DETECTOR.get());
    }

    public PlayerDetectorTileEntity(final TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    @Override
    public void addLuaMethods(LuaMethodRegistry registry) {
        //Nothing here...
    }

    @Override
    public LuaMethodRegistry getLuaMethodRegistry() {
        return luaMethodRegistry;
    }

    @Override
    public String getPeripheralType() {
        return "playerDetector";
    }
}
