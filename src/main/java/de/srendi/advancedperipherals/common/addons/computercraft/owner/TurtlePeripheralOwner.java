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
package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.TurtlePermissions;
import dan200.computercraft.shared.util.InventoryUtil;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.common.util.fakeplayer.FakePlayerProviderTurtle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TurtlePeripheralOwner extends BasePeripheralOwner {
    public final ITurtleAccess turtle;
    public final TurtleSide side;

    public TurtlePeripheralOwner(ITurtleAccess turtle, TurtleSide side) {
        super();
        this.turtle = turtle;
        this.side = side;
    }

    @Nullable @Override
    public String getCustomName() {
        return null;
    }

    @NotNull @Override
    public Level getLevel() {
        return turtle.getLevel();
    }

    @NotNull @Override
    public BlockPos getPos() {
        return turtle.getPosition();
    }

    @NotNull @Override
    public Direction getFacing() {
        return turtle.getDirection();
    }

    /**
     * Not used for turtles
     */
    @NotNull @Override
    public FrontAndTop getOrientation() {
        return FrontAndTop.NORTH_UP;
    }

    @Nullable @Override
    public Player getOwner() {
        GameProfile owningPlayer = turtle.getOwningPlayer();
        if (owningPlayer == null)
            return null;
        return turtle.getLevel().getPlayerByUUID(owningPlayer.getId());
    }

    @NotNull @Override
    public CompoundTag getDataStorage() {
        return DataStorageUtil.getDataStorage(turtle, side);
    }

    @Override
    public void markDataStorageDirty() {
        turtle.updateUpgradeNBTData(side);
    }

    @Override
    public <T> T withPlayer(Function<APFakePlayer, T> function) {
        return FakePlayerProviderTurtle.withPlayer(turtle, function);
    }

    @Override
    public ItemStack getToolInMainHand() {
        return turtle.getInventory().getItem(turtle.getSelectedSlot());
    }

    @Override
    public ItemStack storeItem(ItemStack stored) {
        return InventoryUtil.storeItems(stored, turtle.getItemHandler(), turtle.getSelectedSlot());
    }

    @Override
    public void destroyUpgrade() {
        turtle.setUpgrade(side, null);
    }

    @Override
    public boolean isMovementPossible(@NotNull Level level, @NotNull BlockPos pos) {
        return FakePlayerProviderTurtle.withPlayer(turtle, player -> {
            if (level.isOutsideBuildHeight(pos))
                return false;
            if (!level.isInWorldBounds(pos))
                return false;
            if (ComputerCraft.turtlesObeyBlockProtection && !TurtlePermissions.isBlockEnterable(level, pos, player))
                return false;
            if (!level.isAreaLoaded(pos, 0))
                return false;
            return level.getWorldBorder().isWithinBounds(pos);
        });
    }

    @Override
    public boolean move(@NotNull Level level, @NotNull BlockPos pos) {
        return turtle.teleportTo(level, pos);
    }

    @NotNull public ITurtleAccess getTurtle() {
        return turtle;
    }

    @NotNull public TurtleSide getSide() {
        return side;
    }

    public TurtlePeripheralOwner attachFuel(int maxFuelConsumptionLevel) {
        attachAbility(PeripheralOwnerAbility.FUEL, new TurtleFuelAbility(this, maxFuelConsumptionLevel));
        return this;
    }
}
