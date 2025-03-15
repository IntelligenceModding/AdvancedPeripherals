package de.srendi.advancedperipherals.client;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = AdvancedPeripherals.MOD_ID)
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
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tasks.forEach((id, runnable) -> {
                tasks.remove(id, runnable);
                runnable.run();
            });
        }
    }
}
