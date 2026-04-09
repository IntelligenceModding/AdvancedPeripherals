package de.srendi.advancedperipherals;

import dan200.computercraft.api.media.MediaCapability;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import dan200.computercraft.shared.media.MountMedia;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.ae2.AEApi;
import de.srendi.advancedperipherals.common.addons.ae2.AE2Registries;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RSApi;
import de.srendi.advancedperipherals.common.blocks.base.ICapabilityProvider;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.setup.APItems;
import de.srendi.advancedperipherals.common.setup.APRegistration;
import de.srendi.advancedperipherals.common.util.ChunkManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

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

    static {
        try {
            fingerprintMessageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            AdvancedPeripherals.debug("Could not create message digest. Fingerprint creation WILL fail.", e);
        }
    }

    public AdvancedPeripherals(IEventBus modBus) {
        LOGGER.info("AdvancedPeripherals says hello!");

        APAddon.setup();

        APConfig.register(ModLoadingContext.get());

        modBus.addListener(this::onLoadComplete);
        modBus.addListener(this::registerCapabilities);
        modBus.addListener(ChunkManager::registerTicketController);

        APRegistration.register(modBus);
    }

    @Nullable
    public static MessageDigest getFingerprintMessageDigest() {
        if (fingerprintMessageDigest != null) {
            fingerprintMessageDigest.reset();
        }
        return fingerprintMessageDigest;
    }

    public static void debug(String message, Object... params) {
        if (APConfig.GENERAL_CONFIG.enableDebugMode.get()) {
            LOGGER.info("[DEBUG] " + message, params);
        }
    }

    public static void debug(Level level, String message, Object... params) {
        if (APConfig.GENERAL_CONFIG.enableDebugMode.get()) {
            LOGGER.log(level, "[DEBUG] " + message, params);
        }
    }

    public static void debug(String message, Throwable throwable) {
        if (APConfig.GENERAL_CONFIG.enableDebugMode.get()) {
            LOGGER.error("[DEBUG] " + message, throwable);
        }
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
                (blockEntity, side) -> blockEntity instanceof ICapabilityProvider provider
                    ? provider.createPeripheralCap(side)
                    : null
            );
            event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                entry.get(),
                (blockEntity, side) -> blockEntity instanceof ICapabilityProvider provider
                    ? provider.createItemHandlerCap(side)
                    : null
            );
            event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                entry.get(),
                (blockEntity, side) -> blockEntity instanceof ICapabilityProvider provider
                    ? provider.createFluidHandlerCap(side)
                    : null
            );
            event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                entry.get(),
                (blockEntity, side) -> blockEntity instanceof ICapabilityProvider provider
                    ? provider.createEnergyStorageCap(side)
                    : null
            );
        });

        ItemLike[] smartGlasses = new ItemLike[]{
            APItems.SMART_GLASSES.get(),
            APItems.SMART_GLASSES_NETHERITE.get(),
        };
        event.registerItem(MediaCapability.get(), (stack, ignored) -> MountMedia.COMPUTER, smartGlasses);
        event.registerItem(
            Capabilities.ItemHandler.ITEM,
            (stack, ignored) -> ((SmartGlassesItem) stack.getItem()).createItemHandlerCap(stack),
            smartGlasses
        );
        if (APAddon.CURIOS.isLoaded()) {
            event.registerItem(
                CuriosCapability.ITEM,
                (stack, ignored) -> (ICurio) ((SmartGlassesItem) stack.getItem()).createCurioCap(stack),
                smartGlasses
            );
        }

        if (APAddon.AE2.isLoaded()) {
            AEApi.registerCapabilities(event);
        }
        if (APAddon.REFINEDSTORAGE.isLoaded()) {
            RSApi.registerCapabilities(event);
        }

        IntegrationPeripheralProvider.registerBlockIntegrations(event);
    }
}
