package de.srendi.advancedperipherals.common.addons.curios;

public class CuriosHelper {
    /*public static ICapabilityProvider createARGogglesProvider(ItemStack stackFor) {
        return new ICapabilityProvider() {

            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
                return CuriosCapability.ITEM.orEmpty(cap, LazyOptional.of(() -> new ICurio() {

                    @Override
                    public void curioTick(String identifier, int index, LivingEntity livingEntity) {
                        if (!SideHelper.isClientPlayer(livingEntity))
                            return;
                        ARGogglesItem.clientTick((LocalPlayer) livingEntity, stackFor);
                    }

                    @Override
                    public ItemStack getStack() {
                        return stackFor;
                    }

                    @Override
                    public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                        if (!(slotContext.getWearer() instanceof ServerPlayer serverPlayer))
                            return;
                        MNetwork.sendTo(new ClearHudCanvasMessage(), serverPlayer);
                    }

                    //TODO: add rendering if in Curio slot
                }));
            }
        };
    }*/
}
