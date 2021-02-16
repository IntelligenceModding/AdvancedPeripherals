package de.srendi.advancedperipherals.common.setup;

import com.google.common.collect.Sets;
import de.srendi.advancedperipherals.common.blocks.tileentity.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;

public class ModTileEntityTypes {


    public static final RegistryObject<TileEntityType<ChatBoxTileEntity>> CHAT_BOX = Registration.TILE_ENTITIES.register("chat_box", ()->new TileEntityType<>(ChatBoxTileEntity::new, Sets.newHashSet(ModBlocks.CHAT_BOX.get()), null));
    public static final RegistryObject<TileEntityType<EnvironmentDetectorTileEntiy>> ENVIRONMENT_DETECTOR = Registration.TILE_ENTITIES.register("environment_detector", ()->new TileEntityType<>(EnvironmentDetectorTileEntiy::new, Sets.newHashSet(ModBlocks.ENVIRONMENT_DETECTOR.get()), null));
    public static final RegistryObject<TileEntityType<PlayerDetectorTileEntity>> PLAYER_DETECTOR = Registration.TILE_ENTITIES.register("player_detector", ()->new TileEntityType<>(PlayerDetectorTileEntity::new, Sets.newHashSet(ModBlocks.PLAYER_DETECTOR.get()), null));
    public static final RegistryObject<TileEntityType<MeBridgeTileEntity>> ME_BRIDGE = registerMeBridge();
    public static final RegistryObject<TileEntityType<RsBridgeTileEntity>> RS_BRIDGE = registerRsBridge();

    static void register() {}

    private static RegistryObject<TileEntityType<MeBridgeTileEntity>> registerMeBridge() {
        if (ModList.get().isLoaded("appliedenergistics2")) {
            return Registration.TILE_ENTITIES.register("me_bridge", ()->new TileEntityType<>(MeBridgeTileEntity::new, Sets.newHashSet(ModBlocks.ME_BRIDGE.get()), null));
        }
        return null;
    }

    private static RegistryObject<TileEntityType<RsBridgeTileEntity>> registerRsBridge() {
        if (ModList.get().isLoaded("refinedstorage")) {
            return Registration.TILE_ENTITIES.register("rs_bridge", ()->new TileEntityType<>(RsBridgeTileEntity::new, Sets.newHashSet(ModBlocks.RS_BRIDGE.get()), null));
        }
        return null;
    }
}