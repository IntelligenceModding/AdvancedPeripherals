package de.srendi.advancedperipherals.client;

import com.mojang.blaze3d.platform.ScreenManager;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.container.InventoryManagerScreen;
import de.srendi.advancedperipherals.common.setup.ContainerTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.HashSet;
import java.util.Map;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistry {

    private static final String[] TURTLE_MODELS = new String[]{
            "turtle_chat_box_upgrade_left",
            "turtle_chat_box_upgrade_right",
            "turtle_environment_upgrade_left",
            "turtle_environment_upgrade_right",
            "turtle_player_upgrade_left",
            "turtle_player_upgrade_right",
            "turtle_geoscanner_upgrade_left",
            "turtle_geoscanner_upgrade_right"
    };

    @SubscribeEvent
    public static void onModelBakeEvent(ModelBakeEvent event) {
        //Loading turtle models
        //Adapted from CC-Tweaked
        ModelLoader loader = event.getModelLoader();
        Map<ResourceLocation, BakedModel> registry = event.getModelRegistry();

        for (String modelName : TURTLE_MODELS) {
            ResourceLocation location = new ResourceLocation(AdvancedPeripherals.MOD_ID, "item/" + modelName);
            UnbakedModel model = loader.getModelOrMissing(location);
            model.getMaterials(loader::getModelOrMissing, new HashSet<>());

            BakedModel baked = model.bake(loader, ModelLoader.defaultTextureGetter(), SimpleModelState.IDENTITY, location);
            if (baked != null) {
                registry.put(new ModelResourceLocation(new ResourceLocation(AdvancedPeripherals.MOD_ID, modelName), "inventory"), baked);
            }
        }
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(ContainerTypes.INVENTORY_MANAGER_CONTAINER.get(), InventoryManagerScreen::new);
        KeyBindings.register();
    }

    //TODO change the icon of the curio icon
    /*@SubscribeEvent
    public static void onTextureStitching(TextureStitchEvent.Pre event) {
        event.addSprite(new ResourceLocation(AdvancedPeripherals.MOD_ID, "item/empty_glasses_slot"));
    }*/
}