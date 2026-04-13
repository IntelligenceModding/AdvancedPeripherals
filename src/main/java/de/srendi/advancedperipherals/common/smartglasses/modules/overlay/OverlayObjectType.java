package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import de.srendi.advancedperipherals.client.smartglasses.objects.IObjectRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class OverlayObjectType<T extends OverlayObject> {
    private final Function<OverlayModule, @NotNull T> serverFactory;
    private final Function<UUID, @NotNull T> clientFactory;
    private final Supplier<IObjectRenderer> renderer;
    private IObjectRenderer rendererInst = null;

    public OverlayObjectType(Function<OverlayModule, @NotNull T> serverFactory, Function<UUID, @NotNull T> clientFactory, Supplier<IObjectRenderer> renderer) {
        this.serverFactory = serverFactory;
        this.clientFactory = clientFactory;
        this.renderer = renderer;
    }

    @NotNull
    public T createServer(OverlayModule module) {
        return this.serverFactory.apply(module);
    }

    @NotNull
    public T createClient(UUID player) {
        return this.clientFactory.apply(player);
    }

    @NotNull
    public IObjectRenderer getRenderer() {
        if (this.rendererInst == null) {
            this.rendererInst = this.renderer.get();
        }
        return this.rendererInst;
    }
}
