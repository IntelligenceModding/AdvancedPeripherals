package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import dan200.computercraft.api.lua.LuaFunction;

public abstract class OverlayObject {

    private boolean enabled = true;
    private final String id;
    private final OverlayModule module;

    public OverlayObject(String id, OverlayModule module) {
        this.id = id;
        this.module = module;
    }

    @LuaFunction
    public final String getId() {
        return id;
    }

    public OverlayModule getModule() {
        return module;
    }

    @LuaFunction
    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @LuaFunction
    public final boolean isEnabled() {
        return enabled;
    }
}
