package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.google.common.math.IntMath;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class GeoScannerPeripheral extends BasePeripheral {
	/*
	Highly inspired by https://github.com/SquidDev-CC/plethora/ BlockScanner
	*/

	private Optional<Timestamp> lastScanTimestamp = Optional.empty();

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

	private static int estimateCost(int radius) {
    	if (radius <= AdvancedPeripheralsConfig.geoScannerMaxFreeRadius) {
    		return 0;
		}
    	if (radius > AdvancedPeripheralsConfig.geoScannerMaxCostRadius) {
    		return -1;
		}
    	int freeBlockCount = IntMath.pow(2 * AdvancedPeripheralsConfig.geoScannerMaxFreeRadius + 1, 3);
    	int allBlockCount = IntMath.pow(2 * radius + 1, 3);
    	return (allBlockCount - freeBlockCount) * AdvancedPeripheralsConfig.geoScannerAdditionalBlockCost;
	}

	private boolean isScanOnCooldown(Timestamp currentTimestamp) {
    	return lastScanTimestamp.map(
    			timestamp -> (currentTimestamp.getTime() - timestamp.getTime()) < AdvancedPeripheralsConfig.geoScannerMinScanPeriod
		).orElse(false);
	}

	private long getScanCooldownLeft() {
    	Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
    	return lastScanTimestamp.map(
    			timestamp -> Math.max(
    					0,
						AdvancedPeripheralsConfig.geoScannerMinScanPeriod - currentTimestamp.getTime() + timestamp.getTime()
				)
		).orElse(0L);
	}

	@LuaFunction(mainThread = true)
	public final MethodResult cost(int radius){
    	int estimatedCost = estimateCost(radius);
    	if (estimatedCost < 0) {
    		return MethodResult.of(null, "Radius is exceed max value");
		}
    	return MethodResult.of(estimatedCost);
	}

	@LuaFunction(mainThread = true)
	public final int getEnergy(){
    	if (tileEntity == null) {
    		return 0;
		}
    	return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
	}

	@LuaFunction(mainThread = true)
	public final int getEnergyCapacity(){
		if (tileEntity == null) {
			return 0;
		}
		return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
	}

	@LuaFunction(mainThread = true)
	public final long getScanCooldown(){
    	return getScanCooldownLeft();
	}

	@LuaFunction(mainThread = true)
	public final MethodResult deepScan(int x, int y, int z) {
    	Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
		if (isScanOnCooldown(currentTimestamp)) {
			return MethodResult.of(null, "Scanner need some time to cooldown");
		}
    	World world = getWorld();
    	int maxShift = Math.max(Math.max(x, y), z);
    	if (maxShift > AdvancedPeripheralsConfig.geoScannerMaxCostRadius) {
			return MethodResult.of(null, "Radius is exceed max value");
		}
    	if (maxShift > AdvancedPeripheralsConfig.geoScannerMaxFreeRadius) {
			if (tileEntity == null) {
				return MethodResult.of(null, "Radius is exceed max value");
			}
			LazyOptional<IEnergyStorage> lazyEnergyStorage = tileEntity.getCapability(CapabilityEnergy.ENERGY);
			int energyCount = lazyEnergyStorage.map(IEnergyStorage::getEnergyStored).orElse(0);
			if (energyCount < AdvancedPeripheralsConfig.geoScannerAdditionalBlockCost) {
				return MethodResult.of(null, String.format("Not enough RF energy, %d needed", AdvancedPeripheralsConfig.geoScannerAdditionalBlockCost));
			}
			lazyEnergyStorage.ifPresent(iEnergyStorage -> iEnergyStorage.extractEnergy(AdvancedPeripheralsConfig.geoScannerAdditionalBlockCost, false));
		}
    	BlockPos blockPos = getPos().offset(x, y, z);
		BlockState block = world.getBlockState(blockPos);

		HashMap<String, Object> data = new HashMap<>();
		ResourceLocation name = block.getBlock().getRegistryName();
		data.put("name", name == null ? "unknown" : name.toString());
		if (!block.is(Blocks.AIR)) {
			data.put("tags", block.getBlock().getTags());
			if (block.hasTileEntity()) {
				try {
					data.put("nbt", NBTUtil.toLua(world.getBlockEntity(blockPos).save(new CompoundNBT())));
				} catch (NullPointerException e) {
					// just do nothing ...
				}
			}
		}
		lastScanTimestamp = Optional.of(currentTimestamp);
		return MethodResult.of(data);
	}

    @LuaFunction(mainThread = true)
    public final MethodResult scan(int radius) {
		Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
		if (isScanOnCooldown(currentTimestamp)) {
			return MethodResult.of(null, "Scanner need some time to cooldown");
		}
    	BlockPos pos = getPos();
    	World world = getWorld();
    	if (radius > AdvancedPeripheralsConfig.geoScannerMaxCostRadius) {
    		return MethodResult.of(null, "Radius is exceed max value");
		}
    	if (radius > AdvancedPeripheralsConfig.geoScannerMaxFreeRadius) {
    		if (tileEntity == null) {
				return MethodResult.of(null, "Radius is exceed max value");
			}
			LazyOptional<IEnergyStorage> lazyEnergyStorage = tileEntity.getCapability(CapabilityEnergy.ENERGY);
			int cost = estimateCost(radius);
			int energyCount = lazyEnergyStorage.map(IEnergyStorage::getEnergyStored).orElse(0);
			if (energyCount < cost) {
				return MethodResult.of(null, String.format("Not enough RF energy, %d needed", cost));
			}
			lazyEnergyStorage.ifPresent(iEnergyStorage -> iEnergyStorage.extractEnergy(cost, false));
		}
		List<Map<String, ?>> scanResult = scan(world, pos.getX(), pos.getY(), pos.getZ(), Math.min(radius, 8));
    	lastScanTimestamp = Optional.of(currentTimestamp);
    	return MethodResult.of(scanResult);
    }
}
