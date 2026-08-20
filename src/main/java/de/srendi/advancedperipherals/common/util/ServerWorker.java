package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@EventBusSubscriber
public class ServerWorker {

    private static final Queue<Runnable> callQueue = new ConcurrentLinkedQueue<>();
    private static int tasksRan = 0;

    /**
     * This method will queue a task to current tick's end.
     * If a task added during the end of a tick, the task will be delayed to the next tick;
     */
    public static void add(final Runnable task) {
        callQueue.add(task);
    }

    /**
     * Add to next tick will execute the task in next tick.
     * It's an alias of ServerWorker.add(() -> ServerWorker.add(task));
     */
    public static void addToNextTick(final Runnable task) {
        add(() -> add(task));
    }

    @SubscribeEvent
    public static void serverTick(final ServerTickEvent event) {
        if (event.phase != ServerTickEvent.Phase.END) {
            return;
        }
        for (int remain = callQueue.size(); remain > 0; remain--) {
            final Runnable runnable = callQueue.poll();
            tasksRan++;
            AdvancedPeripherals.debug("Running task #{}. Running {}", tasksRan, runnable.getClass());
            runnable.run();
        }
    }
}
