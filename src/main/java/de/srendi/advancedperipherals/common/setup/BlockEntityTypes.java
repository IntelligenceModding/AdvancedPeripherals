package de.srendi.advancedperipherals.common.setup;

import com.google.common.collect.Sets;
import de.srendi.advancedperipherals.common.addons.APAddons;
import de.srendi.advancedperipherals.common.blocks.blockentities.BlockReaderEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.ChatBoxEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.EnergyDetectorEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.EnvironmentDetectorEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.GeoScannerEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.InventoryManagerEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.MeBridgeEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.NBTStorageEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.PlayerDetectorEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.RedstoneIntegratorEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.RsBridgeEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockEntityTypes {

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChatBoxEntity>> CHAT_BOX = Registration.BLOCK_ENTITIES.register("chat_box", () -> new BlockEntityType<>(ChatBoxEntity::new, Sets.newHashSet(Blocks.CHAT_BOX.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnvironmentDetectorEntity>> ENVIRONMENT_DETECTOR = Registration.BLOCK_ENTITIES.register("environment_detector", () -> new BlockEntityType<>(EnvironmentDetectorEntity::new, Sets.newHashSet(Blocks.ENVIRONMENT_DETECTOR.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PlayerDetectorEntity>> PLAYER_DETECTOR = Registration.BLOCK_ENTITIES.register("player_detector", () -> new BlockEntityType<>(PlayerDetectorEntity::new, Sets.newHashSet(Blocks.PLAYER_DETECTOR.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MeBridgeEntity>> ME_BRIDGE = APAddons.ae2Loaded() ? Registration.BLOCK_ENTITIES.register("me_bridge", () -> new BlockEntityType<>(MeBridgeEntity::new, Sets.newHashSet(Blocks.ME_BRIDGE.get()), null)) : null;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RsBridgeEntity>> RS_BRIDGE = APAddons.refinedStorageLoaded() ? Registration.BLOCK_ENTITIES.register("rs_bridge", () -> new BlockEntityType<>(RsBridgeEntity::new, Sets.newHashSet(Blocks.RS_BRIDGE.get()), null)) : null;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyDetectorEntity>> ENERGY_DETECTOR = Registration.BLOCK_ENTITIES.register("energy_detector", () -> new BlockEntityType<>(EnergyDetectorEntity::new, Sets.newHashSet(Blocks.ENERGY_DETECTOR.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InventoryManagerEntity>> INVENTORY_MANAGER = Registration.BLOCK_ENTITIES.register("inventory_manager", () -> new BlockEntityType<>(InventoryManagerEntity::new, Sets.newHashSet(Blocks.INVENTORY_MANAGER.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RedstoneIntegratorEntity>> REDSTONE_INTEGRATOR = Registration.BLOCK_ENTITIES.register("redstone_integrator", () -> new BlockEntityType<>(RedstoneIntegratorEntity::new, Sets.newHashSet(Blocks.REDSTONE_INTEGRATOR.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockReaderEntity>> BLOCK_READER = Registration.BLOCK_ENTITIES.register("block_reader", () -> new BlockEntityType<>(BlockReaderEntity::new, Sets.newHashSet(Blocks.BLOCK_READER.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeoScannerEntity>> GEO_SCANNER = Registration.BLOCK_ENTITIES.register("geo_scanner", () -> new BlockEntityType<>(GeoScannerEntity::new, Sets.newHashSet(Blocks.GEO_SCANNER.get()), null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NBTStorageEntity>> NBT_STORAGE = Registration.BLOCK_ENTITIES.register("nbt_storage", () -> new BlockEntityType<>(NBTStorageEntity::new, Sets.newHashSet(Blocks.NBT_STORAGE.get()), null));

    public static void register() {
    }

}
