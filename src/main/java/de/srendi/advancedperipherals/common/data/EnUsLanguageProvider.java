package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.common.setup.Items;
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
        super(gen, AdvancedPeripherals.ITEM_MOD_ID, "en_us");
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
        add("advancedperipherals.name", AdvancedPeripherals.NAME);
        add("itemGroup.advancedperipheralstab", AdvancedPeripherals.NAME);
    }

    private void addItems() {
        addItem(Items.CHUNK_CONTROLLER, "Chunk Controller");
    }

    private void addBlocks() {
    }

    private void addTurtles() {
        addTurtle(CCRegistration.ID.CHUNKY_TURTLE, "Chunky");
        addTurtle(CCRegistration.ID.COMPASS_TURTLE, "Compass");
    }

    private void addPockets() {
    }

    private void addAdvancements() {
    }

    private void addTooltips() {
        addTooltip("show_desc", "&b[&7%s&b] &7For Description");
        addTooltip("disabled", "&cThis item is disabled in the config, so you can craft it, but it'll not have any functionality.");
        addTooltip(Items.CHUNK_CONTROLLER.get(), "&7A crafting ingredient for the Chunky Turtle.");
    }

    private void addText() {
    }

    private void addKeybinds() {
        add("keybind.advancedperipherals.category", AdvancedPeripherals.NAME);
        addKeybind(KeyBindings.DESCRIPTION_KEYBINDING, "Show Description");
    }

    private void addAdvancement(@NotNull String advancement, @NotNull String name, @NotNull String description) {
        add("advancements." + AdvancedPeripherals.ITEM_MOD_ID + "." + advancement, name);
        add("advancements." + AdvancedPeripherals.ITEM_MOD_ID + "." + advancement + ".description", description);
    }

    private void add(@NotNull Supplier<VillagerProfession> key, @NotNull String name) {
        add("entity.minecraft.villager." + AdvancedPeripherals.ITEM_MOD_ID + "." + key.get().name().split(":")[1], name);
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
        add("item." + AdvancedPeripherals.ITEM_MOD_ID + ".tooltip." + tooltip, name);
    }

    private void addKeybind(@NotNull KeyMapping keybind, String name) {
        add(keybind.getName(), name);
    }

}
