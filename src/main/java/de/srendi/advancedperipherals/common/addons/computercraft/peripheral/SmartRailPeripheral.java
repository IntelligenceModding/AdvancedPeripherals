package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.SmartRailBlockEntity;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class SmartRailPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<SmartRailBlockEntity>> {
    private final SmartRailBlockEntity be;

    private final List<AbstractMinecart> cartsBuf = new ArrayList<>();
    private volatile Map<UUID, Map<String, ?>> carts = Map.of();
    private int rescanCD = 0;

    public SmartRailPeripheral(SmartRailBlockEntity be) {
        super("smart_rail", new BlockEntityPeripheralOwner<>(be));
        this.be = be;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @LuaFunction
    public final Collection<Map<String, ?>> getCarts() {
        return this.carts.values();
    }

    @Override
    public void update() {
        this.rescanCD--;
        if (this.rescanCD > 0) {
            return;
        }
        this.rescanCD = 2;

        this.be.collectCarts(this.cartsBuf);

        Map<UUID, Map<String, ?>> carts = this.carts;
        List<Map.Entry<UUID, Map<String, ?>>> newCarts = new ArrayList<>();
        Set<UUID> removedCarts = new HashSet<>(carts.keySet());
        LuaConverter.EntityConverter.Context context = LuaConverter.entityContextBuilder()
            .detailed()
            .position(Vec3.atBottomCenterOf(this.be.getBlockPos()))
            .build();
        for (AbstractMinecart cart : this.cartsBuf) {
            if (!removedCarts.remove(cart.getUUID())) {
                newCarts.add(Map.entry(cart.getUUID(), LuaConverter.entityToLua(cart, context)));
            }
        }

        if (!removedCarts.isEmpty() || !newCarts.isEmpty()) {
            Map<UUID, Map<String, ?>> newCartsMap = Map.ofEntries(
                Stream.concat(
                    carts.entrySet().stream().filter((e) -> !removedCarts.contains(e.getKey())),
                    newCarts.stream()
                )
                    .toArray(Map.Entry[]::new)
            );
            this.carts = newCartsMap;
            if (!removedCarts.isEmpty()) {
                this.forEachConnectedComputers(
                    (computer) -> computer.queueEvent(
                        CCEvents.CART_DETACHED,
                        computer.getAttachmentName(),
                        removedCarts.stream().map(UUID::toString).toList()
                    )
                );
            }
            if (!newCarts.isEmpty()) {
                this.forEachConnectedComputers(
                    (computer) -> computer.queueEvent(
                        CCEvents.CART_ATTACHED,
                        computer.getAttachmentName(),
                        newCarts.stream().map(Map.Entry::getValue).toList()
                    )
                );
            }
        }

        this.cartsBuf.clear();
    }
}
