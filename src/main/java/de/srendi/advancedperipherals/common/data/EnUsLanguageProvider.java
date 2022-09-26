package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.common.setup.Items;
import de.srendi.advancedperipherals.common.setup.Villagers;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class EnUsLanguageProvider extends LanguageProvider {

    public EnUsLanguageProvider(DataGenerator gen) {
        super(gen, AdvancedPeripherals.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addBlocks();
        addItems();
        addTurtles();
        addPockets();
        add(Villagers.COMPUTER_SCIENTIST, "Computer Scientist");
        add("itemGroup." + AdvancedPeripherals.TAB.getRecipeFolderName(), "Advanced Peripherals");
    }

    private void addItems() {
        addItem(Items.CHUNK_CONTROLLER, "Chunk Controller");
        addItem(Items.COMPUTER_TOOL, "Computer Tool");
        addItem(Items.WEAK_AUTOMATA_CORE, "Weak Automata Core");
        addItem(Items.HUSBANDRY_AUTOMATA_CORE, "Husbandry Automata Core");
        addItem(Items.END_AUTOMATA_CORE, "End Automata Core");
        addItem(Items.OVERPOWERED_WEAK_AUTOMATA_CORE, "Overpowered Weak Automata Core");
        addItem(Items.OVERPOWERED_HUSBANDRY_AUTOMATA_CORE, "Overpowered Husbandry Automata Core");
        addItem(Items.OVERPOWERED_END_AUTOMATA_CORE, "Overpowered End Automata Core");
        addItem(Items.MEMORY_CARD, "Memory Card");
    }

    private void addBlocks() {
        addBlock(Blocks.BLOCK_READER, "Block Reader");
        addBlock(Blocks.CHAT_BOX, "Chat Box");
        addBlock(Blocks.AR_CONTROLLER, "Ar Controller");
        addBlock(Blocks.COLONY_INTEGRATOR, "Colony Integrator");
        addBlock(Blocks.ENERGY_DETECTOR, "Energy Detector");
        addBlock(Blocks.ENVIRONMENT_DETECTOR, "Environment Detector");
        addBlock(Blocks.GEO_SCANNER, "Geo Scanner");
        addBlock(Blocks.INVENTORY_MANAGER, "Inventory Manager");
        addBlock(Blocks.ME_BRIDGE, "ME Bridge");
        addBlock(Blocks.RS_BRIDGE, "RS Bridge");
        addBlock(Blocks.NBT_STORAGE, "NBT Storage");
        addBlock(Blocks.PERIPHERAL_CASING, "Peripheral Casing");
        addBlock(Blocks.PLAYER_DETECTOR, "Player Detector");
        addBlock(Blocks.REDSTONE_INTEGRATOR, "Redstone Integrator");

    }

    private void addTurtles() {
        addTurtle(CCRegistration.ID.CHUNKY_TURTLE, "Chunky");
        addTurtle(CCRegistration.ID.CHATTY_TURTLE, "Chatty");
        addTurtle(CCRegistration.ID.ENVIRONMENT_TURTLE, "Environment");
        addTurtle(CCRegistration.ID.PLAYER_TURTLE, "Player Detector");
        addTurtle(CCRegistration.ID.GEOSCANNER_TURTLE, "Geo");
        addTurtle(CCRegistration.ID.COMPASS_TURTLE, "Compass");
        addTurtle(CCRegistration.ID.WEAK_AUTOMATA, "Weak automata");
        addTurtle(CCRegistration.ID.HUSBANDRY_AUTOMATA, "Husbandry automata");
        addTurtle(CCRegistration.ID.END_AUTOMATA, "End automata");
        addTurtle(CCRegistration.ID.OP_WEAK_AUTOMATA, "Overpowered weak automata");
        addTurtle(CCRegistration.ID.OP_HUSBANDRY_AUTOMATA, "Overpowered husbandry automata");
        addTurtle(CCRegistration.ID.OP_END_AUTOMATA, "Overpowered end automata");

    }

    private void addPockets() {
        addPocket(CCRegistration.ID.COLONY_POCKET, "Colony");
        addPocket(CCRegistration.ID.CHATTY_POCKET, "Chatty");
        addPocket(CCRegistration.ID.ENVIRONMENT_POCKET, "Environment");
        addPocket(CCRegistration.ID.GEOSCANNER_POCKET, "Geo");
        addPocket(CCRegistration.ID.PLAYER_POCKET, "Player Detector");
    }

    private void addAdvancements() {
        addAdvancement("root", "Advanced Peripherals", "Every journey starts with the first block");
        addAdvancement("weak_automata_core", "First automata core", "Does the afterlife exist in minecraft?");

    }

    private void addAdvancement(@NotNull String advancement, @NotNull String name, @NotNull String description) {
        add("advancements." + AdvancedPeripherals.MOD_ID + "." + advancement, name);
        add("advancements." + AdvancedPeripherals.MOD_ID + "." + advancement + ".description", description);
    }

    private void add(@NotNull Supplier<VillagerProfession> key, @NotNull String name) {
        add("entity.minecraft.villager." + AdvancedPeripherals.MOD_ID + "." + key.get().name(), name);
    }

    private void addTurtle(@NotNull ResourceLocation key, @NotNull String name) {
        add("turtle." + key.getNamespace() + "." + key.getPath(), name);
    }

    private void addPocket(@NotNull ResourceLocation key, @NotNull String name) {
        add("pocket." + key.getNamespace() + "." + key.getPath(), name);
    }

}
