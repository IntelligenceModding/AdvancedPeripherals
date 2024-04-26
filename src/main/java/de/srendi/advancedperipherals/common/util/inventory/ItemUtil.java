package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.shared.Registry;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.StringUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Level;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class ItemUtil {

    public static final Item TURTLE_NORMAL = Registry.ModItems.TURTLE_NORMAL.get();
    public static final Item TURTLE_ADVANCED = Registry.ModItems.TURTLE_ADVANCED.get();

    public static final Item POCKET_NORMAL = Registry.ModItems.POCKET_COMPUTER_NORMAL.get();
    public static final Item POCKET_ADVANCED = Registry.ModItems.POCKET_COMPUTER_ADVANCED.get();

    public static <T> T getRegistryEntry(String name, IForgeRegistry<T> forgeRegistry) {
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

    /**
     * Fingerprints are MD5 hashes generated out of the nbt tag, the registry name and the display name from item stacks
     * Used to filter inventory specific operations. {@link de.srendi.advancedperipherals.common.addons.computercraft.peripheral.InventoryManagerPeripheral}
     *
     * @return A generated MD5 hash from the item stack
     */
    public static String getFingerprint(ItemStack stack) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(stack.getOrCreateTag().toString().getBytes(StandardCharsets.UTF_8));
            md.update(getRegistryKey(stack).toString().getBytes(StandardCharsets.UTF_8));
            md.update(stack.getDisplayName().getString().getBytes(StandardCharsets.UTF_8));
            return StringUtil.toHexString(md.digest());
        } catch (NoSuchAlgorithmException ex) {
            AdvancedPeripherals.debug("Could not parse fingerprint.", Level.ERROR);
            ex.printStackTrace();
        }
        return "";
    }

    public static Object asMapKey(ItemStack stack) {
        return new Pair(stack.getItem(), stack.hasTag() ? stack.getTag() : null);
    }

    public static Object fluidAsMapKey(FluidStack stack) {
        return new Pair(stack.getFluid(), stack.hasTag() ? stack.getTag() : null);
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

    //Gathers all items in handler and returns them
    public static List<ItemStack> getItemsFromItemHandler(IItemHandler handler) {
        List<ItemStack> items = new ArrayList<>(handler.getSlots());
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            items.add(handler.getStackInSlot(slot).copy());
        }

        return items;
    }

    public static ResourceLocation getRegistryKey(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

    public static ResourceLocation getRegistryKey(ItemStack item) {
        return ForgeRegistries.ITEMS.getKey(item.copy().getItem());
    }
}
