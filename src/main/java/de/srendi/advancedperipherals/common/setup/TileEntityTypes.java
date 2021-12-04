package de.srendi.advancedperipherals.common.setup;

import com.google.common.collect.Sets;
import de.srendi.advancedperipherals.common.blocks.tileentity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;

public class TileEntityTypes {

    static void register() {
    }

    public static final RegistryObject<BlockEntityType<ChatBoxTile>> CHAT_BOX = Registration.TILE_ENTITIES.register("chat_box", () -> new BlockEntityType<>(ChatBoxTile::new, Sets.newHashSet(Blocks.CHAT_BOX.get()), null));
    public static final RegistryObject<BlockEntityType<EnvironmentDetectorTile>> ENVIRONMENT_DETECTOR = Registration.TILE_ENTITIES.register("environment_detector", () -> new BlockEntityType<>(EnvironmentDetectorTile::new, Sets.newHashSet(Blocks.ENVIRONMENT_DETECTOR.get()), null));
    public static final RegistryObject<BlockEntityType<PlayerDetectorTile>> PLAYER_DETECTOR = Registration.TILE_ENTITIES.register("player_detector", () -> new BlockEntityType<>(PlayerDetectorTile::new, Sets.newHashSet(Blocks.PLAYER_DETECTOR.get()), null));
    public static final RegistryObject<BlockEntityType<EnergyDetectorTile>> ENERGY_DETECTOR = Registration.TILE_ENTITIES.register("energy_detector", () -> new BlockEntityType<>(EnergyDetectorTile::new, Sets.newHashSet(Blocks.ENERGY_DETECTOR.get()), null));
    public static final RegistryObject<BlockEntityType<ARControllerTile>> AR_CONTROLLER = Registration.TILE_ENTITIES.register("ar_controller", () -> new BlockEntityType<>(ARControllerTile::new, Sets.newHashSet(Blocks.AR_CONTROLLER.get()), null));
    public static final RegistryObject<BlockEntityType<InventoryManagerTile>> INVENTORY_MANAGER = Registration.TILE_ENTITIES.register("inventory_manager", () -> new BlockEntityType<>(InventoryManagerTile::new, Sets.newHashSet(Blocks.INVENTORY_MANAGER.get()), null));
    public static final RegistryObject<BlockEntityType<RedstoneIntegratorTile>> REDSTONE_INTEGRATOR = Registration.TILE_ENTITIES.register("redstone_integrator", () -> new BlockEntityType<>(RedstoneIntegratorTile::new, Sets.newHashSet(Blocks.REDSTONE_INTEGRATOR.get()), null));
    public static final RegistryObject<BlockEntityType<BlockReaderTile>> BLOCK_READER = Registration.TILE_ENTITIES.register("block_reader", () -> new BlockEntityType<>(BlockReaderTile::new, Sets.newHashSet(Blocks.BLOCK_READER.get()), null));
    public static final RegistryObject<BlockEntityType<GeoScannerTile>> GEO_SCANNER = Registration.TILE_ENTITIES.register("geo_scanner", () -> new BlockEntityType<>(GeoScannerTile::new, Sets.newHashSet(Blocks.GEO_SCANNER.get()), null));
    public static final RegistryObject<BlockEntityType<NBTStorageTile>> NBT_STORAGE = Registration.TILE_ENTITIES.register("nbt_storage", () -> new BlockEntityType<>(NBTStorageTile::new, Sets.newHashSet(Blocks.NBT_STORAGE.get()), null));

}