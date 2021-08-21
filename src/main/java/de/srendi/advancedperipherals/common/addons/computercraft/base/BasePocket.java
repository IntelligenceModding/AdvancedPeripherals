package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class BasePocket<T extends BasePeripheral> extends AbstractPocketUpgrade {

    protected T peripheral;

    protected BasePocket(ResourceLocation id, String adjective, Supplier<? extends ItemLike> stack) {
        super(id, adjective, stack);
    }

    protected BasePocket(ResourceLocation id, Supplier<? extends ItemLike> stack) {
        super(id, TranslationUtil.pocket(id.getPath()), stack);
    }

    protected abstract T getPeripheral(IPocketAccess access);

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull IPocketAccess iPocketAccess) {
        peripheral = getPeripheral(iPocketAccess);
        if (!peripheral.isEnabled())
            return DisabledPeripheral.INSTANCE;
        return peripheral;
    }
}
