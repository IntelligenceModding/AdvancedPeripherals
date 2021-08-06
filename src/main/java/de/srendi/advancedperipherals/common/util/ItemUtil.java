package de.srendi.advancedperipherals.common.util;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

public class ItemUtil {

    @ObjectHolder("computercraft:turtle_normal")
    public static Item TURTLE_NORMAL;
    @ObjectHolder("computercraft:turtle_advanced")
    public static Item TURTLE_ADVANCED;

    @ObjectHolder("computercraft:pocket_computer_normal")
    public static Item POCKET_NORMAL;
    @ObjectHolder("computercraft:pocket_computer_advanced")
    public static Item POCKET_ADVANCED;

    public static <T extends ForgeRegistryEntry<T>> T getRegistryEntry(String name, IForgeRegistry<T> forgeRegistry) {
        ResourceLocation location;
        try {
            location = new ResourceLocation(name);
        } catch (ResourceLocationException ex) {
            location = null;
        }

        T value;
        if (location != null && forgeRegistry.containsKey(location) && (value = forgeRegistry.getValue(location)) != null) {
            return value;
        } else {
            return null;
        }
    }

    public static ItemStack makeTurtle(Item turtle, String upgrade) {
        ItemStack stack = new ItemStack(turtle);
        stack.getOrCreateTag().putString("RightUpgrade", upgrade);
        return stack;
    }

    public static ItemStack makePocket(Item turtle, String upgrade) {
        ItemStack stack = new ItemStack(turtle);
        stack.getOrCreateTag().putString("Upgrade", upgrade);
        return stack;
    }


}
