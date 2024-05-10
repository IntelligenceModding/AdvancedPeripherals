package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID)
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
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            int size = callQueue.size();
            for (int i = 0; i < size; i++) {
                final Runnable task = callQueue.poll();
                tasksRan++;
                AdvancedPeripherals.debug("Running task #" + tasksRan + ". Running " + task.getClass());
                task.run();
            }
        }
    }
}
