package de.srendi.advancedperipherals.client;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

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
}
