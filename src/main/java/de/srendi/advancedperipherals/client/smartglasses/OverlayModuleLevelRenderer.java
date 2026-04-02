package de.srendi.advancedperipherals.client.smartglasses;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.IThreeDObjectRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(Dist.CLIENT)
public class OverlayModuleLevelRenderer {

    @SubscribeEvent
    public static void renderLevelState(RenderLevelStageEvent event) {
        PoseStack poseStack = event.getPoseStack();
        Vec3 view = event.getCamera().getPosition();

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            Map<Class<? extends ThreeDimensionalObject>, List<ThreeDimensionalObject>> batches = new HashMap<>();

            for (RenderableObject object : OverlayObjectHolder.getObjects()) {
                if (!object.isEnabled()) {
                    continue;
                }
                if (!(object instanceof ThreeDimensionalObject threeDimObject)) {
                    continue;
                }

                Class<? extends ThreeDimensionalObject> objectClass = threeDimObject.getClass();

                List<ThreeDimensionalObject> batchList = batches.get(objectClass);
                if (batchList == null) {
                    batchList = new ArrayList<>();
                    batches.put(objectClass, batchList);
                }
                batchList.add(threeDimObject);
            }

            for (List<ThreeDimensionalObject> batch : batches.values()) {
                ((IThreeDObjectRenderer) batch.get(0).getObjectRenderer()).renderBatch(batch, event, poseStack, view);
            }
        }
    }
}
