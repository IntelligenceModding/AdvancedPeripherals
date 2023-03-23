package de.srendi.advancedperipherals.common.smartglasses.modules;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IOwnerAbility;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.PeripheralOwnerAbility;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;

public class ModulePeripheralOwner implements IPeripheralOwner {

    //TODO: Think about making our own smart glasses access so we don't have the not used stuff like the color or the light
    // We would need to remove the pocket stuff from the SmartGlassesComputer
    private IPocketAccess access;

    public ModulePeripheralOwner(IPocketAccess access) {
        this.access = access;
    }

    @Override
    public @Nullable String getCustomName() {
        return null;
    }

    @Override
    public @Nullable Level getLevel() {
        return access.getEntity().getLevel();
    }

    @Override
    public @NotNull BlockPos getPos() {
        return access.getEntity().getOnPos();
    }

    @Override
    public @NotNull Direction getFacing() {
        return null;
    }

    @Override
    public @NotNull FrontAndTop getOrientation() {
        return null;
    }

    @Override
    public @Nullable Player getOwner() {
        return null;
    }

    @Override
    public @NotNull CompoundTag getDataStorage() {
        return null;
    }

    @Override
    public void markDataStorageDirty() {

    }

    @Override
    public <T> T withPlayer(Function<APFakePlayer, T> function) {
        return null;
    }

    @Override
    public ItemStack getToolInMainHand() {
        return null;
    }

    @Override
    public ItemStack storeItem(ItemStack stored) {
        return null;
    }

    @Override
    public void destroyUpgrade() {

    }

    @Override
    public boolean isMovementPossible(@NotNull Level level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public boolean move(@NotNull Level level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public <T extends IOwnerAbility> void attachAbility(PeripheralOwnerAbility<T> ability, T abilityImplementation) {

    }

    @Override
    public <T extends IOwnerAbility> @Nullable T getAbility(PeripheralOwnerAbility<T> ability) {
        return null;
    }

    @Override
    public Collection<IOwnerAbility> getAbilities() {
        return null;
    }
}
