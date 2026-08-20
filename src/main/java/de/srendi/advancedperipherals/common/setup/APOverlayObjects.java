package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.BlockRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.BoxRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.SphereRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.TextureRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.TorusRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.threedim.TriangleRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.CircleRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.ItemRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.LineRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.RectangleRenderer;
import de.srendi.advancedperipherals.client.smartglasses.objects.twodim.TextRenderer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BlockObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BoxObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.SphereObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.TextureObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.TorusObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.TriangleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.CircleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.ItemObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.LineObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RectangleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.TextObject;
import net.minecraftforge.registries.RegistryObject;

public final class APOverlayObjects {
    public static final RegistryObject<OverlayObjectType<BlockObject>> BLOCK =
        APRegistration.OVERLAY_OBJECTS.register("block", () -> new OverlayObjectType<>(BlockObject::new, BlockObject::new, () -> new BlockRenderer()));
    public static final RegistryObject<OverlayObjectType<BoxObject>> BOX =
        APRegistration.OVERLAY_OBJECTS.register("box", () -> new OverlayObjectType<>(BoxObject::new, BoxObject::new, () -> new BoxRenderer()));
    public static final RegistryObject<OverlayObjectType<SphereObject>> SPHERE =
        APRegistration.OVERLAY_OBJECTS.register("sphere", () -> new OverlayObjectType<>(SphereObject::new, SphereObject::new, () -> new SphereRenderer()));
    public static final RegistryObject<OverlayObjectType<TorusObject>> TORUS =
        APRegistration.OVERLAY_OBJECTS.register("torus", () -> new OverlayObjectType<>(TorusObject::new, TorusObject::new, () -> new TorusRenderer()));
    public static final RegistryObject<OverlayObjectType<TextureObject>> TEXTURE =
        APRegistration.OVERLAY_OBJECTS.register("texture", () -> new OverlayObjectType<>(TextureObject::new, TextureObject::new, () -> new TextureRenderer()));
    public static final RegistryObject<OverlayObjectType<TriangleObject>> TRIANGLE =
        APRegistration.OVERLAY_OBJECTS.register("triangle", () -> new OverlayObjectType<>(TriangleObject::new, TriangleObject::new, () -> TriangleRenderer.INSTANCE));
    public static final RegistryObject<OverlayObjectType<CircleObject>> CIRCLE =
        APRegistration.OVERLAY_OBJECTS.register("circle", () -> new OverlayObjectType<>(CircleObject::new, CircleObject::new, () -> new CircleRenderer()));
    public static final RegistryObject<OverlayObjectType<ItemObject>> ITEM =
        APRegistration.OVERLAY_OBJECTS.register("item", () -> new OverlayObjectType<>(ItemObject::new, ItemObject::new, () -> new ItemRenderer()));
    public static final RegistryObject<OverlayObjectType<LineObject>> LINE =
        APRegistration.OVERLAY_OBJECTS.register("line", () -> new OverlayObjectType<>(LineObject::new, LineObject::new, () -> new LineRenderer()));
    public static final RegistryObject<OverlayObjectType<RectangleObject>> RECTANGLE =
        APRegistration.OVERLAY_OBJECTS.register("rectangle", () -> new OverlayObjectType<>(RectangleObject::new, RectangleObject::new, () -> new RectangleRenderer()));
    public static final RegistryObject<OverlayObjectType<TextObject>> TEXT =
        APRegistration.OVERLAY_OBJECTS.register("text", () -> new OverlayObjectType<>(TextObject::new, TextObject::new, () -> new TextRenderer()));

    public static void register() {}
}
