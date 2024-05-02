package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.core.ServerComputer;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID)
public final class SwarmEventDispatcher {
	private static final ConcurrentMap<String, ConcurrentMap<Integer, ConcurrentMap<String, Set<Object>>>> events = new ConcurrentHashMap<>();
	private static final AtomicBoolean updated = new AtomicBoolean();

	private SwarmEventDispatcher(){}

	public static void dispatch(@NotNull String event, @NotNull BasePeripheral peripheral, Object data) {
		boolean set = false;
		ConcurrentMap<Integer, ConcurrentMap<String, Set<Object>>> computers = events.computeIfAbsent(event, (k) -> new ConcurrentHashMap<>());
		Iterable<IComputerAccess> computerIter = peripheral.getConnectedComputers();
		for (IComputerAccess computer : computerIter) {
			computers.computeIfAbsent(computer.getID(), (k) -> new ConcurrentHashMap<>()).compute(computer.getAttachmentName(), (name, datas) -> {
				if (datas == null) {
					datas = new HashSet<>();
				}
				datas.add(data);
				return datas;
			});
			set = true;
		}
		if (set) {
			updated.set(true);
		}
	}

	private static Map<Integer, ServerComputer> getComputers(MinecraftServer server) {
		Map<Integer, ServerComputer> computers = new HashMap<>();
		ServerContext.get(server).registry().getComputers().forEach(computer -> computers.put(computer.getID(), computer));
		return computers;
	}

	@SubscribeEvent
	public static void serverTick(TickEvent.ServerTickEvent tickEvent) {
		if (tickEvent.phase != TickEvent.Phase.END) {
			return;
		}
		if (!updated.compareAndSet(true, false)) {
			return;
		}
		Map<Integer, ServerComputer> computerMap = getComputers(tickEvent.getServer());
		events.forEach((event, computers) -> {
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
