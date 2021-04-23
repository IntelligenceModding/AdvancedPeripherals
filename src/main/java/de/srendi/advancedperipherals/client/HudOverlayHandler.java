package de.srendi.advancedperipherals.client;

import java.util.ArrayList;
import java.util.List;

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

@OnlyIn(Dist.CLIENT)
public class HudOverlayHandler {
	private static HudOverlayHandler instance;
	private List<ARRenderAction> canvas = new ArrayList<ARRenderAction>();

	public static void init() {
		instance = new HudOverlayHandler();
		MinecraftForge.EVENT_BUS.register(instance);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onRender(RenderGameOverlayEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		MatrixStack matrixStack = event.getMatrixStack();
		for (ARRenderAction action : canvas) {
			action.draw(mc, matrixStack, event.getWindow().getScaledWidth(), event.getWindow().getScaledHeight());
		}
		mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
	}

	public static HudOverlayHandler getInstance() {
		return instance;
	}
	
	public static void updateCanvas(List<ARRenderAction> actions) {
		if (instance == null)
			return;
		instance.canvas.clear();
		for (ARRenderAction action : actions) {
			instance.canvas.add(action);
		}
	}

	public static void clearCanvas() {
		if (instance == null)
			return;
		instance.canvas.clear();
	}
}
