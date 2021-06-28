package de.srendi.advancedperipherals.common.setup;

import com.google.common.collect.Sets;
import de.srendi.advancedperipherals.common.blocks.tileentity.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;

public class TileEntityTypes {
    public static final RegistryObject<TileEntityType<ChatBoxTileEntity>> CHAT_BOX = Registration.TILE_ENTITIES.register("chat_box", () -> new TileEntityType<>(ChatBoxTileEntity::new, Sets.newHashSet(Blocks.CHAT_BOX.get()), null));
    public static final RegistryObject<TileEntityType<EnvironmentDetectorTileEntiy>> ENVIRONMENT_DETECTOR = Registration.TILE_ENTITIES.register("environment_detector", () -> new TileEntityType<>(EnvironmentDetectorTileEntiy::new, Sets.newHashSet(Blocks.ENVIRONMENT_DETECTOR.get()), null));
    public static final RegistryObject<TileEntityType<PlayerDetectorTileEntity>> PLAYER_DETECTOR = Registration.TILE_ENTITIES.register("player_detector", () -> new TileEntityType<>(PlayerDetectorTileEntity::new, Sets.newHashSet(Blocks.PLAYER_DETECTOR.get()), null));
    public static final RegistryObject<TileEntityType<MeBridgeTileEntity>> ME_BRIDGE = ModList.get().isLoaded("appliedenergistics2") ? Registration.TILE_ENTITIES.register("me_bridge", () -> new TileEntityType<>(MeBridgeTileEntity::new, Sets.newHashSet(Blocks.ME_BRIDGE.get()), null)) : null;
    public static final RegistryObject<TileEntityType<RsBridgeTileEntity>> RS_BRIDGE = ModList.get().isLoaded("refinedstorage") ? Registration.TILE_ENTITIES.register("rs_bridge", () -> new TileEntityType<>(RsBridgeTileEntity::new, Sets.newHashSet(Blocks.RS_BRIDGE.get()), null)) : null;
    public static final RegistryObject<TileEntityType<EnergyDetectorTileEntity>> ENERGY_DETECTOR = Registration.TILE_ENTITIES.register("energy_detector", () -> new TileEntityType<>(EnergyDetectorTileEntity::new, Sets.newHashSet(Blocks.ENERGY_DETECTOR.get()), null));
    public static final RegistryObject<TileEntityType<ARControllerTileEntity>> AR_CONTROLLER = Registration.TILE_ENTITIES.register("ar_controller", () -> new TileEntityType<>(ARControllerTileEntity::new, Sets.newHashSet(Blocks.AR_CONTROLLER.get()), null));
    public static final RegistryObject<TileEntityType<InventoryManagerTileEntity>> INVENTORY_MANAGER = Registration.TILE_ENTITIES.register("inventory_manager", () -> new TileEntityType<>(InventoryManagerTileEntity::new, Sets.newHashSet(Blocks.INVENTORY_MANAGER.get()), null));
    public static final RegistryObject<TileEntityType<RedstoneIntegratorTileEntity>> REDSTONE_INTEGRATOR = Registration.TILE_ENTITIES.register("redstone_integrator", () -> new TileEntityType<>(RedstoneIntegratorTileEntity::new, Sets.newHashSet(Blocks.REDSTONE_INTEGRATOR.get()), null));
    public static final RegistryObject<TileEntityType<BlockReaderTileEntity>> BLOCK_READER = Registration.TILE_ENTITIES.register("block_reader", () -> new TileEntityType<>(BlockReaderTileEntity::new, Sets.newHashSet(Blocks.BLOCK_READER.get()), null));
    public static final RegistryObject<TileEntityType<GeoScannerTileEntity>> GEO_SCANNER = Registration.TILE_ENTITIES.register("geo_scanner", () -> new TileEntityType<>(GeoScannerTileEntity::new, Sets.newHashSet(Blocks.GEO_SCANNER.get()), null));
    public static final RegistryObject<TileEntityType<ColonyIntegratorTile>> COLONY_INTEGRATOR = Registration.TILE_ENTITIES.register("colony_integrator", () -> new TileEntityType<>(ColonyIntegratorTile::new, Sets.newHashSet(Blocks.COLONY_INTEGRATOR.get()), null));
}