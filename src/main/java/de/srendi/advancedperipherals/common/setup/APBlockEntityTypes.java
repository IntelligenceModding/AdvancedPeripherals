package de.srendi.advancedperipherals.common.setup;

import com.google.common.collect.Sets;
import de.srendi.advancedperipherals.common.addons.APAddons;
import de.srendi.advancedperipherals.common.blocks.blockentities.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class APBlockEntityTypes {

    protected static void register() {
    }

    public static final RegistryObject<BlockEntityType<ChatBoxEntity>> CHAT_BOX = APRegistration.TILE_ENTITIES.register("chat_box", () -> new BlockEntityType<>(ChatBoxEntity::new, Sets.newHashSet(APBlocks.CHAT_BOX.get()), null));
    public static final RegistryObject<BlockEntityType<EnvironmentDetectorEntity>> ENVIRONMENT_DETECTOR = APRegistration.TILE_ENTITIES.register("environment_detector", () -> new BlockEntityType<>(EnvironmentDetectorEntity::new, Sets.newHashSet(APBlocks.ENVIRONMENT_DETECTOR.get()), null));
    public static final RegistryObject<BlockEntityType<PlayerDetectorEntity>> PLAYER_DETECTOR = APRegistration.TILE_ENTITIES.register("player_detector", () -> new BlockEntityType<>(PlayerDetectorEntity::new, Sets.newHashSet(APBlocks.PLAYER_DETECTOR.get()), null));
    public static final RegistryObject<BlockEntityType<MeBridgeEntity>> ME_BRIDGE = APAddons.appliedEnergisticsLoaded ? APRegistration.TILE_ENTITIES.register("me_bridge", () -> new BlockEntityType<>(MeBridgeEntity::new, Sets.newHashSet(APBlocks.ME_BRIDGE.get()), null)) : null;
    public static final RegistryObject<BlockEntityType<RsBridgeEntity>> RS_BRIDGE = APAddons.refinedStorageLoaded ? APRegistration.TILE_ENTITIES.register("rs_bridge", () -> new BlockEntityType<>(RsBridgeEntity::new, Sets.newHashSet(APBlocks.RS_BRIDGE.get()), null)) : null;
    public static final RegistryObject<BlockEntityType<EnergyDetectorEntity>> ENERGY_DETECTOR = APRegistration.TILE_ENTITIES.register("energy_detector", () -> new BlockEntityType<>(EnergyDetectorEntity::new, Sets.newHashSet(APBlocks.ENERGY_DETECTOR.get()), null));
    public static final RegistryObject<BlockEntityType<FluidDetectorEntity>> FLUID_DETECTOR = APRegistration.TILE_ENTITIES.register("fluid_detector", () -> new BlockEntityType<>(FluidDetectorEntity::new, Sets.newHashSet(APBlocks.FLUID_DETECTOR.get()), null));
    public static final RegistryObject<BlockEntityType<GasDetectorEntity>> GAS_DETECTOR = APAddons.mekanismLoaded ? APRegistration.TILE_ENTITIES.register("gas_detector", () -> new BlockEntityType<>(GasDetectorEntity::new, Sets.newHashSet(APBlocks.GAS_DETECTOR.get()), null)) : null;
    public static final RegistryObject<BlockEntityType<InventoryManagerEntity>> INVENTORY_MANAGER = APRegistration.TILE_ENTITIES.register("inventory_manager", () -> new BlockEntityType<>(InventoryManagerEntity::new, Sets.newHashSet(APBlocks.INVENTORY_MANAGER.get()), null));
    public static final RegistryObject<BlockEntityType<RedstoneIntegratorEntity>> REDSTONE_INTEGRATOR = APRegistration.TILE_ENTITIES.register("redstone_integrator", () -> new BlockEntityType<>(RedstoneIntegratorEntity::new, Sets.newHashSet(APBlocks.REDSTONE_INTEGRATOR.get()), null));
    public static final RegistryObject<BlockEntityType<BlockReaderEntity>> BLOCK_READER = APRegistration.TILE_ENTITIES.register("block_reader", () -> new BlockEntityType<>(BlockReaderEntity::new, Sets.newHashSet(APBlocks.BLOCK_READER.get()), null));
    public static final RegistryObject<BlockEntityType<GeoScannerEntity>> GEO_SCANNER = APRegistration.TILE_ENTITIES.register("geo_scanner", () -> new BlockEntityType<>(GeoScannerEntity::new, Sets.newHashSet(APBlocks.GEO_SCANNER.get()), null));
    public static final RegistryObject<BlockEntityType<ColonyIntegratorEntity>> COLONY_INTEGRATOR = APRegistration.TILE_ENTITIES.register("colony_integrator", () -> new BlockEntityType<>(ColonyIntegratorEntity::new, Sets.newHashSet(APBlocks.COLONY_INTEGRATOR.get()), null));
    public static final RegistryObject<BlockEntityType<NBTStorageEntity>> NBT_STORAGE = APRegistration.TILE_ENTITIES.register("nbt_storage", () -> new BlockEntityType<>(NBTStorageEntity::new, Sets.newHashSet(APBlocks.NBT_STORAGE.get()), null));
    public static final RegistryObject<BlockEntityType<DistanceDetectorEntity>> DISTANCE_DETECTOR = APRegistration.TILE_ENTITIES.register("distance_detector", () -> new BlockEntityType<>(DistanceDetectorEntity::new, Sets.newHashSet(APBlocks.DISTANCE_DETECTOR.get()), null));

}
