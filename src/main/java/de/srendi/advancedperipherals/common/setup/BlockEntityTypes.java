package de.srendi.advancedperipherals.common.setup;

import com.google.common.collect.Sets;
import de.srendi.advancedperipherals.common.blocks.blockentities.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.fml.ModList;
import net.neoforged.registries.RegistryObject;

public class BlockEntityTypes {

    public static final RegistryObject<BlockEntityType<ChatBoxEntity>> CHAT_BOX = Registration.TILE_ENTITIES.register("chat_box", () -> new BlockEntityType<>(ChatBoxEntity::new, Sets.newHashSet(Blocks.CHAT_BOX.get()), null));
    public static final RegistryObject<BlockEntityType<EnvironmentDetectorEntity>> ENVIRONMENT_DETECTOR = Registration.TILE_ENTITIES.register("environment_detector", () -> new BlockEntityType<>(EnvironmentDetectorEntity::new, Sets.newHashSet(Blocks.ENVIRONMENT_DETECTOR.get()), null));
    public static final RegistryObject<BlockEntityType<PlayerDetectorEntity>> PLAYER_DETECTOR = Registration.TILE_ENTITIES.register("player_detector", () -> new BlockEntityType<>(PlayerDetectorEntity::new, Sets.newHashSet(Blocks.PLAYER_DETECTOR.get()), null));
    public static final RegistryObject<BlockEntityType<MeBridgeEntity>> ME_BRIDGE = ModList.get().isLoaded("ae2") ? Registration.TILE_ENTITIES.register("me_bridge", () -> new BlockEntityType<>(MeBridgeEntity::new, Sets.newHashSet(Blocks.ME_BRIDGE.get()), null)) : null;
    public static final RegistryObject<BlockEntityType<RsBridgeEntity>> RS_BRIDGE = ModList.get().isLoaded("refinedstorage") ? Registration.TILE_ENTITIES.register("rs_bridge", () -> new BlockEntityType<>(RsBridgeEntity::new, Sets.newHashSet(Blocks.RS_BRIDGE.get()), null)) : null;
    public static final RegistryObject<BlockEntityType<EnergyDetectorEntity>> ENERGY_DETECTOR = Registration.TILE_ENTITIES.register("energy_detector", () -> new BlockEntityType<>(EnergyDetectorEntity::new, Sets.newHashSet(Blocks.ENERGY_DETECTOR.get()), null));
    public static final RegistryObject<BlockEntityType<InventoryManagerEntity>> INVENTORY_MANAGER = Registration.TILE_ENTITIES.register("inventory_manager", () -> new BlockEntityType<>(InventoryManagerEntity::new, Sets.newHashSet(Blocks.INVENTORY_MANAGER.get()), null));
    public static final RegistryObject<BlockEntityType<RedstoneIntegratorEntity>> REDSTONE_INTEGRATOR = Registration.TILE_ENTITIES.register("redstone_integrator", () -> new BlockEntityType<>(RedstoneIntegratorEntity::new, Sets.newHashSet(Blocks.REDSTONE_INTEGRATOR.get()), null));
    public static final RegistryObject<BlockEntityType<BlockReaderEntity>> BLOCK_READER = Registration.TILE_ENTITIES.register("block_reader", () -> new BlockEntityType<>(BlockReaderEntity::new, Sets.newHashSet(Blocks.BLOCK_READER.get()), null));
    public static final RegistryObject<BlockEntityType<GeoScannerEntity>> GEO_SCANNER = Registration.TILE_ENTITIES.register("geo_scanner", () -> new BlockEntityType<>(GeoScannerEntity::new, Sets.newHashSet(Blocks.GEO_SCANNER.get()), null));
    public static final RegistryObject<BlockEntityType<ColonyIntegratorEntity>> COLONY_INTEGRATOR = Registration.TILE_ENTITIES.register("colony_integrator", () -> new BlockEntityType<>(ColonyIntegratorEntity::new, Sets.newHashSet(Blocks.COLONY_INTEGRATOR.get()), null));

    public static final RegistryObject<BlockEntityType<NBTStorageEntity>> NBT_STORAGE = Registration.TILE_ENTITIES.register("nbt_storage", () -> new BlockEntityType<>(NBTStorageEntity::new, Sets.newHashSet(Blocks.NBT_STORAGE.get()), null));

    public static void register() {
    }

}
