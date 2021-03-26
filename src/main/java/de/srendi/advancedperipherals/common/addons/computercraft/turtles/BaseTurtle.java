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
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseTurtle<T extends BasePeripheral> extends AbstractTurtleUpgrade {

    protected static final List<ChunkPos> loadedChunks = new ArrayList<>();

    protected T peripheral;
    protected TileEntity tileEntity;
    protected int tick;
    protected boolean init;

    //Todo - 1.0r: Make unique models for the turtles.
    private static final ModelResourceLocation model = new ModelResourceLocation("computercraft:turtle_advanced", "inventory");

    public BaseTurtle(String id, String adjective, ItemStack item) {
        super(new ResourceLocation(AdvancedPeripherals.MOD_ID, id), TurtleUpgradeType.PERIPHERAL, adjective, item);
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