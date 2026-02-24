package de.srendi.advancedperipherals;

import dan200.computercraft.api.peripheral.PeripheralCapability;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.ae2.AE2Registries;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AEApi;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RSApi;
import de.srendi.advancedperipherals.common.blocks.base.ICapabilityProvider;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.setup.APRegistration;
import de.srendi.advancedperipherals.common.util.ChunkManager;
import de.srendi.advancedperipherals.common.village.VillageStructures;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Mod(AdvancedPeripherals.MOD_ID)
public class AdvancedPeripherals {

    public static final String MOD_ID = "advancedperipherals";
    public static final String NAME = "Advanced Peripherals";
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final Random RANDOM = new Random();

    // Used for out item/fluid fingerprints
    private static MessageDigest fingerprintMessageDigest = null;

    public AdvancedPeripherals(IEventBus modBus) {
        LOGGER.info("AdvancedPeripherals says hello!");
        APAddon.setup();

        APConfig.register(ModLoadingContext.get());

        modBus.addListener(this::registerCapabilities);
        modBus.addListener(ChunkManager::registerTicketController);
        modBus.addListener(this::onLoadComplete);

        APRegistration.register(modBus);

        try {
            fingerprintMessageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            AdvancedPeripherals.debug("Could not create message digest. Fingerprint creation WILL fail.", e);
        }
    }

    @Nullable
    public static MessageDigest getFingerprintMessageDigest() {
        if (fingerprintMessageDigest != null) {
            fingerprintMessageDigest.reset();
        }
        return fingerprintMessageDigest;
    }

    public static void debug(String message) {
        if (APConfig.GENERAL_CONFIG.enableDebugMode.get())
            LOGGER.info("[DEBUG] {}", message);
    }

    public static void debug(String message, Level level) {
        if (APConfig.GENERAL_CONFIG.enableDebugMode.get())
            LOGGER.log(level, "[DEBUG] {}", message);
    }

    public static void debug(String message, Throwable throwable) {
        if (APConfig.GENERAL_CONFIG.enableDebugMode.get())
            LOGGER.error("[DEBUG] " + message, throwable);
    }

    public static void exception(String message, Throwable throwable) {
        LOGGER.error("[ERROR] " + message, throwable);
    }

    public static ResourceLocation getRL(String resource) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, resource);
    }

    public void onLoadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            if (APAddon.AE2.isLoaded()) {
                AE2Registries.finishRegister();
            }
        });
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        APRegistration.BLOCK_ENTITIES.getEntries().forEach((entry) -> {

            event.registerBlockEntity(
                    PeripheralCapability.get(),
                    entry.get(),
                    (blockEntity, side) -> {
                        if (blockEntity instanceof ICapabilityProvider provider)
                            return provider.createPeripheralCap(side);
                        return null;
                    });

            event.registerBlockEntity(
                    Capabilities.ItemHandler.BLOCK,
                    entry.get(),
                    (blockEntity, side) -> {
                        if (blockEntity instanceof ICapabilityProvider provider)
                            return provider.createItemHandlerCap(side);
                        return null;
                    });

            event.registerBlockEntity(
                    Capabilities.FluidHandler.BLOCK,
                    entry.get(),
                    (blockEntity, side) -> {
                        if (blockEntity instanceof ICapabilityProvider provider)
                            return provider.createFluidHandlerCap(side);
                        return null;
                    });

            event.registerBlockEntity(
                    Capabilities.EnergyStorage.BLOCK,
                    entry.get(),
                    (blockEntity, side) -> {
                        if (blockEntity instanceof ICapabilityProvider provider)
                            return provider.createEnergyStorageCap(side);
                        return null;
                    });
        });

        if (APAddon.AE2.isLoaded())
            AEApi.registerCapabilities(event);
        if (APAddon.REFINEDSTORAGE.isLoaded())
            RSApi.registerCapabilities(event);

        IntegrationPeripheralProvider.registerBlockIntegrations(event);

    }
}
