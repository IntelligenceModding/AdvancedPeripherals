package de.srendi.advancedperipherals;

import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import dan200.computercraft.shared.turtle.items.TurtleItem;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.setup.APRegistration;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public class APCreativeTab {

    public static void populateCreativeTabBuilder(CreativeModeTab.Builder builder) {
        builder
            .icon(() -> new ItemStack(APBlocks.CHAT_BOX.get()))
            .title(Component.translatable("advancedperipherals.name"))
            .displayItems((set, out) -> {
                APRegistration.ITEMS.getEntries().stream().map(RegistryObject::get).forEach(out::accept);

                forEachID(CCRegistration.ID.Turtle.class, (id) -> {
                    addTurtle(out, ModRegistry.Items.TURTLE_NORMAL.get(), id);
                    addTurtle(out, ModRegistry.Items.TURTLE_ADVANCED.get(), id);
                });
                forEachID(CCRegistration.ID.Pocket.class, (id) -> {
                    addPocket(out, ModRegistry.Items.POCKET_COMPUTER_NORMAL.get(), id);
                    addPocket(out, ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get(), id);
                });
            });
    }

    private static void addTurtle(CreativeModeTab.Output out, TurtleItem turtle, ResourceLocation id) {
        ItemStack stack = new ItemStack(turtle);
        stack.getOrCreateTag().putString("RightUpgrade", id.toString());
        out.accept(stack);
    }

    private static void addPocket(CreativeModeTab.Output out, PocketComputerItem pocket, ResourceLocation id) {
        ItemStack stack = new ItemStack(pocket);
        stack.getOrCreateTag().putString("Upgrade", id.toString());
        out.accept(stack);
    }

    private static <T, U> void forEachID(
        Class<T> clazz,
        Consumer<ResourceLocation> consumer
    ) {
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.canAccess(null)) {
                continue;
            }
            if (!ResourceLocation.class.isAssignableFrom(field.getType())) {
                continue;
            }
            Object value;
            try {
                value = field.get(null);
            } catch (IllegalAccessException e) {
                continue;
            }
            consumer.accept((ResourceLocation) value);
        }
    }
}
