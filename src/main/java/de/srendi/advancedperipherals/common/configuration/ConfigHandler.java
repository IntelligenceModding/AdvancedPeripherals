package de.srendi.advancedperipherals.common.configuration;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.lib.LibConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigHandler {

    @SubscribeEvent
    public static void configEvent(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == ConfigHolder.COMMON_SPEC) {
            ConfigHandler.bakeCommon();
            LibConfig.reloadConfig();
        }
    }

    private static void bakeCommon() {
        //Defaults
        AdvancedPeripheralsConfig.defaultChatBoxPrefix = ConfigHolder.COMMON_CONFIG.DEFAULT_CHAT_BOX_PREFIX.get();

        //Restrictions
        AdvancedPeripheralsConfig.playerDetMaxRange = ConfigHolder.COMMON_CONFIG.PLAYER_DET_MAX_RANGE.get();
        AdvancedPeripheralsConfig.energyDetectorMaxFlow = ConfigHolder.COMMON_CONFIG.ENERGY_DETECTOR_MAX_FLOW.get();
        AdvancedPeripheralsConfig.poweredPeripheralMaxEnergyStored = ConfigHolder.COMMON_CONFIG.POWERED_PERIPHERAL_MAX_ENERGY_STORED.get();
        AdvancedPeripheralsConfig.nbtStorageMaxSize = ConfigHolder.COMMON_CONFIG.NBT_STORAGE_MAX_SIZE.get();
        AdvancedPeripheralsConfig.chunkLoadValidTime = ConfigHolder.COMMON_CONFIG.CHUNK_LOAD_VALID_TIME.get();

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
        AdvancedPeripheralsConfig.enableWeakAutomataCore = ConfigHolder.COMMON_CONFIG.ENABLE_WEAK_AUTOMATA_CORE.get();
        AdvancedPeripheralsConfig.enableEndAutomataCore = ConfigHolder.COMMON_CONFIG.ENABLE_END_AUTOMATA_CORE.get();
        AdvancedPeripheralsConfig.enableHusbandryAutomataCore = ConfigHolder.COMMON_CONFIG.ENABLE_HUSBANDRY_AUTOMATA_CORE.get();
        AdvancedPeripheralsConfig.endAutomataCoreWarpPointLimit = ConfigHolder.COMMON_CONFIG.END_AUTOMATA_CORE_WARP_POINT_LIMIT.get();
        AdvancedPeripheralsConfig.overpoweredAutomataBreakChance = ConfigHolder.COMMON_CONFIG.OVERPOWERED_AUTOMATA_BREAK_CHANCE.get();

        //World
        AdvancedPeripheralsConfig.givePlayerBookOnJoin = ConfigHolder.COMMON_CONFIG.GIVE_PLAYER_BOOK_ON_JOIN.get();
        AdvancedPeripheralsConfig.enableVillagerStructures = ConfigHolder.COMMON_CONFIG.ENABLE_VILLAGER_STRUCTURES.get();
    }
}
