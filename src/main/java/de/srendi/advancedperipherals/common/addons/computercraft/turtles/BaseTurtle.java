package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.TileEntityList;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public abstract class BaseTurtle<T extends BasePeripheral> extends AbstractTurtleUpgrade {

    protected boolean initiated = false;
    protected T peripheral;
    protected TileEntity tileEntity;

    //Todo - 1.0r: Make unique models for the turtles.
    private static final ModelResourceLocation model = new ModelResourceLocation("computercraft:turtle_advanced", "inventory");

    public BaseTurtle() {
        super(new ResourceLocation(AdvancedPeripherals.MOD_ID, "chat_box_turtle"), TurtleUpgradeType.PERIPHERAL, "block.advancedperipherals.chat_box_turtle", new ItemStack(Blocks.CHAT_BOX.get()));
        peripheral = createPeripheral();
    }

    protected abstract T createPeripheral();

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return peripheral;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public TransformedModel getModel(ITurtleAccess turtle, @Nonnull TurtleSide side) {
        return TransformedModel.of(model);
    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        if(!initiated) {
            if(turtle.getWorld().getTileEntity(turtle.getPosition()) != null) {
                tileEntity = turtle.getWorld().getTileEntity(turtle.getPosition());
            }
                TileEntityList tileEntityList = TileEntityList.get(turtle.getWorld());
                if (!tileEntityList.getBlockPositions().contains(turtle.getPosition())) {
                    tileEntityList.setTileEntity(turtle.getWorld(), turtle.getPosition()); //Add the turtle to the List for event use
                }

            initiated = true;
        }
    }
}
