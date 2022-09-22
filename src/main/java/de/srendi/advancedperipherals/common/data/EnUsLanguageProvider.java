package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class EnUsLanguageProvider extends LanguageProvider {

    public EnUsLanguageProvider(DataGenerator gen) {
        super(gen, AdvancedPeripherals.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addBlocks();
        addGuis();
        addTooltips();
        add("itemGroup." + AdvancedPeripherals.MOD_ID, "Advanced Peripherals");
    }

    private void addItems() {
        add(Items.CHUNK_CONTROLLER.get(), "Chunk Controller");
        add(Items.COMPUTER_TOOL.get(), "Computer Tool");
        add(Items.WEAK_AUTOMATA_CORE.get(), "Weak Automata Core");
        add(Items.HUSBANDRY_AUTOMATA_CORE.get(), "Husbandry Automata Core");
        add(Items.END_AUTOMATA_CORE.get(), "End Automata Core");
        add(Items.OVERPOWERED_WEAK_AUTOMATA_CORE.get(), "Overpowered Weak Automata Core");
        add(Items.OVERPOWERED_HUSBANDRY_AUTOMATA_CORE.get(), "Overpowered Husbandry Automata Core");
        add(Items.OVERPOWERED_END_AUTOMATA_CORE.get(), "Overpowered End Automata Core");
        add(Items.MEMORY_CARD.get(), "Memory Card");
    }

    private void addBlocks() {
        add(Blocks.BLOCK_READER.get(), "Block Reader");
        add(Blocks.CHAT_BOX.get(), "Chat Box");
        add(Blocks.AR_CONTROLLER.get(), "Ar Controller");
        add(Blocks.COLONY_INTEGRATOR.get(), "Colony Integrator");
        add(Blocks.ENERGY_DETECTOR.get(), "Energy Detector");
        add(Blocks.ENVIRONMENT_DETECTOR.get(), "Environment Detector");
        add(Blocks.GEO_SCANNER.get(), "Geo Scanner");
        add(Blocks.INVENTORY_MANAGER.get(), "Inventory Manager");
        add(Blocks.ME_BRIDGE.get(), "ME Bridge");
        add(Blocks.RS_BRIDGE.get(), "RS Bridge");
        add(Blocks.NBT_STORAGE.get(), "NBT Storage");
        add(Blocks.PERIPHERAL_CASING.get(), "Peripheral Casing");
        add(Blocks.PLAYER_DETECTOR.get(), "Player Detector");
        add(Blocks.REDSTONE_INTEGRATOR.get(), "Redstone Integrator");

    }

    private void addGuis() {

    }

    private void addTooltips() {

    }

}