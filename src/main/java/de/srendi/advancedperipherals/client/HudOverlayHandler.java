package de.srendi.advancedperipherals.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.srendi.advancedperipherals.common.argoggles.ARRenderAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class HudOverlayHandler {
    private static HudOverlayHandler instance;
    private List<ARRenderAction> canvas = new ArrayList<ARRenderAction>();

    public static void init() {
        instance = new HudOverlayHandler();
        MinecraftForge.EVENT_BUS.register(instance);
    }

    public static HudOverlayHandler getInstance() {
        return instance;
    }

    public static void updateCanvas(List<ARRenderAction> actions) {
        if (instance == null)
            return;
        instance.canvas.clear();
        instance.canvas.addAll(actions);
    }

    public static void clearCanvas() {
        if (instance == null)
            return;
        instance.canvas.clear();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.getWindow() == null) return;
        Minecraft mc = Minecraft.getInstance();
        MatrixStack matrixStack = event.getMatrixStack();
        for (ARRenderAction action : canvas) {
            action.draw(mc, matrixStack, event.getWindow().getScreenWidth(), event.getWindow().getScreenHeight());
        }
        mc.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
    }
}
