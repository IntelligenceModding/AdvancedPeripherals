package de.srendi.advancedperipherals;

import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.ae2.AEApi;
import de.srendi.advancedperipherals.common.addons.ae2.AE2Registries;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RSApi;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.setup.APRegistration;
import de.srendi.advancedperipherals.common.util.ChunkManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Mod(AdvancedPeripherals.MOD_ID)
@EventBusSubscriber
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
        if (APAddon.AE2.isLoaded()) {
            AEApi.registerCapabilities(event);
        }
        if (APAddon.REFINEDSTORAGE.isLoaded()) {
            RSApi.registerCapabilities(event);
        }

        IntegrationPeripheralProvider.registerBlockIntegrations(event);
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        ItemStack stack = event.getFrom();
        ItemStack newStack = event.getTo();
        if (stack.getItem() instanceof SmartGlassesItem glassesItem) {
            if (newStack.getItem() == glassesItem && SmartGlassesItem.getComputerID(stack) == SmartGlassesItem.getComputerID(newStack)) {
                return;
            }
            glassesItem.onUnequip(stack, (ServerLevel) event.getEntity().level(), event.getEntity());
        }
    }
}
