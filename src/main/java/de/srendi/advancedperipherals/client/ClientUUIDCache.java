package de.srendi.advancedperipherals.client;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.network.APNetworking;
import de.srendi.advancedperipherals.network.toserver.RetrieveUsernamePacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Used for client side messages where we don't have a username, only a UUID
 * See {@link de.srendi.advancedperipherals.common.items.MemoryCardItem#appendHoverText(ItemStack, Level, List, TooltipFlag)} as example
 * <p>
 * Probably the most useless feature, but I love it - endi
 */
public class ClientUUIDCache {

    private static final HashMap<UUID, String> CACHE = new HashMap<>();

    private ClientUUIDCache() { }

    @Nullable
    public static String getUsername(UUID uuid, UUID requester) {
        if (CACHE.containsKey(uuid))
            return CACHE.get(uuid);

        APNetworking.sendToServer(new RetrieveUsernamePacket(uuid, requester));
        return null;
    }

    public static void putUsername(UUID uuid, String username) {
        CACHE.put(uuid, username);
        AdvancedPeripherals.debug(String.format("Putting username %s with uuid %s into cache", username, uuid));
    }

}
