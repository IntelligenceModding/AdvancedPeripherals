package de.srendi.advancedperipherals.common.setup;

import com.google.common.collect.Sets;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class APBlockEntityTypes {

    protected static void register() {
    }

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockReaderEntity>> BLOCK_READER = APRegistration.BLOCK_ENTITIES.register("block_reader", () -> new BlockEntityType<>(BlockReaderEntity::new, Sets.newHashSet(APBlocks.BLOCK_READER.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChatBoxEntity>> CHAT_BOX = APRegistration.BLOCK_ENTITIES.register("chat_box", () -> new BlockEntityType<>(ChatBoxEntity::new, Sets.newHashSet(APBlocks.CHAT_BOX.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ColonyIntegratorEntity>> COLONY_INTEGRATOR = APRegistration.BLOCK_ENTITIES.register("colony_integrator", () -> new BlockEntityType<>(ColonyIntegratorEntity::new, Sets.newHashSet(APBlocks.COLONY_INTEGRATOR.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DistanceDetectorEntity>> DISTANCE_DETECTOR = APRegistration.BLOCK_ENTITIES.register("distance_detector", () -> new BlockEntityType<>(DistanceDetectorEntity::new, Sets.newHashSet(APBlocks.DISTANCE_DETECTOR.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyDetectorEntity>> ENERGY_DETECTOR = APRegistration.BLOCK_ENTITIES.register("energy_detector", () -> new BlockEntityType<>(EnergyDetectorEntity::new, Sets.newHashSet(APBlocks.ENERGY_DETECTOR.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnvironmentDetectorEntity>> ENVIRONMENT_DETECTOR = APRegistration.BLOCK_ENTITIES.register("environment_detector", () -> new BlockEntityType<>(EnvironmentDetectorEntity::new, Sets.newHashSet(APBlocks.ENVIRONMENT_DETECTOR.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidDetectorEntity>> FLUID_DETECTOR = APRegistration.BLOCK_ENTITIES.register("fluid_detector", () -> new BlockEntityType<>(FluidDetectorEntity::new, Sets.newHashSet(APBlocks.FLUID_DETECTOR.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GasDetectorEntity>> GAS_DETECTOR = APAddon.MEKANISM.isLoaded() ? APRegistration.BLOCK_ENTITIES.register("gas_detector", () -> new BlockEntityType<>(GasDetectorEntity::new, Sets.newHashSet(APBlocks.GAS_DETECTOR.get()), null)) : null;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeoScannerEntity>> GEO_SCANNER = APRegistration.BLOCK_ENTITIES.register("geo_scanner", () -> new BlockEntityType<>(GeoScannerEntity::new, Sets.newHashSet(APBlocks.GEO_SCANNER.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InventoryManagerEntity>> INVENTORY_MANAGER = APRegistration.BLOCK_ENTITIES.register("inventory_manager", () -> new BlockEntityType<>(InventoryManagerEntity::new, Sets.newHashSet(APBlocks.INVENTORY_MANAGER.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MEBridgeEntity>> ME_BRIDGE = APAddon.AE2.isLoaded() ? APRegistration.BLOCK_ENTITIES.register("me_bridge", () -> new BlockEntityType<>(MEBridgeEntity::new, Sets.newHashSet(APBlocks.ME_BRIDGE.get()), null)) : null;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NBTStorageEntity>> NBT_STORAGE = APRegistration.BLOCK_ENTITIES.register("nbt_storage", () -> new BlockEntityType<>(NBTStorageEntity::new, Sets.newHashSet(APBlocks.NBT_STORAGE.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PlayerDetectorEntity>> PLAYER_DETECTOR = APRegistration.BLOCK_ENTITIES.register("player_detector", () -> new BlockEntityType<>(PlayerDetectorEntity::new, Sets.newHashSet(APBlocks.PLAYER_DETECTOR.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RSBridgeEntity>> RS_BRIDGE = APAddon.REFINEDSTORAGE.isLoaded() ? APRegistration.BLOCK_ENTITIES.register("rs_bridge", () -> new BlockEntityType<>(RSBridgeEntity::new, Sets.newHashSet(APBlocks.RS_BRIDGE.get()), null)) : null;

}
