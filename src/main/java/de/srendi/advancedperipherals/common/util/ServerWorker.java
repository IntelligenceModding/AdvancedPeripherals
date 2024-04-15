package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.neoforged.event.TickEvent;
import net.neoforged.eventbus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.Queue;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID)
public class ServerWorker {

    private static final Queue<Runnable> callQueue = new ArrayDeque<>();

    public static void add(final Runnable call) {
        callQueue.add(call);
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (!callQueue.isEmpty()) {
                final Runnable runnable = callQueue.poll();
                AdvancedPeripherals.debug("Running queued server worker call: " + runnable);
                runnable.run();
            }
        }
    }
}
