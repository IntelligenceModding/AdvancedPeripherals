package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.ILuaMethodProvider;
import de.srendi.advancedperipherals.common.addons.computercraft.LuaMethod;
import de.srendi.advancedperipherals.common.addons.computercraft.LuaMethodRegistry;
import de.srendi.advancedperipherals.common.setup.ModTileEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

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
        registry.registerLuaMethod(new LuaMethod("getPlayersInRange") {
            @Override
            public Object[] call(Object[] args) {
                double range = (double) args[0];
                double rangeNegative = -range;
                List<PlayerEntity> players = getWorld().getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(pos.add(rangeNegative,rangeNegative,rangeNegative), pos.add(range + 1, range + 1, range + 1)));
                List<String> playersName = new ArrayList<>();
                for(PlayerEntity name : players) {
                    playersName.add(name.getName().getString());
                }
                return new Object[]{playersName};
            }
        });
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
