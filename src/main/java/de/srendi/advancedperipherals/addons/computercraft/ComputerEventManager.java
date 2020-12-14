package de.srendi.advancedperipherals.addons.computercraft;

import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

public class ComputerEventManager {

    static final ComputerEventManager computerEventManager = new ComputerEventManager();

    private final List<IComputerEventSender> senders = new ArrayList<>();

    public static ComputerEventManager getInstance() {
        return computerEventManager;
    }

    public void registerSender(IComputerEventSender sender) {
        senders.add(sender);
    }

    public void sendEvent(TileEntity tileEntity, String name, Object... params) {
        senders.forEach(s -> s.sendEvent(tileEntity, name, params));
    }

    @FunctionalInterface
    public interface IComputerEventSender {
        void sendEvent(TileEntity te, String name, Object... params);
    }
}
