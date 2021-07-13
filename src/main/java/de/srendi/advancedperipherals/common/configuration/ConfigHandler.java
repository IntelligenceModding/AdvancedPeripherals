package de.srendi.advancedperipherals.common.configuration;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigHandler {

    @SubscribeEvent
    public static void configEvent(final ModConfig.ModConfigEvent event) {
        if (event.getConfig().getSpec() == ConfigHolder.COMMON_SPEC) {
            ConfigHandler.bakeCommon();
        }
    }

    @SubscribeEvent
    public static void reloadConfigEvent(final ModConfig.Reloading event) {
        if (event.getConfig().getSpec() == ConfigHolder.COMMON_SPEC) {
            ConfigHandler.bakeCommon();
        }
    }

    private static void bakeCommon() {
        //Defaults
        AdvancedPeripheralsConfig.defaultChatBoxPrefix = ConfigHolder.COMMON_CONFIG.DEFAULT_CHAT_BOX_PREFIX.get();

        //Restrictions
        AdvancedPeripheralsConfig.chatBoxCooldown = ConfigHolder.COMMON_CONFIG.CHAT_BOX_COOLDOWN.get();
        AdvancedPeripheralsConfig.playerDetMaxRange = ConfigHolder.COMMON_CONFIG.PLAYER_DET_MAX_RANGE.get();
        AdvancedPeripheralsConfig.energyDetectorMaxFlow = ConfigHolder.COMMON_CONFIG.ENERGY_DETECTOR_MAX_FLOW.get();
        AdvancedPeripheralsConfig.geoScannerMaxFreeRadius = ConfigHolder.COMMON_CONFIG.GEO_SCANNER_MAX_FREE_RADIUS.get();
        AdvancedPeripheralsConfig.geoScannerMaxCostRadius = ConfigHolder.COMMON_CONFIG.GEO_SCANNER_MAX_COST_RADIUS.get();
        AdvancedPeripheralsConfig.geoScannerExtraBlockCost = ConfigHolder.COMMON_CONFIG.GEO_SCANNER_EXTRA_BLOCK_COST.get();
        AdvancedPeripheralsConfig.geoScannerMaxEnergyStored = ConfigHolder.COMMON_CONFIG.GEO_SCANNER_MAX_ENERGY_STORED.get();
        AdvancedPeripheralsConfig.geoScannerMinScanPeriod = ConfigHolder.COMMON_CONFIG.GEO_SCANNER_MIN_SCAN_PERIOD.get();
        AdvancedPeripheralsConfig.environmentDetectorMaxCostRadius = ConfigHolder.COMMON_CONFIG.ENVIRONMENT_DETECTOR_MAX_COST_RADIUS.get();
        AdvancedPeripheralsConfig.environmentDetectorExtraBlockCost = ConfigHolder.COMMON_CONFIG.ENVIRONMENT_DETECTOR_EXTRA_BLOCK_COST.get();
        AdvancedPeripheralsConfig.environmentDetectorMaxEnergyStored = ConfigHolder.COMMON_CONFIG.ENVIRONMENT_DETECTOR_MAX_ENERGY_STORED.get();
        AdvancedPeripheralsConfig.environmentDetectorMaxFreeRadius = ConfigHolder.COMMON_CONFIG.ENVIRONMENT_DETECTOR_MAX_FREE_RADIUS.get();
        AdvancedPeripheralsConfig.environmentDetectorMinScanPeriod = ConfigHolder.COMMON_CONFIG.ENVIRONMENT_DETECTOR_MIN_SCAN_PERIOD.get();
        AdvancedPeripheralsConfig.nbtStorageMaxSize = ConfigHolder.COMMON_CONFIG.NBT_STORAGE_MAX_SIZE.get();

        //Features
        AdvancedPeripheralsConfig.enableChatBox = ConfigHolder.COMMON_CONFIG.ENABLE_CHAT_BOX.get();
        AdvancedPeripheralsConfig.enableMeBridge = ConfigHolder.COMMON_CONFIG.ENABLE_ME_BRIDGE.get();
        AdvancedPeripheralsConfig.enableRsBridge = ConfigHolder.COMMON_CONFIG.ENABLE_RS_BRIDGE.get();
        AdvancedPeripheralsConfig.enablePlayerDetector = ConfigHolder.COMMON_CONFIG.ENABLE_PLAYER_DETECTOR.get();
        AdvancedPeripheralsConfig.enableEnvironmentDetector = ConfigHolder.COMMON_CONFIG.ENABLE_ENVIRONMENT_DETECTOR.get();
        AdvancedPeripheralsConfig.enableChunkyTurtle = ConfigHolder.COMMON_CONFIG.ENABLE_CHUNKY_TURTLE.get();
        AdvancedPeripheralsConfig.enableDebugMode = ConfigHolder.COMMON_CONFIG.ENABLE_DEBUG_MODE.get();
        AdvancedPeripheralsConfig.enableEnergyDetector = ConfigHolder.COMMON_CONFIG.ENABLE_ENERGY_DETECTOR.get();
        AdvancedPeripheralsConfig.enableARGoggles = ConfigHolder.COMMON_CONFIG.ENABLE_AR_GOGGLES.get();
        AdvancedPeripheralsConfig.enableInventoryManager = ConfigHolder.COMMON_CONFIG.ENABLE_INVENTORY_MANAGER.get();
        AdvancedPeripheralsConfig.enableRedstoneIntegrator = ConfigHolder.COMMON_CONFIG.ENABLE_REDSTONE_INTEGRATOR.get();
        AdvancedPeripheralsConfig.enableBlockReader = ConfigHolder.COMMON_CONFIG.ENABLE_BLOCK_READER.get();
        AdvancedPeripheralsConfig.enableGeoScanner = ConfigHolder.COMMON_CONFIG.ENABLE_GEO_SCANNER.get();
        AdvancedPeripheralsConfig.enableColonyIntegrator = ConfigHolder.COMMON_CONFIG.ENABLE_COLONY_INTEGRATOR.get();
        AdvancedPeripheralsConfig.enableNBTStorage = ConfigHolder.COMMON_CONFIG.ENABLE_NBT_STORAGE.get();
        AdvancedPeripheralsConfig.enablePoweredPeripherals = ConfigHolder.COMMON_CONFIG.ENABLE_POWERED_PERIPHERALS.get();

        //Mechanical soul
        AdvancedPeripheralsConfig.energyToFuelRate = ConfigHolder.COMMON_CONFIG.ENERGY_TO_FUEL_RATE.get();
        AdvancedPeripheralsConfig.suckItemCost = ConfigHolder.COMMON_CONFIG.SUCK_ITEM_COST.get();
        AdvancedPeripheralsConfig.suckItemCooldown = ConfigHolder.COMMON_CONFIG.SUCK_ITEM_COOLDOWN.get();
        AdvancedPeripheralsConfig.digBlockCost = ConfigHolder.COMMON_CONFIG.DIG_BLOCK_COST.get();
        AdvancedPeripheralsConfig.digBlockCooldown = ConfigHolder.COMMON_CONFIG.DIG_BLOCK_COOLDOWN.get();
        AdvancedPeripheralsConfig.clickBlockCost = ConfigHolder.COMMON_CONFIG.USE_ON_BLOCK_COST.get();
        AdvancedPeripheralsConfig.useOnBlockCooldown = ConfigHolder.COMMON_CONFIG.USE_ON_BLOCK_COOLDOWN.get();
        AdvancedPeripheralsConfig.warpCooldown = ConfigHolder.COMMON_CONFIG.WARP_COOLDOWN.get();
        AdvancedPeripheralsConfig.useOnAnimalCost = ConfigHolder.COMMON_CONFIG.USE_ON_ANIMAL_COST.get();
        AdvancedPeripheralsConfig.useOnAnimalCooldown = ConfigHolder.COMMON_CONFIG.USE_ON_ANIMAL_COOLDOWN.get();
        AdvancedPeripheralsConfig.captureAnimalCost = ConfigHolder.COMMON_CONFIG.CAPTURE_ANIMAL_COST.get();
        AdvancedPeripheralsConfig.captureAnimalCooldown = ConfigHolder.COMMON_CONFIG.CAPTURE_ANIMAL_COOLDOWN.get();
        AdvancedPeripheralsConfig.enableWeakAutomataCore = ConfigHolder.COMMON_CONFIG.ENABLE_WEAK_AUTOMATA_CORE.get();
        AdvancedPeripheralsConfig.weakAutomataCoreInteractionRadius = ConfigHolder.COMMON_CONFIG.WEAK_AUTOMATA_CORE_TURTLE_INTERACTION_RANGE.get();
        AdvancedPeripheralsConfig.weakAutomataCoreMaxFuelConsumptionLevel = ConfigHolder.COMMON_CONFIG.WEAK_AUTOMATA_CORE_TURTLE_MAX_FUEL_CONSUMPTION_LEVEL.get();
        AdvancedPeripheralsConfig.enableEndAutomataCore = ConfigHolder.COMMON_CONFIG.ENABLE_END_AUTOMATA_CORE.get();
        AdvancedPeripheralsConfig.endAutomataCoreInteractionRadius = ConfigHolder.COMMON_CONFIG.END_AUTOMATA_CORE_TURTLE_INTERACTION_RANGE.get();
        AdvancedPeripheralsConfig.endAutomataCoreMaxFuelConsumptionLevel = ConfigHolder.COMMON_CONFIG.END_AUTOMATA_CORE_TURTLE_MAX_FUEL_CONSUMPTION_LEVEL.get();
        AdvancedPeripheralsConfig.enableHusbandryAutomataCore = ConfigHolder.COMMON_CONFIG.ENABLE_HUSBANDRY_AUTOMATA_CORE.get();
        AdvancedPeripheralsConfig.husbandryAutomataCoreInteractionRadius = ConfigHolder.COMMON_CONFIG.HUSBANDRY_AUTOMATA_CORE_TURTLE_INTERACTION_RANGE.get();
        AdvancedPeripheralsConfig.husbandryAutomataCoreMaxFuelConsumptionLevel = ConfigHolder.COMMON_CONFIG.HUSBANDRY_AUTOMATA_CORE_TURTLE_MAX_FUEL_CONSUMPTION_LEVEL.get();

        AdvancedPeripheralsConfig.enableVillagerStructures = ConfigHolder.COMMON_CONFIG.ENABLE_VILLAGER_STRUCTURES.get();
    }
}
