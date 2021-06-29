package de.srendi.advancedperipherals.common.setup;

import com.google.common.collect.Sets;
import de.srendi.advancedperipherals.common.blocks.tileentity.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;

public class TileEntityTypes {

    static void register() {
    }

    public static final RegistryObject<TileEntityType<ChatBoxTile>> CHAT_BOX = Registration.TILE_ENTITIES.register("chat_box", () -> new TileEntityType<>(ChatBoxTile::new, Sets.newHashSet(Blocks.CHAT_BOX.get()), null));
    public static final RegistryObject<TileEntityType<EnvironmentDetectorTile>> ENVIRONMENT_DETECTOR = Registration.TILE_ENTITIES.register("environment_detector", () -> new TileEntityType<>(EnvironmentDetectorTile::new, Sets.newHashSet(Blocks.ENVIRONMENT_DETECTOR.get()), null));
    public static final RegistryObject<TileEntityType<PlayerDetectorTile>> PLAYER_DETECTOR = Registration.TILE_ENTITIES.register("player_detector", () -> new TileEntityType<>(PlayerDetectorTile::new, Sets.newHashSet(Blocks.PLAYER_DETECTOR.get()), null));
    public static final RegistryObject<TileEntityType<MeBridgeTile>> ME_BRIDGE = ModList.get().isLoaded("appliedenergistics2") ? Registration.TILE_ENTITIES.register("me_bridge", () -> new TileEntityType<>(MeBridgeTile::new, Sets.newHashSet(Blocks.ME_BRIDGE.get()), null)) : null;
    public static final RegistryObject<TileEntityType<RsBridgeTile>> RS_BRIDGE = ModList.get().isLoaded("refinedstorage") ? Registration.TILE_ENTITIES.register("rs_bridge", () -> new TileEntityType<>(RsBridgeTile::new, Sets.newHashSet(Blocks.RS_BRIDGE.get()), null)) : null;
    public static final RegistryObject<TileEntityType<EnergyDetectorTile>> ENERGY_DETECTOR = Registration.TILE_ENTITIES.register("energy_detector", () -> new TileEntityType<>(EnergyDetectorTile::new, Sets.newHashSet(Blocks.ENERGY_DETECTOR.get()), null));
    public static final RegistryObject<TileEntityType<ARControllerTile>> AR_CONTROLLER = Registration.TILE_ENTITIES.register("ar_controller", () -> new TileEntityType<>(ARControllerTile::new, Sets.newHashSet(Blocks.AR_CONTROLLER.get()), null));
    public static final RegistryObject<TileEntityType<InventoryManagerTile>> INVENTORY_MANAGER = Registration.TILE_ENTITIES.register("inventory_manager", () -> new TileEntityType<>(InventoryManagerTile::new, Sets.newHashSet(Blocks.INVENTORY_MANAGER.get()), null));
    public static final RegistryObject<TileEntityType<RedstoneIntegratorTile>> REDSTONE_INTEGRATOR = Registration.TILE_ENTITIES.register("redstone_integrator", () -> new TileEntityType<>(RedstoneIntegratorTile::new, Sets.newHashSet(Blocks.REDSTONE_INTEGRATOR.get()), null));
    public static final RegistryObject<TileEntityType<BlockReaderTile>> BLOCK_READER = Registration.TILE_ENTITIES.register("block_reader", () -> new TileEntityType<>(BlockReaderTile::new, Sets.newHashSet(Blocks.BLOCK_READER.get()), null));
    public static final RegistryObject<TileEntityType<GeoScannerTile>> GEO_SCANNER = Registration.TILE_ENTITIES.register("geo_scanner", () -> new TileEntityType<>(GeoScannerTile::new, Sets.newHashSet(Blocks.GEO_SCANNER.get()), null));
    public static final RegistryObject<TileEntityType<ColonyIntegratorTile>> COLONY_INTEGRATOR = Registration.TILE_ENTITIES.register("colony_integrator", () -> new TileEntityType<>(ColonyIntegratorTile::new, Sets.newHashSet(Blocks.COLONY_INTEGRATOR.get()), null));
    public static final RegistryObject<TileEntityType<NBTStorageTile>> NBT_STORAGE = Registration.TILE_ENTITIES.register("nbt_storage", () -> new TileEntityType<>(NBTStorageTile::new, Sets.newHashSet(Blocks.NBT_STORAGE.get()), null));
}