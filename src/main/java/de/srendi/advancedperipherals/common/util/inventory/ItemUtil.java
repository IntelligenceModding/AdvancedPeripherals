package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.shared.Registry;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.StringUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
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

    private ItemUtil() {}

    static MessageDigest getMessageDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            AdvancedPeripherals.debug("Could not generate fingerprint.", Level.ERROR);
            ex.printStackTrace();
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
        MessageDigest md = getMessageDigest("MD5");
        if (md == null) {
            return "";
        }
        CompoundTag tag = stack.getTag();
        md.update(getRegistryKey(stack).toString().getBytes(StandardCharsets.UTF_8));
        if (tag != null && !tag.isEmpty()) {
            md.update(tag.getAsString().getBytes(StandardCharsets.UTF_8));
        }
        return StringUtil.toHexString(md.digest());
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
        return ForgeRegistries.ITEMS.getKey(item.getItem());
    }
}
