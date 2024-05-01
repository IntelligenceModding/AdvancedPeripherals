/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.common.addons.curios;

public class CuriosHelper {
    /*
     * public static ICapabilityProvider createARGogglesProvider(ItemStack stackFor)
     * { return new ICapabilityProvider() {
     * 
     * @Override public <T> LazyOptional<T> getCapability(Capability<T> cap,
     * Direction side) { return CuriosCapability.ITEM.orEmpty(cap,
     * LazyOptional.of(() -> new ICurio() {
     * 
     * @Override public void curioTick(String identifier, int index, LivingEntity
     * livingEntity) { if (!SideHelper.isClientPlayer(livingEntity)) return;
     * ARGogglesItem.clientTick((LocalPlayer) livingEntity, stackFor); }
     * 
     * @Override public ItemStack getStack() { return stackFor; }
     * 
     * @Override public void onUnequip(SlotContext slotContext, ItemStack newStack)
     * { if (!(slotContext.getWearer() instanceof ServerPlayer serverPlayer))
     * return; MNetwork.sendTo(new ClearHudCanvasMessage(), serverPlayer); }
     * 
     * //TODO: add rendering if in Curio slot })); } }; }
     */
}
