package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.setup.APItems;
import de.srendi.advancedperipherals.common.setup.APTranslations;
import de.srendi.advancedperipherals.common.setup.APVillagers;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import de.srendi.advancedperipherals.lib.annotation.DefaultTooltip;
import de.srendi.advancedperipherals.lib.annotation.DefaultTranslation;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;

public class EnUsLanguageProvider extends LanguageProvider {

    public EnUsLanguageProvider(PackOutput gen) {
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
        addVillager(APVillagers.COMPUTER_SCIENTIST.get().name(), "Computer Scientist");
        forEachField(APTranslations.class, String.class, DefaultTranslation.class, (key, tr) -> add(key, tr.value()));

        add("curios.identifier.glasses", "Glasses");
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
        forEachField(APBlocks.class, DeferredHolder.class, DefaultTooltip.class, (block, tr) -> addTooltip((Block) block.get(), tr.value()));
        forEachField(APItems.class, DeferredHolder.class, DefaultTooltip.class, (item, tr) -> addTooltip((Item) item.get(), tr.value()));
    }

    private void addKeybinds() {
        add("keybind.advancedperipherals.category", AdvancedPeripherals.NAME);
        forEachField(KeyBindings.class, KeyMapping.class, DefaultTranslation.class, (key, tr) -> addKeybind(key, tr.value()));
    }

    private void addAdvancement(@NotNull String advancement, @NotNull String value, @NotNull String description) {
        add("advancements." + AdvancedPeripherals.MOD_ID + "." + advancement, value);
        add("advancements." + AdvancedPeripherals.MOD_ID + "." + advancement + ".description", description);
    }

    private void addVillager(@NotNull String key, @NotNull String value) {
        add(ResourceLocation.parse(key).toLanguageKey("entity.minecraft.villager"), value);
    }

    private void addTurtle(@NotNull ResourceLocation key, @NotNull String value) {
        add(TranslationUtil.turtle(key), value);
    }

    private void addPocket(@NotNull ResourceLocation key, @NotNull String value) {
        add(TranslationUtil.pocket(key), value);
    }

    private void addTooltip(Item item, String value) {
        addTooltip("item.", BuiltInRegistries.ITEM.getKey(item), value);
    }

    private void addTooltip(Block block, String value) {
        addTooltip("block.", BuiltInRegistries.BLOCK.getKey(block), value);
    }

    private void addTooltip(String prefix, ResourceLocation key, String value) {
        add(TranslationUtil.tooltip(prefix + key.getNamespace() + "." + key.getPath()), value);
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
