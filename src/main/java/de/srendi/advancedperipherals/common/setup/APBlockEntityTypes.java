package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.blocks.blockentities.BlockReaderEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.ChatBoxEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.ColonyIntegratorEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.DistanceDetectorEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.EnergyDetectorEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.EnvironmentDetectorEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.FluidDetectorEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.GasDetectorEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.GeoScannerEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.InventoryManagerEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.MEBridgeEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.NBTStorageEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.PlayerDetectorEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.RSBridgeEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.SmartRailBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;

public class APBlockEntityTypes {
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockReaderEntity>> BLOCK_READER = APRegistration.BLOCK_ENTITIES.register("block_reader", () -> new BlockReaderEntity.Type<>(BlockReaderEntity::new, Set.of(APBlocks.BLOCK_READER.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChatBoxEntity>> CHAT_BOX = APRegistration.BLOCK_ENTITIES.register("chat_box", () -> new ChatBoxEntity.Type<>(ChatBoxEntity::new, Set.of(APBlocks.CHAT_BOX.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ColonyIntegratorEntity>> COLONY_INTEGRATOR = APAddon.MINECOLONIES.isLoaded() ? APRegistration.BLOCK_ENTITIES.register("colony_integrator", () -> new ColonyIntegratorEntity.Type<>(ColonyIntegratorEntity::new, Set.of(APBlocks.COLONY_INTEGRATOR.get()), null)) : null;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DistanceDetectorEntity>> DISTANCE_DETECTOR = APRegistration.BLOCK_ENTITIES.register("distance_detector", () -> new DistanceDetectorEntity.Type<>(DistanceDetectorEntity::new, Set.of(APBlocks.DISTANCE_DETECTOR.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyDetectorEntity>> ENERGY_DETECTOR = APRegistration.BLOCK_ENTITIES.register("energy_detector", () -> new EnergyDetectorEntity.Type<>(EnergyDetectorEntity::new, Set.of(APBlocks.ENERGY_DETECTOR.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnvironmentDetectorEntity>> ENVIRONMENT_DETECTOR = APRegistration.BLOCK_ENTITIES.register("environment_detector", () -> new EnvironmentDetectorEntity.Type<>(EnvironmentDetectorEntity::new, Set.of(APBlocks.ENVIRONMENT_DETECTOR.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidDetectorEntity>> FLUID_DETECTOR = APRegistration.BLOCK_ENTITIES.register("fluid_detector", () -> new FluidDetectorEntity.Type<>(FluidDetectorEntity::new, Set.of(APBlocks.FLUID_DETECTOR.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GasDetectorEntity>> GAS_DETECTOR = APAddon.MEKANISM.isLoaded() ? APRegistration.BLOCK_ENTITIES.register("gas_detector", () -> new GasDetectorEntity.Type<>(GasDetectorEntity::new, Set.of(APBlocks.GAS_DETECTOR.get()), null)) : null;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeoScannerEntity>> GEO_SCANNER = APRegistration.BLOCK_ENTITIES.register("geo_scanner", () -> new GeoScannerEntity.Type<>(GeoScannerEntity::new, Set.of(APBlocks.GEO_SCANNER.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InventoryManagerEntity>> INVENTORY_MANAGER = APRegistration.BLOCK_ENTITIES.register("inventory_manager", () -> new InventoryManagerEntity.Type<>(InventoryManagerEntity::new, Set.of(APBlocks.INVENTORY_MANAGER.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MEBridgeEntity>> ME_BRIDGE = APAddon.AE2.isLoaded() ? APRegistration.BLOCK_ENTITIES.register("me_bridge", () -> new MEBridgeEntity.Type<>(MEBridgeEntity::new, Set.of(APBlocks.ME_BRIDGE.get()), null)) : null;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NBTStorageEntity>> NBT_STORAGE = APRegistration.BLOCK_ENTITIES.register("nbt_storage", () -> new NBTStorageEntity.Type<>(NBTStorageEntity::new, Set.of(APBlocks.NBT_STORAGE.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PlayerDetectorEntity>> PLAYER_DETECTOR = APRegistration.BLOCK_ENTITIES.register("player_detector", () -> new PlayerDetectorEntity.Type<>(PlayerDetectorEntity::new, Set.of(APBlocks.PLAYER_DETECTOR.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RSBridgeEntity>> RS_BRIDGE = APAddon.REFINEDSTORAGE.isLoaded() ? APRegistration.BLOCK_ENTITIES.register("rs_bridge", () -> new RSBridgeEntity.Type<>(RSBridgeEntity::new, Set.of(APBlocks.RS_BRIDGE.get()), null)) : null;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SmartRailBlockEntity>> SMART_RAIL = APRegistration.BLOCK_ENTITIES.register("smart_rail", () -> new SmartRailBlockEntity.Type<>(SmartRailBlockEntity::new, Set.of(APBlocks.SMART_RAIL.get()), null));

    public static void register() {
    }
}
