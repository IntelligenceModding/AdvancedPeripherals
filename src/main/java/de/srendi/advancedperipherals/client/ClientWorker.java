package de.srendi.advancedperipherals.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(Dist.CLIENT)
public class ClientWorker {

    private static final Map<String, Runnable> tasks = new ConcurrentHashMap<>();

    /**
     * This method will put a task to current tick's end.
     * If a task with given identifier is already exists, the task will be replaced.
     */
    public static void put(final String id, final Runnable task) {
        tasks.put(id, task);
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        tasks.forEach((id, runnable) -> {
            tasks.remove(id, runnable);
            runnable.run();
        });
    }

    @SubscribeEvent
    public static void clientLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() != Minecraft.getInstance().level) {
            return;
        }
        tasks.clear();
    }
}
