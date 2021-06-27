package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoScannerPeripheral extends BasePeripheral {
	/*
	Highly inspired by https://github.com/SquidDev-CC/plethora/ BlockScanner
	*/

    public GeoScannerPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    public GeoScannerPeripheral(String type, TileEntity tileEntity) {
        super(type, tileEntity);
    }

    public GeoScannerPeripheral(String type, Entity entity) {
        super(type, entity);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableGeoScanner;
    }
    
    private static List<Map<String, ?>> scan(World world, int x, int y, int z, int radius) {
		List<Map<String, ?>> result = new ArrayList<>();
		for (int oX = x - radius; oX <= x + radius; oX++) {
			for (int oY = y - radius; oY <= y + radius; oY++) {
				for (int oZ = z - radius; oZ <= z + radius; oZ++) {
					BlockPos subPos = new BlockPos(oX, oY, oZ);
					BlockState block = world.getBlockState(subPos);

					HashMap<String, Object> data = new HashMap<>(6);
					data.put("x", oX - x);
					data.put("y", oY - y);
					data.put("z", oZ - z);

					ResourceLocation name = block.getBlock().getRegistryName();
					data.put("name", name == null ? "unknown" : name.toString());

					result.add(data);
				}
			}
		}

		return result;
	}

    @LuaFunction(mainThread = true)
    public final Object scan(int radius) throws LuaException {
    	BlockPos pos = getPos();
    	World world = getWorld();
    	List<Map<String, ?>> scanResult = scan(world, pos.getX(), pos.getY(), pos.getZ(), Math.min(radius, 8));
    	return scanResult;
    }
}
