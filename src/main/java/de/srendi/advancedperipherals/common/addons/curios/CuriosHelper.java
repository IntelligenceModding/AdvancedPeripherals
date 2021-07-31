package de.srendi.advancedperipherals.common.addons.curios;

import de.srendi.advancedperipherals.common.items.ARGogglesItem;
import de.srendi.advancedperipherals.common.util.SideHelper;
import de.srendi.advancedperipherals.network.MNetwork;
import de.srendi.advancedperipherals.network.messages.ClearHudCanvasMessage;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.core.jmx.Server;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class CuriosHelper {
    public static ICapabilityProvider createARGogglesProvider(ItemStack stackFor) {
        return new ICapabilityProvider() {

            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
                return CuriosCapability.ITEM.orEmpty(cap, LazyOptional.of(() -> new ICurio() {
                    ItemStack stack = stackFor;

                    @Override
                    public void curioTick(String identifier, int index, LivingEntity livingEntity) {
                        if (!SideHelper.isClientPlayer(livingEntity))
                            return;
                        ARGogglesItem.clientTick((LocalPlayer) livingEntity, stack);
                    }

                    @Override
                    public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                        if (!(slotContext.getWearer() instanceof ServerPlayer))
                            return;
                        MNetwork.sendTo(new ClearHudCanvasMessage(), (ServerPlayer) slotContext.getWearer());
                    }

                    //TODO: add rendering if in Curio slot
                }));
            }
        };
    }
}
