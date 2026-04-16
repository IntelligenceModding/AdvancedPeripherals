package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IVisitorData;
import com.minecolonies.api.colony.managers.interfaces.IRegisteredStructureManager;
import com.minecolonies.api.colony.requestsystem.manager.IRequestManager;
import com.minecolonies.api.colony.requestsystem.requestable.IDeliverable;
import com.minecolonies.api.colony.requestsystem.resolver.player.IPlayerRequestResolver;
import com.minecolonies.api.colony.requestsystem.resolver.retrying.IRetryingRequestResolver;
import com.minecolonies.api.colony.workorders.IWorkOrder;
import com.minecolonies.api.research.IGlobalResearchTree;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.PocketPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.minecolonies.MineColonies;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ColonyPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "colony_integrator";

    protected boolean hasPermission = true;

    public ColonyPeripheral(PeripheralBlockEntity<?> tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    public ColonyPeripheral(IPocketAccess access) {
        super(PERIPHERAL_TYPE, PocketPeripheralOwner.of(access));
    }

    @Override
    public boolean isEnabled() {
        return APAddon.MINECOLONIES.isLoaded() && APConfig.PERIPHERALS_CONFIG.enableColonyIntegrator.get();
    }

    @LuaFunction(mainThread = true)
    public final boolean isInColony() {
        return getColonyWithoutPermission() != null;
    }

    @LuaFunction(mainThread = true)
    public final boolean isWithin(Map<?, ?> posTable) throws LuaException {
        IColony colony = getColonyWithoutPermission();
        if (colony == null) {
            return false;
        }

        BlockPos pos = LuaConverter.convertToBlockPos(posTable);
        return colony.isCoordInColony(this.getLevel(), pos);
    }

    @LuaFunction(mainThread = true)
    public final int amountOfConstructionSites() throws LuaException {
        return MineColonies.getAmountOfConstructionSites(getColonyOrThrow());
    }

    @LuaFunction(mainThread = true)
    public final int getColonyID() throws LuaException {
        IColony colony = getColonyOrThrow();

        return colony.getID();
    }

    @LuaFunction(mainThread = true)
    public final String getColonyName() throws LuaException {
        IColony colony = getColonyOrThrow();

        return colony.getName();
    }

    @LuaFunction(mainThread = true)
    public final String getColonyStyle() throws LuaException {
        IColony colony = getColonyOrThrow();

        return colony.getStructurePack();
    }

    @LuaFunction(mainThread = true)
    public final boolean isActive() throws LuaException {
        IColony colony = getColonyOrThrow();

        return colony.isActive();
    }

    @LuaFunction(mainThread = true)
    public final double getHappiness() throws LuaException {
        IColony colony = getColonyOrThrow();

        return colony.getOverallHappiness();
    }

    @LuaFunction(mainThread = true)
    public final Object getLocation() throws LuaException {
        IColony colony = getColonyOrThrow();

        return LuaConverter.posToLua(colony.getCenter());
    }

    @LuaFunction(mainThread = true)
    public final boolean isUnderAttack() throws LuaException {
        IColony colony = getColonyOrThrow();

        return colony.isColonyUnderAttack();
    }

    @LuaFunction(mainThread = true)
    public final boolean isUnderRaid() throws LuaException {
        IColony colony = getColonyOrThrow();

        return colony.getRaiderManager().isRaided();
    }

    @LuaFunction(mainThread = true)
    public final int getCitizenCount() throws LuaException {
        IColony colony = getColonyOrThrow();

        return colony.getCitizenManager().getCurrentCitizenCount();
    }

    @LuaFunction(mainThread = true)
    public final int getCitizenLimit() throws LuaException {
        IColony colony = getColonyOrThrow();

        return colony.getCitizenManager().getMaxCitizens();
    }

    @LuaFunction(mainThread = true)
    public final int getGraveCount() throws LuaException {
        IColony colony = getColonyOrThrow();

        return colony.getGraveManager().getGraves().size();
    }

    @LuaFunction(mainThread = true)
    public final List<Map<String, Object>> getCitizens() throws LuaException {
        IColony colony = getColonyOrThrow();

        return colony.getCitizenManager().getCitizens().stream()
            .map(MineColonies::citizenToLua)
            .toList();
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getCitizen(int id) throws LuaException {
        IColony colony = getColonyOrThrow();

        ICitizenData citizen = colony.getCitizenManager().getCivilian(id);
        if (citizen == null) {
            return null;
        }
        return MineColonies.citizenToLua(citizen);
    }

    @LuaFunction(mainThread = true)
    public final List<Map<String, Object>> getVisitors() throws LuaException {
        IColony colony = getColonyOrThrow();

        return colony.getVisitorManager().getCivilianDataMap().values().stream()
            .filter(IVisitorData.class::isInstance)
            .map(IVisitorData.class::cast)
            .map(MineColonies::visitorToLua)
            .toList();
    }

    @LuaFunction(mainThread = true)
    public final List<Map<String, Object>> getBuildings() throws LuaException {
        IColony colony = getColonyOrThrow();

        IRegisteredStructureManager buildingManager = colony.getServerBuildingManager();
        return buildingManager.getBuildings().values().stream()
            .map(building -> MineColonies.buildingToLua(building, buildingManager))
            .toList();
    }

    @LuaFunction(mainThread = true)
    public final List<Map<String, Object>> getWorkOrders() throws LuaException {
        IColony colony = getColonyOrThrow();

        IRegisteredStructureManager buildingManager = colony.getServerBuildingManager();
        return colony.getWorkManager().getWorkOrders().values().stream()
            .map(workOrder -> MineColonies.workOrderToLua(workOrder, buildingManager))
            .toList();
    }

    @LuaFunction(mainThread = true)
    public final Object getResearches() throws LuaException {
        IColony colony = getColonyOrThrow();

        IGlobalResearchTree globalTree = IGlobalResearchTree.getInstance();

        Map<String, Object> result = new HashMap<>();
        for (ResourceLocation branch : globalTree.getBranches()) {
            try {
                result.put(branch.toString(), MineColonies.getResearches(branch, globalTree.getPrimaryResearch(branch), colony));
            } catch (CommandSyntaxException ex) {
                AdvancedPeripherals.debug(org.apache.logging.log4j.Level.WARN, "Error getting research for branch {}: {}", branch, ex.getMessage());
                ex.printStackTrace();
            }
        }
        return result;
    }

    @LuaFunction(mainThread = true)
    public final Object getWorkOrderResources(int id) throws LuaException {
        IColony colony = getColonyOrThrow();

        IWorkOrder workOrder = colony.getWorkManager().getWorkOrder(id);
        if (workOrder == null) {
            return null;
        }
        return MineColonies.builderResourcesToLua(colony, colony.getServerBuildingManager().getBuilding(workOrder.getClaimedBy()));
    }

    @LuaFunction(mainThread = true)
    public final Object getBuilderResources(Map<?, ?> posTable) throws LuaException {
        IColony colony = getColonyOrThrow();

        BlockPos blockPos = LuaConverter.convertToBlockPos(posTable);
        return MineColonies.builderResourcesToLua(colony, colony.getServerBuildingManager().getBuilding(blockPos));
    }

    @LuaFunction(mainThread = true)
    public final Object getRequests() throws LuaException {
        IColony colony = getColonyOrThrow();

        IRequestManager requestManager = colony.getRequestManager();
        //All requests assigned to players
        IPlayerRequestResolver playerResolver = requestManager.getPlayerResolver();
        //All requests not assigned to players
        IRetryingRequestResolver requestResolver = requestManager.getRetryingRequestResolver();

        return Stream.concat(
            playerResolver.getAllAssignedRequests().stream(),
            requestResolver.getAllAssignedRequests().stream()
        )
            .distinct()
            .map(requestManager::getRequestForToken)
            .filter(request -> request.getRequest() instanceof IDeliverable)
            .map(request -> {
                IDeliverable deliverableRequest = (IDeliverable) request.getRequest();
                Map<String, Object> map = new HashMap<>();
                map.put("id", request.getId().getIdentifier().toString());
                map.put("displayName", ChatFormatting.stripFormatting(request.getShortDisplayString().getString()));
                map.put("description", ChatFormatting.stripFormatting(request.getLongDisplayString().getString()));
                map.put("state", request.getState().name());
                map.put("count", deliverableRequest.getCount());
                map.put("minCount", deliverableRequest.getMinimumCount());
                map.put("items", request.getDisplayStacks().stream().map(item -> LuaConverter.itemStackToLua(item)).toList());
                map.put("target", LuaConverter.posToLua(request.getRequester().getLocation().getInDimensionLocation()));
                return map;
            })
            .toList();
    }

    @NotNull
    private IColony getColonyOrThrow() throws LuaException {
        IColony colony = getColonyWithoutPermission();
        this.hasPermission = !(owner instanceof PocketPeripheralOwner) || MineColonies.hasAccess(owner.getOwner(), colony);
        if (colony == null || !this.hasPermission) {
            throw new LuaException("Here is no colony or you don't have the right permissions");
        }
        return colony;
    }

    @Nullable
    private IColony getColonyWithoutPermission() {
        IMinecoloniesAPI api = IMinecoloniesAPI.getInstance();
        return api.getColonyManager().getColonyByPosFromWorld(getLevel(), getPos());
    }
}
