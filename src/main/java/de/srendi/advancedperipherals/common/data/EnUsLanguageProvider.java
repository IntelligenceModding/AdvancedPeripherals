package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.setup.APItems;
import de.srendi.advancedperipherals.common.setup.APVillagers;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.lib.annotation.DefaultTooltip;
import de.srendi.advancedperipherals.lib.annotation.DefaultTranslation;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class EnUsLanguageProvider extends LanguageProvider {

    public EnUsLanguageProvider(PackOutput gen) {
        super(gen, AdvancedPeripherals.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("advancedperipherals.name", AdvancedPeripherals.NAME);
        add("itemGroup.advancedperipheralstab", AdvancedPeripherals.NAME);
        add("curios.identifier.glasses", "Glasses");
        addBlocks();
        addItems();
        addTurtles();
        addPockets();
        addAdvancements();
        addTooltips();
        addKeybinds();
        addTexts();
        add(APVillagers.COMPUTER_SCIENTIST, "Computer Scientist");
    }

    private void addBlocks() {
        forEachField(APBlocks.class, DeferredHolder.class, DefaultTranslation.class, (block, tr) -> addBlock(block, tr.value()));
    }

    private void addItems() {
        forEachField(APItems.class, DeferredHolder.class, DefaultTranslation.class, (item, tr) -> addItem(item, tr.value()));
    }

    private void addTurtles() {
        forEachField(CCRegistration.ID.Turtle.class, ResourceLocation.class, DefaultTranslation.class, (id, tr) -> addTurtle(id, tr.value()));
    }

    private void addPockets() {
        forEachField(CCRegistration.ID.Pocket.class, ResourceLocation.class, DefaultTranslation.class, (id, tr) -> addPocket(id, tr.value()));
    }

    private void addAdvancements() {
        addAdvancement("root", AdvancedPeripherals.NAME, "Every journey starts with the first block");

        addAdvancement("base_toolkit", "Gentleman's set!", "Collect a redstone integrator, inventory manager and energy detector. How did you even play without this?");
        addAdvancement("nbt_toolkit", "No secrets", "Collect a NBT storage and block reader. Now, all the world's secrets are open to you!");
        addAdvancement("sense_toolkit", "The truth can't hide forever", "Collect a geo scanner and environmental detector. There are no limits for observability!");

        addAdvancement("end_automata_core", "End Automata Core", "If you can code gps-free position location with this, you're a powerful human being");
        addAdvancement("husbandry_automata_core", "Husbandry Automata Core", "Is this core gluten-free?");
        addAdvancement("overpowered_automata_core", "Overpowered Automata Core", "Can you handle so much power?");
        addAdvancement("weak_automata_core", "First Automata Core", "Does the afterlife exist in minecraft?");
    }

    private void addTooltips() {
        addTooltip("item.", "show_desc", "&b[&7%s&b] &7For Description");
        addTooltip("item.", "disabled", "&cThis item is disabled in config, so you can craft it, but it'll not have any functionality.");

        forEachField(APBlocks.class, DeferredHolder.class, DefaultTooltip.class, (block, tr) -> addTooltip((Block) block.get(), tr.value()));
        forEachField(APItems.class, DeferredHolder.class, DefaultTooltip.class, (item, tr) -> addTooltip((Item) item.get(), tr.value()));

        addTooltip("item.", "keyboard.binding.bound_to", "&7Bound to &b%s&7.");
        addTooltip("item.", "memory_card.bound", "&7Bound to &b%s&7.");
    }

    private void addTexts() {
        addText("automata_core.feed_by_player", "You're trying to feed an entity to a soul, but your own body refuses to do this. Maybe something more mechanical can do this?");
        addText("keyboard.close", "Press ESC to close the Keyboard Screen");
        addText("cleared_memorycard", "Cleared the memory card");
        addText("bind_memorycard", "Bounded the memory card to you");
        addText("keyboard_notbound", "The keyboard it not bound");
        addText("bind_keyboard", "Bounded the keyboard to %s");
        addText("cleared_keyboard", "Cleared the keyboard");
        addText("smart_glasses.peripherals", "Peripherals");
        addText("smart_glasses.modules", "Modules");
        addText("saddle_turtle.dismount_hint", "Controlling %1$s. Press %2$s and %3$s to dismount.");
    }

    private void addKeybinds() {
        add("keybind.advancedperipherals.category", AdvancedPeripherals.NAME);
        addKeybind(KeyBindings.DESCRIPTION_KEYBINDING, "Show Description");
    }

    private void addAdvancement(@NotNull String advancement, @NotNull String value, @NotNull String description) {
        add("advancements." + AdvancedPeripherals.MOD_ID + "." + advancement, value);
        add("advancements." + AdvancedPeripherals.MOD_ID + "." + advancement + ".description", description);
    }

    private void add(@NotNull Supplier<VillagerProfession> key, @NotNull String value) {
        add(ResourceLocation.parse(key.get().name()).toLanguageKey("entity.minecraft.villager"), value);
    }

    private void addText(String key, String value) {
        add("text." + AdvancedPeripherals.MOD_ID + "." + key, value);
    }

    private void addTurtle(@NotNull ResourceLocation key, @NotNull String value) {
        add("turtle." + key.getNamespace() + "." + key.getPath(), value);
    }

    private void addPocket(@NotNull ResourceLocation key, @NotNull String value) {
        add("pocket." + key.getNamespace() + "." + key.getPath(), value);
    }

    private void addTooltip(Item item, String value) {
        addTooltip("item.", BuiltInRegistries.ITEM.getKey(item).getPath(), value);
    }

    private void addTooltip(Block block, String value) {
        addTooltip("block.", BuiltInRegistries.BLOCK.getKey(block).getPath(), value);
    }

    private void addTooltip(String prefix, String tooltip, String value) {
        add(prefix + AdvancedPeripherals.MOD_ID + ".tooltip." + tooltip, value);
    }

    private void addKeybind(@NotNull KeyMapping keybind, String value) {
        add(keybind.getName(), value);
    }

    private static <T, U, A extends Annotation> void forEachField(
        Class<T> clazz,
        Class<U> fieldClazz,
        Class<A> annotationClazz,
        BiConsumer<U, A> consumer
    ) {
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.canAccess(null)) {
                continue;
            }
            if (!fieldClazz.isAssignableFrom(field.getType())) {
                continue;
            }
            Object value;
            try {
                value = field.get(null);
            } catch (IllegalAccessException e) {
                continue;
            }
            A tr = field.getDeclaredAnnotation(annotationClazz);
            if (tr != null) {
                consumer.accept((U) value, tr);
            }
        }
    }
}
