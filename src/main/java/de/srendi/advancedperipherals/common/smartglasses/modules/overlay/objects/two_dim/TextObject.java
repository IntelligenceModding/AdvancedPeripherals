package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim;

import de.srendi.advancedperipherals.common.setup.APOverlayObjects;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.FloatingNumberProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.StringProperty;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TextObject extends RenderableObject {
    @StringProperty
    public String content = "";

    @FloatingNumberProperty(min = 0, max = 128)
    public float fontSize = 1;

    @BooleanProperty(getterPrefix = "has")
    public boolean shadow = false;

    @BooleanProperty
    public boolean center = false;

    public TextObject(OverlayModule module) {
        super(module);
    }

    public TextObject(UUID player) {
        super(player);
    }

    @Override
    @NotNull
    public OverlayObjectType<TextObject> getType() {
        return APOverlayObjects.TEXT.get();
    }
}
