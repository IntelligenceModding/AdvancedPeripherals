package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.Queue;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID)
public class ServerWorker {

    private static final Queue<Runnable> callQueue = new ArrayDeque<>();

    public static void add(final Runnable call) {
        if (call != null) {
            callQueue.add(call);
        }
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
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
}
