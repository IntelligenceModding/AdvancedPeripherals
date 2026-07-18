package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaValues;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.SmartRailBlock;
import de.srendi.advancedperipherals.common.blocks.blockentities.SmartRailBlockEntity;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class SmartRailPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<SmartRailBlockEntity>> {
    private final SmartRailBlockEntity be;

    private final List<AbstractMinecart> cartsBuf = new ArrayList<>();
    private Map<UUID, AbstractMinecart> carts = Map.of();
    private int rescanCD = 0;
    private volatile boolean haveAnyCart = false;

    public SmartRailPeripheral(SmartRailBlockEntity be) {
        super("smart_rail", new BlockEntityPeripheralOwner<>(be));
        this.be = be;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @LuaFunction
    public final boolean hasCarts() {
        return this.haveAnyCart;
    }

    @LuaFunction(mainThread = true)
    public final Collection<?> getCarts() {
        LuaConverter.EntityConverter.Context context = LuaConverter.entityContextBuilder()
            .detailed()
            .position(this.be.getBottomCenter())
            .build();
        return this.carts.values().stream().map((e) -> LuaConverter.entityToLua(e, context)).toList();
    }

    @LuaFunction
    public final MethodResult getState() {
        SmartRailBlock.RailPoweredState state = this.be.getState();
        return MethodResult.of(state.name(), state.ordinal());
    }

    @LuaFunction
    public final void setState(IArguments args) throws LuaException {
        Object arg = args.get(0);
        if (arg == null) {
            throw new LuaException("argument #1 must provide a state name or an index between [0, 4]");
        }
        SmartRailBlock.RailPoweredState state = null;
        if (arg instanceof Number index) {
            int i = Math.min(Math.max(index.intValue(), 0), 4);
            state = SmartRailBlock.RailPoweredState.values()[i];
        } else if (arg instanceof String name) {
            name = name.toUpperCase(Locale.ROOT);
            for (SmartRailBlock.RailPoweredState s : SmartRailBlock.RailPoweredState.values()) {
                if (s.name().equals(name)) {
                    state = s;
                }
            }
            if (state == null) {
                throw new LuaException("Unknown rail state '" + arg + "'");
            }
        } else {
            throw LuaValues.badArgumentOf(args, 0, "string or number");
        }
        this.be.setState(state);
    }

    @LuaFunction
    public final boolean isActivating() {
        return this.be.isActivating();
    }

    @LuaFunction
    public final void setActivating(boolean value) {
        this.be.setActivating(value);
    }

    @Override
    public void update() {
        this.rescanCD--;
        if (this.rescanCD > 0) {
            return;
        }
        this.rescanCD = 2;

        this.be.collectCarts(this.cartsBuf);

        Map<UUID, AbstractMinecart> carts = this.carts;
        List<AbstractMinecart> newCarts = new ArrayList<>();
        Set<UUID> removedCarts = new HashSet<>(carts.keySet());
        for (AbstractMinecart cart : this.cartsBuf) {
            if (!removedCarts.remove(cart.getUUID())) {
                newCarts.add(cart);
            }
        }

        if (!removedCarts.isEmpty() || !newCarts.isEmpty()) {
            Map<UUID, AbstractMinecart> newCartsMap = Map.ofEntries(
                Stream.concat(
                    carts.entrySet().stream().filter((e) -> !removedCarts.contains(e.getKey())),
                    newCarts.stream().map((e) -> Map.entry(e.getUUID(), e))
                )
                    .toArray(Map.Entry[]::new)
            );
            this.carts = newCartsMap;
            this.haveAnyCart = !newCartsMap.isEmpty();
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
                LuaConverter.EntityConverter.Context context = LuaConverter.entityContextBuilder()
                    .detailed()
                    .position(this.be.getBottomCenter())
                    .build();
                this.forEachConnectedComputers(
                    (computer) -> computer.queueEvent(
                        CCEvents.CART_ATTACHED,
                        computer.getAttachmentName(),
                        newCarts.stream().map((e) -> LuaConverter.entityToLua(e, context)).toList()
                    )
                );
            }
        }

        this.cartsBuf.clear();
    }
}
