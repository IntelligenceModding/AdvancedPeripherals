package de.srendi.advancedperipherals.setup;

import com.google.common.collect.Sets;
import de.srendi.advancedperipherals.blocks.tileentity.ChatBoxTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

public class ModTileEntityTypes {

    public static final RegistryObject<TileEntityType<ChatBoxTileEntity>> CHAT_BOX = Registration.TILE_ENTITIES.register(
            "chat_box", ()->new TileEntityType<>(ChatBoxTileEntity::new, Sets.newHashSet(ModBlocks.CHAT_BOX.get()), null));


    static void register() {

    }
}