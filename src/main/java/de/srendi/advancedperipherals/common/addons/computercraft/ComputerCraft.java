package de.srendi.advancedperipherals.common.addons.computercraft;

import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ComputerCraft {

    @CapabilityInject(IPeripheral.class)
    public static final Capability<IPeripheral> PERIPHERAL_CAPABILITY = null;

    public static void initiate() {
        CCEventManager.getInstance().registerSender((te, name, params)->te.getCapability(PERIPHERAL_CAPABILITY).ifPresent(handler->{
            if (handler instanceof CCEventManager.IComputerEventSender) {
                ((CCEventManager.IComputerEventSender) handler).sendEvent(te, name, params);
            }
        }));
    }

    @SubscribeEvent
    public static void attachPeripheralCap(AttachCapabilitiesEvent<TileEntity> event) {
        if (PERIPHERAL_CAPABILITY != null && event.getObject() instanceof ILuaMethodProvider) {
            event.addCapability(new ResourceLocation(AdvancedPeripherals.MOD_ID, ("computercraft")), new AdvancedPeripheralProvider((ILuaMethodProvider) event.getObject()));
        }
    }

}