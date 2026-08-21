package de.srendi.advancedperipherals;

import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.ae2.AE2Registries;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.setup.APRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod(AdvancedPeripherals.MOD_ID)
@Mod.EventBusSubscriber
public class AdvancedPeripherals {

    public static final String MOD_ID = "advancedperipherals";
    public static final String NAME = "Advanced Peripherals";
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final Random RANDOM = new Random();

    public AdvancedPeripherals(IEventBus modBus) {
        LOGGER.info("AdvancedPeripherals says hello!");

        APAddon.setup();

        APConfig.register(ModLoadingContext.get());

        modBus.addListener(this::onLoadComplete);

        APRegistration.register(modBus);

        if (APAddon.AE2.isLoaded()) {
            modBus.addListener(AE2Registries::onRegister);
        }
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
        return new ResourceLocation(MOD_ID, resource);
    }

    public void onLoadComplete(FMLLoadCompleteEvent event) {
        if (APAddon.AE2.isLoaded()) {
            event.enqueueWork(AE2Registries::finishRegister);
        }
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        ItemStack stack = event.getFrom();
        ItemStack newStack = event.getTo();
        if (stack.getItem() instanceof SmartGlassesItem glassesItem) {
            if (newStack.getItem() == glassesItem && glassesItem.getComputerID(stack) == glassesItem.getComputerID(newStack)) {
                return;
            }
            glassesItem.onUnequip(stack, (ServerLevel) event.getEntity().level(), event.getEntity());
        }
    }
}
