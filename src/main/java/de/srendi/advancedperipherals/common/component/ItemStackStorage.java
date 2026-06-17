package de.srendi.advancedperipherals.common.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class ItemStackStorage {
    public static final Codec<ItemStackStorage> CODEC = ItemStack.OPTIONAL_CODEC.listOf()
        .xmap(ItemStackStorage::of, ItemStackStorage::getAllAsList);

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackStorage> STREAM_CODEC = ItemStack.OPTIONAL_LIST_STREAM_CODEC
        .map(ItemStackStorage::of, ItemStackStorage::getAllAsList);

    private final ItemStack[] items;

    private ItemStackStorage(ItemStack[] items) {
        this.items = items;
    }

    public static ItemStackStorage ofSize(int size) {
        ItemStack[] items = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            items[i] = ItemStack.EMPTY;
        }
        return new ItemStackStorage(items);
    }

    public static ItemStackStorage of(@NotNull ItemStack... items) {
        return new ItemStackStorage(copyItems(items));
    }

    public static ItemStackStorage of(List<ItemStack> items) {
        ItemStack[] copies = new ItemStack[items.size()];
        for (int i = 0; i < copies.length; i++) {
            copies[i] = items.get(i).copy();
        }
        return new ItemStackStorage(copies);
    }

    public int size() {
        return this.items.length;
    }

    @NotNull
    public ItemStack get(int slot) {
        return this.items[slot].copy();
    }

    @NotNull
    public Item getItem(int slot) {
        return this.items[slot].getItem();
    }

    public ItemStack[] getAll() {
        return copyItems(this.items);
    }

    private List<ItemStack> getAllAsList() {
        return List.of(this.getAll());
    }

    @CheckReturnValue
    public ItemStackStorage set(int slot, @NotNull ItemStack stack) {
        ItemStack[] items = copyItems(this.items);
        items[slot] = stack.copy();
        return new ItemStackStorage(items);
    }

    @CheckReturnValue
    public ItemStackStorage update(int slot, Consumer<ItemStack> processor) {
        ItemStack[] items = copyItems(this.items);
        processor.accept(items[slot]);
        return new ItemStackStorage(items);
    }

    @CheckReturnValue
    public ItemStackStorage updateAll(BiConsumer<Integer, ItemStack> processor) {
        ItemStack[] items = copyItems(this.items);
        for (int i = 0; i < items.length; i++) {
            processor.accept(i, items[i]);
        }
        return new ItemStackStorage(items);
    }

    public boolean isSameItemSameComponents(int slot, ItemStack stack) {
        return ItemStack.isSameItemSameComponents(this.items[slot], stack);
    }

    public ItemStack[] getAllUnsafe() {
        return this.items;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ItemStackStorage storage)) {
            return false;
        }
        int length = this.items.length;
        if (length != storage.items.length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (!ItemStack.isSameItemSameComponents(this.items[i], storage.items[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.items);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder()
            .append("ItemStackStorage[");
        for (int i = 0; i < this.items.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(this.items[i].toString());
        }
        return builder
            .append("]")
            .toString();
    }

    private static ItemStack[] copyItems(ItemStack[] items) {
        ItemStack[] copies = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            copies[i] = items[i].copy();
        }
        return copies;
    }
}
