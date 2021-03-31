package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.TileEntityList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseTurtle<T extends BasePeripheral> extends AbstractTurtleUpgrade {

    protected T peripheral;
    protected TileEntity tileEntity;
    protected int tick;
    protected boolean init;

    public BaseTurtle(String id, String adjective, ItemStack item) {
        super(new ResourceLocation(AdvancedPeripherals.MOD_ID, id), TurtleUpgradeType.PERIPHERAL, adjective, item);
        peripheral = createPeripheral();
    }

    protected abstract T createPeripheral();

    protected abstract ModelResourceLocation getLeftModel();
    protected abstract ModelResourceLocation getRightModel();

    @NotNull
    @Override
    public TransformedModel getModel(@Nullable ITurtleAccess iTurtleAccess, @NotNull TurtleSide turtleSide) {
        if(getLeftModel() == null) {
            float xOffset = turtleSide == TurtleSide.LEFT ? -0.40625f : 0.40625f;
            Matrix4f transform = new Matrix4f(new float[]{
                    0.0f, 0.0f, -1.0f, 1.0f + xOffset,
                    1.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, -1.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 0.0f, 1.0f,
            });
            return TransformedModel.of(getCraftingItem(), new TransformationMatrix(transform));
        }
        return TransformedModel.of(turtleSide == TurtleSide.LEFT ? getLeftModel() : getRightModel() );
    }

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return peripheral;
    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        if (!init) {
            if (!turtle.getWorld().isRemote) {
                World world = turtle.getWorld();
                BlockPos position = turtle.getPosition();
                tileEntity = turtle instanceof TurtleBrain ? ((TurtleBrain) turtle).getOwner() : world.getTileEntity(position);
                peripheral.setTileEntity(tileEntity);
                init = true;
            }
        }
        tick++;
        if(tick > 10) {
            TileEntityList tileEntityList = TileEntityList.get(turtle.getWorld()); //Sync the position with the tile entity list.
            if (!tileEntityList.getBlockPositions().contains(turtle.getPosition())) {
                tileEntityList.setTileEntity(turtle.getWorld(), turtle.getPosition()); //Add the turtle to the List for event use
            }
            tick = 0;
        }
    }
}