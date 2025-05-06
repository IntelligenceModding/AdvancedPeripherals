package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID)
public class ServerWorker {

    private static final Queue<Runnable> callQueue = new ConcurrentLinkedQueue<>();

    public static void add(final Runnable call) {
        if (call != null) {
            callQueue.add(call);
        }
    }

    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Post event) {
        while (true) {
            final Runnable runnable = callQueue.poll();
            if (runnable == null) {
                return;
            }
            AdvancedPeripherals.debug("Running queued server worker call: " + runnable);
            runnable.run();
        }
    }
}
