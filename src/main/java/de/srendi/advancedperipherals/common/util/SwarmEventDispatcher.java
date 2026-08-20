package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import de.srendi.advancedperipherals.lib.peripherals.IBasePeripheral;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * SwarmEventDispatcher will combine multiple same events which fired from different peripherals into one event as a table.
 * Then we can save ComputerCraft's event queue space.
 */
@EventBusSubscriber
public final class SwarmEventDispatcher {
    private static final ConcurrentMap<String, ConcurrentMap<Integer, ConcurrentMap<String, Set<Object>>>> EVENTS = new ConcurrentHashMap<>();
    private static volatile boolean updated = false;

    private SwarmEventDispatcher() {}

    /**
     * {@code dispatch} will put periperal and event data into queue.
     * The events will be fired together at the end of the tick.
     *
     * For example, if you invoke
     * <pre>
     * <code>
     * dispatch("a_event", peripheral1, "random data1")
     * dispatch("a_event", peripheral1, "data2")
     * dispatch("a_event", peripheral2, "random data3")
     * dispatch("another_event", peripheral1, "random data4")
     * </code>
     * </pre>
     * the events will be pushed at the end of the tick with form of
     * <pre>
     * <code>
     * "a_event", {
     *   ["peripheral1_name"] = {"random data1", "data2"},
     *   ["peripheral2_name"] = {"random data3"},
     * }
     *
     * "another_event", {
     *   ["peripheral1_name"] = {"random data4"},
     * }
     * </code>
     * </pre>
     */
    public static void dispatch(@NotNull String event, @NotNull IBasePeripheral<?> peripheral, Object data) {
        ConcurrentMap<Integer, ConcurrentMap<String, Set<Object>>> computers = EVENTS.computeIfAbsent(event, (k) -> new ConcurrentHashMap<>());
        peripheral.forEachConnectedComputers((computer) -> {
            computers
                .computeIfAbsent(computer.getID(), (k) -> new ConcurrentHashMap<>())
                .compute(computer.getAttachmentName(), (name, datas) -> {
                    if (datas == null) {
                        datas = new HashSet<>();
                    }
                    datas.add(data);
                    return datas;
                });
            updated = true;
        });
    }

    private static Map<Integer, ServerComputer> getComputers(MinecraftServer server) {
        Map<Integer, ServerComputer> computers = new HashMap<>();
        ServerContext.get(server).registry().getComputers().forEach(computer -> computers.put(computer.getID(), computer));
        return computers;
    }

    @SubscribeEvent
    public static void serverTick(ServerTickEvent tickEvent) {
        if (tickEvent.phase != ServerTickEvent.Phase.END) {
            return;
        }
        if (!updated) {
            return;
        }
        updated = false;
        Map<Integer, ServerComputer> computerMap = getComputers(tickEvent.getServer());
        EVENTS.forEach((event, computers) -> {
            for (int id : computers.keySet()) {
                ConcurrentMap<String, Set<Object>> peripherals = computers.remove(id);
                if (peripherals == null || peripherals.isEmpty()) {
                    continue;
                }
                ServerComputer computer = computerMap.get(id);
                if (computer != null) {
                    computer.queueEvent(event, new Object[]{peripherals});
                }
            }
        });
    }
}
