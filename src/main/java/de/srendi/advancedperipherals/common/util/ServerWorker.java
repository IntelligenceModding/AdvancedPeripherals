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

    public static void add(final Runnable call) {
        callQueue.add(call);
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (!callQueue.isEmpty()) {
                final Runnable runnable = callQueue.poll();
                tasksRan++;
                AdvancedPeripherals.debug("Running task #" + tasksRan + ". Running " + runnable.getClass());
                runnable.run();
            }
        }
    }
}
