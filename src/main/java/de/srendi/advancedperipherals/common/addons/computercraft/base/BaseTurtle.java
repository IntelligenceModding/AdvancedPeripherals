package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.TileEntityList;
import de.srendi.advancedperipherals.common.util.WorldPos;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseTurtle<T extends BasePeripheral> extends AbstractTurtleUpgrade {

    protected T peripheral;
    protected ITurtleAccess turtle;
    protected int tick;

    public BaseTurtle(String id, String adjective, ItemStack item) {
        super(new ResourceLocation(AdvancedPeripherals.MOD_ID, id), TurtleUpgradeType.PERIPHERAL, adjective, item);
    }

    protected abstract T createPeripheral();

    protected abstract ModelResourceLocation getLeftModel();

    protected abstract ModelResourceLocation getRightModel();

    @NotNull
    @Override
    public TransformedModel getModel(@Nullable ITurtleAccess iTurtleAccess, @NotNull TurtleSide turtleSide) {
        if (getLeftModel() == null) {
            float xOffset = turtleSide == TurtleSide.LEFT ? -0.40625f : 0.40625f;
            Matrix4f transform = new Matrix4f(new float[]{
                    0.0f, 0.0f, -1.0f, 1.0f + xOffset,
                    1.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, -1.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 0.0f, 1.0f,
            });
            return TransformedModel.of(getCraftingItem(), new TransformationMatrix(transform));
        }
        return TransformedModel.of(turtleSide == TurtleSide.LEFT ? getLeftModel() : getRightModel());
    }

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        this.peripheral = createPeripheral();
        return peripheral;
    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        if (!turtle.getWorld().isClientSide) {
            IPeripheral turtlePeripheral = turtle.getPeripheral(side);
            this.turtle = turtle;
            if (turtlePeripheral instanceof EnvironmentDetectorPeripheral)
                ((EnvironmentDetectorPeripheral) turtlePeripheral).setTurtle(turtle);
        }

        tick++;
        if (tick > 10) {
            TileEntityList.get(turtle.getWorld()).setTileEntity(turtle.getWorld(), new WorldPos(turtle.getPosition(), turtle.getWorld()), true); //Add the turtle to the List for event use
            tick = 0;
        }
    }
}