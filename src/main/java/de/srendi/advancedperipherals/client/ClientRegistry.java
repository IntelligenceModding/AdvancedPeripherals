package de.srendi.advancedperipherals.client;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
            "turtle_player_upgrade_right"
    };

    @SubscribeEvent
    public static void onModelBakeEvent(ModelBakeEvent event) {
        //Loading turtle models
        //Adapted from CC-Tweaked
        ModelLoader loader = event.getModelLoader();
        Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();

        for (String modelName : TURTLE_MODELS) {
            ResourceLocation location = new ResourceLocation(AdvancedPeripherals.MOD_ID, "item/" + modelName);
            IUnbakedModel model = loader.getModelOrMissing(location);
            model.getTextures(loader::getModelOrMissing, new HashSet<>());

            IBakedModel baked = model.bakeModel(loader, ModelLoader.defaultTextureGetter(), SimpleModelTransform.IDENTITY, location);
            if (baked != null) {
                registry.put(new ModelResourceLocation(new ResourceLocation(AdvancedPeripherals.MOD_ID, modelName), "inventory"), baked);
            }
        }
    }

}