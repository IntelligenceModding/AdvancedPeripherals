package de.srendi.advancedperipherals.common.addons.curios;

import de.srendi.advancedperipherals.common.items.ARGogglesItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
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
						if (!(livingEntity instanceof PlayerEntity) || livingEntity != Minecraft.getInstance().player)
							return;
						ARGogglesItem.tick((PlayerEntity) livingEntity, stack);
					}
					
					@Override
					public void onUnequip(SlotContext slotContext, ItemStack newStack) {
						// TODO: send network message to player to clear HudOverlayHandler canvas
					}
				}));
			}
		};
	}
}
