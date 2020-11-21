package de.srendi.advancedperipherals.blocks.tileentity;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.addons.computercraft.AdvancedPeripheral;
import de.srendi.advancedperipherals.setup.ModTileEntityTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import org.apache.logging.log4j.Level;

import java.util.UUID;

public class ChatBoxTileEntity extends TileEntity implements ITickableTileEntity {


    public ChatBoxTileEntity() {
        super(ModTileEntityTypes.CHAT_BOX.get());
    }

    @Override
    public void tick() {
        int x = this.pos.getX();
        int y = this.pos.getY();
        int z = this.pos.getZ();
        AdvancedPeripherals.LOGGER.log(Level.INFO, "It works");
        for (PlayerEntity playerEntity : world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(((float) x - 5.0F), ((float) y - 5.0F), ((float) z - 5.0F), ((float) (x + 1) + 5.0F), ((float) (y + 1) + 5.0F), ((float) (z + 1) + 5.0F)))) {
            playerEntity.sendMessage(new StringTextComponent("eeee"), UUID.randomUUID());
        }
    }
}
