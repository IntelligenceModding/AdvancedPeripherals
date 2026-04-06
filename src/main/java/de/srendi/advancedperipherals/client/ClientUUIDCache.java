package de.srendi.advancedperipherals.client;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.toserver.RetrieveUsernamePacket;
import de.srendi.advancedperipherals.common.util.LRUCache;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Used for client side messages where we don't have a username, only a UUID
 * See {@link de.srendi.advancedperipherals.common.items.MemoryCardItem#appendHoverText(ItemStack, Level, List, TooltipFlag)} as example
 * <p>
 * Probably the most useless feature, but I love it - endi
 */
public class ClientUUIDCache {

    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static final LRUCache<UUID, String> CACHE = new LRUCache<>(128);
    private static final Map<UUID, Long> QUERYING = new ConcurrentHashMap<>();

    private ClientUUIDCache() { }

    @Nullable
    public static String getUsername(UUID uuid) {
        String username;
        LOCK.readLock().lock();
        try {
            username = CACHE.get(uuid);
        } finally {
            LOCK.readLock().unlock();
        }
        if (username != null) {
            return username;
        }
        QUERYING.compute(uuid, (uuid2, timeout) -> {
            long now = System.currentTimeMillis();
            if (timeout != null && timeout >= now) {
                return timeout;
            }
            PacketDistributor.sendToServer(new RetrieveUsernamePacket(uuid2));
            return now + 10 * 1000;
        });
        return null;
    }

    public static void putUsername(UUID uuid, String username) {
        AdvancedPeripherals.debug("Putting username {} with uuid {} into cache", username, uuid);
        LOCK.writeLock().lock();
        try {
            CACHE.put(uuid, username);
        } finally {
            LOCK.writeLock().unlock();
        }
        QUERYING.remove(uuid);
    }

    public static void reset() {
        LOCK.writeLock().lock();
        try {
            CACHE.clear();
        } finally {
            LOCK.writeLock().unlock();
        }
        QUERYING.clear();
    }
}
