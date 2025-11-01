package de.srendi.advancedperipherals.common.util.inventory;

import de.srendi.advancedperipherals.common.util.FingerprintUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {

    private ItemUtil() {
    }

    /**
     * Fingerprints are XXHash64 hashes generated out of the nbt tag, the registry name and the display name from item stacks
     * Used to filter inventory specific operations. See {@link ItemFilter}
     *
     * @return A generated XXHash64 hash from the item stack
     */
    public static String getFingerprint(ItemStack stack) {
        FingerprintUtil.FingerprintKey fingerprintKey = new FingerprintUtil.FingerprintKey(getRegistryKey(stack), stack.getComponentsPatch().hashCode(), stack.getDisplayName().getString());

        return FingerprintUtil.hash(fingerprintKey);
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
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static ResourceLocation getRegistryKey(ItemStack item) {
        return BuiltInRegistries.ITEM.getKey(item.copy().getItem());
    }
}
