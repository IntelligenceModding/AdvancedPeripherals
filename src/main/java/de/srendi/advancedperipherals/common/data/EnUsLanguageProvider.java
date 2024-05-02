package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.common.setup.Items;
import de.srendi.advancedperipherals.common.setup.Villagers;
import net.minecraft.client.KeyMapping;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.ForgeRegistries;
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
        addAdvancements();
        addTooltips();
        addKeybinds();
        addText();
        add(Villagers.COMPUTER_SCIENTIST, "Computer Scientist");
        add("advancedperipherals.name", AdvancedPeripherals.NAME);
        add("itemGroup.advancedperipheralstab", AdvancedPeripherals.NAME);
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
        addAdvancement("root", AdvancedPeripherals.NAME, "Every journey starts with the first block");
        addAdvancement("weak_automata_core", "First automata core", "Does the afterlife exist in minecraft?");
        addAdvancement("end_automata_core", "End automata core", "If you can code gps-free position location with this, you're a powerful human being");
        addAdvancement("husbandry_automata_core", "Husbandry automata core", "Is this core gluten-free?");
        addAdvancement("overpowered_automata_core", "Overpowered automata core", "Can you handle so much power?");
        addAdvancement("base_toolkit", "Gentleman's set!", "Collect a redstone integrator, inventory manager and energy detector. How did you even play without this?");
        addAdvancement("nbt_toolkit", "No secrets", "Collect a NBT storage and block reader. Now, all the world's secrets are open to you!");
        addAdvancement("sense_toolkit", "The truth can't hide forever", "Collect a geo scanner and environmental detector. There are no limits for observability!");
    }

    private void addTooltips() {
        addTooltip("show_desc", "&b[&7%s&b] &7For Description");
        addTooltip("disabled", "&cThis item is disabled in the config, so you can craft it, but it'll not have any functionality.");
        addTooltip(Items.COMPUTER_TOOL.get(), "&7This tool was made to tune our blocks. But for now, it's just a blue useless wrench.");
        addTooltip(Blocks.ENERGY_DETECTOR.get(), "&7Can detect energy flow and acts as a resistor.");
        addTooltip(Items.CHUNK_CONTROLLER.get(), "&7A crafting ingredient for the Chunky Turtle.");
        addTooltip(Blocks.ENVIRONMENT_DETECTOR.get(), "&7This peripheral interacts with the minecraft world.");
        addTooltip(Blocks.PLAYER_DETECTOR.get(), "&7This peripheral can be used to interact with players, but don't be a stalker.");
        addTooltip(Blocks.RS_BRIDGE.get(), "&7The RS Bridge interacts with Refined Storage to manage your items.");
        addTooltip(Blocks.ME_BRIDGE.get(), "&7The ME Bridge interacts with Applied Energistics to manage your items.");
        addTooltip(Blocks.CHAT_BOX.get(), "&7Interacts with the ingame chat, can read and write messages.");
        addTooltip(Blocks.PERIPHERAL_CASING.get(), "&7An empty hull without the love it deserves. Used as a crafting ingredient");
        addTooltip(Items.MEMORY_CARD.get(), "&7Can save the rights of a player to use it in an inventory manager.");
        addTooltip("memory_card.bound", "&7Bound to &b%s&7.");
        addTooltip(Blocks.INVENTORY_MANAGER.get(), "&7This block is able to send or receive specific items from a player inventory.");
        addTooltip(Blocks.REDSTONE_INTEGRATOR.get(), "&7This block is able to interact with redstone. Works exactly like the redstone api of an computer.");
        addTooltip(Blocks.BLOCK_READER.get(), "&7Reads nbt data of blocks to interact with blocks which do not have computer support.");
        addTooltip(Blocks.GEO_SCANNER.get(), "&7Scans the area around it to find some shiny ores.");
        addTooltip(Blocks.COLONY_INTEGRATOR.get(), "&7Interacts with Minecolonies to read data about your colony and citizens.");
        addTooltip(Blocks.NBT_STORAGE.get(), "&7Acts like a storage disk. Can store nbt based data.");
        addTooltip(Items.WEAK_AUTOMATA_CORE.get(), "&7Upgrade for turtles, which makes turtles more useful.");
        addTooltip(Items.OVERPOWERED_WEAK_AUTOMATA_CORE.get(), "&7Improved version of the weak automata core, that provides some overpowered uses! Be careful, the upgrade is very fragile.");
        addTooltip(Items.HUSBANDRY_AUTOMATA_CORE.get(), "&7Upgrade for turtles, that allows basic and advanced interactions with animals.");
        addTooltip(Items.OVERPOWERED_HUSBANDRY_AUTOMATA_CORE.get(), "&7Improved version of the husbandry automata core, that provides some overpowered uses! Be careful, the upgrade is very fragile.");
        addTooltip(Items.END_AUTOMATA_CORE.get(), "&7Upgrade for turtles, that allows basic interaction with the world and teleportation in one dimension.");
        addTooltip(Items.OVERPOWERED_END_AUTOMATA_CORE.get(), "&7Improved version of the end automata core, that provides some overpowered uses! Be careful, the upgrade is very fragile.");
    }

    private void addText() {
        add("text." + AdvancedPeripherals.MOD_ID + ".removed_player", "Cleared the memory card");
        add("text." + AdvancedPeripherals.MOD_ID + ".added_player", "Added you to the memory card");
        add("text." + AdvancedPeripherals.MOD_ID + ".automata_core_feed_by_player", "You're trying to feed an entity to a soul, but your own body refuses to do this. Maybe something more mechanical can do this?");
    }

    private void addKeybinds() {
        add("keybind.advancedperipherals.category", AdvancedPeripherals.NAME);
        addKeybind(KeyBindings.DESCRIPTION_KEYBINDING, "Show Description");
    }

    private void addAdvancement(@NotNull String advancement, @NotNull String name, @NotNull String description) {
        add("advancements." + AdvancedPeripherals.MOD_ID + "." + advancement, name);
        add("advancements." + AdvancedPeripherals.MOD_ID + "." + advancement + ".description", description);
    }

    private void add(@NotNull Supplier<VillagerProfession> key, @NotNull String name) {
        add("entity.minecraft.villager." + AdvancedPeripherals.MOD_ID + "." + key.get().name().split(":")[1], name);
    }

    private void addTurtle(@NotNull ResourceLocation key, @NotNull String name) {
        add("turtle." + key.getNamespace() + "." + key.getPath(), name);
    }

    private void addPocket(@NotNull ResourceLocation key, @NotNull String name) {
        add("pocket." + key.getNamespace() + "." + key.getPath(), name);
    }

    private void addTooltip(Item item, String name) {
        addTooltip(ForgeRegistries.ITEMS.getKey(item).getPath(), name);
    }

    private void addTooltip(Block block, String name) {
        addTooltip(ForgeRegistries.BLOCKS.getKey(block).getPath(), name);
    }

    private void addTooltip(String tooltip, String name) {
        add("item." + AdvancedPeripherals.MOD_ID + ".tooltip." + tooltip, name);
    }

    private void addKeybind(@NotNull KeyMapping keybind, String name) {
        add(keybind.getName(), name);
    }

}
