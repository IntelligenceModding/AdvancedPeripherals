package de.srendi.advancedperipherals.common.addons.ae2;

import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import de.srendi.advancedperipherals.common.addons.APAddon;
import net.pedroksl.advanced_ae.common.cluster.AdvCraftingCPU;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public interface ICpuWrapper {
    @Nullable
    ICraftingLink getLastLink();

    @Nullable
    default UUID getCraftingID() {
        ICraftingLink link = this.getLastLink();
        return link == null ? null : link.getCraftingID();
    }

    @Nullable
    GenericStack getFinalJobOutput();
    long getPendingOutputs(AEKey key);
    long getWaitingFor(AEKey key);

    default long getToCraft(AEKey key) {
        return getPendingOutputs(key) + getWaitingFor(key);
    }

    void addListener(Consumer<AEKey> listener);
    void removeListener(Consumer<AEKey> listener);

    static ICpuWrapper of(ICraftingCPU cpu) {
        if (cpu == null) {
            return null;
        }
        if (cpu instanceof CraftingCPUCluster cluster) {
            return new CraftingCPUClusterWrapper(cluster);
        }
        if (APAddon.ADVANCED_AE.isLoaded() && cpu instanceof AdvCraftingCPU advCpu) {
            return new AdvCraftingCPUWrapper(advCpu);
        }
        return null;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class CraftingCPUClusterWrapper implements ICpuWrapper {
    private final CraftingCPUCluster cpu;

    CraftingCPUClusterWrapper(CraftingCPUCluster cpu) {
        this.cpu = cpu;
    }

    @Override
    @Nullable
    public ICraftingLink getLastLink() {
        return this.cpu.craftingLogic.getLastLink();
    }

    @Override
    @Nullable
    public GenericStack getFinalJobOutput() {
        return this.cpu.craftingLogic.getFinalJobOutput();
    }

    @Override
    public long getPendingOutputs(AEKey key) {
        return this.cpu.craftingLogic.getPendingOutputs(key);
    }

    @Override
    public long getWaitingFor(AEKey key) {
        return this.cpu.craftingLogic.getWaitingFor(key);
    }

    @Override
    public void addListener(Consumer<AEKey> listener) {
        this.cpu.craftingLogic.addListener(listener);
    }

    @Override
    public void removeListener(Consumer<AEKey> listener) {
        this.cpu.craftingLogic.removeListener(listener);
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class AdvCraftingCPUWrapper implements ICpuWrapper {
    private final AdvCraftingCPU cpu;

    AdvCraftingCPUWrapper(AdvCraftingCPU cpu) {
        this.cpu = cpu;
    }

    @Override
    @Nullable
    public ICraftingLink getLastLink() {
        return this.cpu.craftingLogic.getLastLink();
    }

    @Override
    @Nullable
    public GenericStack getFinalJobOutput() {
        return this.cpu.craftingLogic.getFinalJobOutput();
    }

    @Override
    public long getPendingOutputs(AEKey key) {
        return this.cpu.craftingLogic.getPendingOutputs(key);
    }

    @Override
    public long getWaitingFor(AEKey key) {
        return this.cpu.craftingLogic.getWaitingFor(key);
    }

    @Override
    public void addListener(Consumer<AEKey> listener) {
        this.cpu.craftingLogic.addListener(listener);
    }

    @Override
    public void removeListener(Consumer<AEKey> listener) {
        this.cpu.craftingLogic.removeListener(listener);
    }
}
