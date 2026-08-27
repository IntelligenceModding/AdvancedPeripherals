package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.pocket.core.PocketBrain;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.AbstractDataStorage;
import de.srendi.advancedperipherals.lib.peripherals.IBasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;

public class PocketPeripheralOwner extends BasePeripheralOwner {
    private final IPocketAccess pocket;

    private final AbstractDataStorage dataStorage = new AbstractDataStorage() {
        @Override
        protected DataComponentPatch getPatch() {
            return pocket.getUpgradeData();
        }

        @Override
        protected void setPatch(DataComponentPatch patch) {
            pocket.setUpgradeData(patch);
        }
    };

    protected PocketPeripheralOwner(IPocketAccess pocket) {
        super();
        this.pocket = pocket;
        if (APConfig.PERIPHERALS_CONFIG.disablePocketFuelConsumption.get()) {
            attachAbility(PeripheralOwnerAbility.FUEL, new InfinitePocketFuelAbility(this));
        }
    }

    public static PocketPeripheralOwner of(IPocketAccess pocket) {
        if (pocket instanceof SmartGlassesSideAccess access) {
            return new SmartGlassesPeripheralOwner(access);
        }
        return new PocketPeripheralOwner(pocket);
    }

    @Override
    @NotNull
    public Level getLevel() {
        // TODO: Certain version of CC will make pocket computer has null level while changing dimensions.
        // Not sure if this is fixed in later CC so bunch of null checks can be removed. :3
        return pocket.getLevel();
    }

    @Override
    @NotNull
    public BlockPos getPos() {
        return BlockPos.containing(getCenterPos());
    }

    @Override
    @NotNull
    public Vec3 getCenterPos() {
        return pocket.getPosition();
    }

    @Override
    @NotNull
    public Vec3 getDirection() {
        Entity owner = pocket.getEntity();
        return owner == null ? /* North */ new Vec3(0, 0, -1) : owner.getLookAngle();
    }

    @Override
    @NotNull
    public Direction getFacing() {
        return Direction.getNearest(this.getDirection());
    }

    @Override
    @NotNull
    public FrontAndTop getFrontAndTop() {
        Entity owner = pocket.getEntity();
        if (owner == null) {
            return FrontAndTop.NORTH_UP;
        }
        return FrontAndTop.fromFrontAndTop(getFacing(), Direction.getNearest(owner.getUpVector(1.0f)));
    }

    @Override
    @NotNull
    public Matrix3dc getOrientation() {
        Entity owner = pocket.getEntity();
        if (owner == null) {
            return new Matrix3d();
        }
        // Note: Pocket computer have no sense about pitch since player always hold it flatly.
        float yRot = owner.getYRot();
        Vec3 front = Vec3.directionFromRotation(0, yRot);
        Vec3 right = Vec3.directionFromRotation(0, (yRot - 90) % 360);
        return new Matrix3d(
            right.x, right.y, right.z,
            0, 1, 0,
            front.x, front.y, front.z
        );
    }

    @Override
    @Nullable
    public Entity getHoldingEntity() {
        return pocket.getEntity();
    }

    @Override
    public AbstractDataStorage getDataStorage() {
        return this.dataStorage;
    }

    @Override
    public <T> T withPlayer(APFakePlayer.Action<T> function) throws LuaException {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public ItemStack getToolInMainHand() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack storeItem(ItemStack stored) {
        // Tricks with inventory needed
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void destroyUpgrade() {
        pocket.setUpgrade(null);
    }

    @Override
    @Nullable
    public <T extends IPeripheral> T getConnectedPeripheral(Class<T> type) {
        ServerComputer computer = null;
        if (pocket instanceof PocketBrain pocketBrain) {
            computer = pocketBrain.computer();
        } else if (pocket instanceof SmartGlassesSideAccess sideAccess) {
            computer = sideAccess.getComputer();
        }
        if (computer != null) {
            for (ComputerSide side : ComputerSide.values()) {
                IPeripheral peripheral = computer.getPeripheral(side);
                if (peripheral == null || !type.isInstance(peripheral)) {
                    continue;
                }
                if (peripheral instanceof IBasePeripheral basePeripheral && !basePeripheral.isEnabled()) {
                    continue;
                }
                return (T) peripheral;
            }
        }
        return null;
    }
}
