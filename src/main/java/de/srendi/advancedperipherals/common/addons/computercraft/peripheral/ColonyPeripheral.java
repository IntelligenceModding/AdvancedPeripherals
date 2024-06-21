package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.ICivilianData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IVisitorData;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.managers.interfaces.IRegisteredStructureManager;
import com.minecolonies.api.colony.requestsystem.manager.IRequestManager;
import com.minecolonies.api.colony.requestsystem.request.IRequest;
import com.minecolonies.api.colony.requestsystem.requestable.IDeliverable;
import com.minecolonies.api.colony.requestsystem.resolver.player.IPlayerRequestResolver;
import com.minecolonies.api.colony.requestsystem.resolver.retrying.IRetryingRequestResolver;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.colony.workorders.IWorkOrder;
import com.minecolonies.api.research.IGlobalResearchTree;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.AdvancedPeripherals;
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
import net.minecraftforge.fml.ModList;

import java.util.*;
import java.util.stream.Collectors;

public class ColonyPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "colony_integrator";

    protected boolean hasPermission = true;

    public ColonyPeripheral(PeripheralBlockEntity<?> tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    public ColonyPeripheral(IPocketAccess access) {
        super(PERIPHERAL_TYPE, new PocketPeripheralOwner(access));
    }

    @Override
    public boolean isEnabled() {
        return ModList.get().isLoaded("minecolonies") && APConfig.PERIPHERALS_CONFIG.enableColonyIntegrator.get();
    }

    @LuaFunction(mainThread = true)
    public final boolean isInColony() {
        return getColonyWithoutPermission() != null;
    }

    @LuaFunction(mainThread = true)
    public final boolean isWithin(Map<?, ?> pos) throws LuaException {
        IColony colony = getColonyWithoutPermission();

        if (!(pos.containsKey("x") && pos.containsKey("y") && pos.containsKey("z")))
            throw new LuaException("Coordinates expected");
        BlockPos p = new BlockPos(((Number) pos.get("x")).intValue(), ((Number) pos.get("y")).intValue(), ((Number) pos.get("z")).intValue());

        return colony.isCoordInColony(this.getLevel(), p);
    }

    @LuaFunction(mainThread = true)
    public final Object getCitizens() throws LuaException {
        IColony colony = getColony();

        List<Object> list = new ArrayList<>();
        colony.getCitizenManager().getCitizens().forEach(citizen -> {
            list.add(MineColonies.citizenToObject(citizen));
        });

        return list;
    }

    @LuaFunction(mainThread = true)
    public final int amountOfConstructionSites() throws LuaException {
        return MineColonies.getAmountOfConstructionSites(getColony());
    }

    @LuaFunction(mainThread = true)
    public final int getColonyID() throws LuaException {
        IColony colony = getColony();

        return colony.getID();
    }

    @LuaFunction(mainThread = true)
    public final String getColonyName() throws LuaException {
        IColony colony = getColony();

        return colony.getName();
    }

    @LuaFunction(mainThread = true)
    public final String getColonyStyle() throws LuaException {
        IColony colony = getColony();

        return colony.getStructurePack();
    }

    @LuaFunction(mainThread = true)
    public final boolean isActive() throws LuaException {
        IColony colony = getColony();

        return colony.isActive();
    }

    @LuaFunction(mainThread = true)
    public final double getHappiness() throws LuaException {
        IColony colony = getColony();

        return colony.getOverallHappiness();
    }

    @LuaFunction(mainThread = true)
    public final Object getLocation() throws LuaException {
        IColony colony = getColony();

        return LuaConverter.posToObject(colony.getCenter());
    }

    @LuaFunction(mainThread = true)
    public final boolean isUnderAttack() throws LuaException {
        IColony colony = getColony();

        return colony.isColonyUnderAttack();
    }

    @LuaFunction(mainThread = true)
    public final boolean isUnderRaid() throws LuaException {
        IColony colony = getColony();

        return colony.getRaiderManager().isRaided();
    }

    @LuaFunction(mainThread = true)
    public final int amountOfCitizens() throws LuaException {
        IColony colony = getColony();

        return colony.getCitizenManager().getCurrentCitizenCount();
    }

    @LuaFunction(mainThread = true)
    public final int maxOfCitizens() throws LuaException {
        IColony colony = getColony();

        return colony.getCitizenManager().getMaxCitizens();
    }

    @LuaFunction(mainThread = true)
    public final int amountOfGraves() throws LuaException {
        IColony colony = getColony();

        return colony.getGraveManager().getGraves().size();
    }

    @LuaFunction(mainThread = true)
    public final Object getVisitors() throws LuaException {
        IColony colony = getColony();

        List<Object> list = new ArrayList<>();
        for (ICivilianData civilian : colony.getVisitorManager().getCivilianDataMap().values()) {
            if (!(civilian instanceof IVisitorData visitorData))
                continue;
            list.add(MineColonies.visitorToObject(visitorData));
        }
        return list;
    }

    @LuaFunction(mainThread = true)
    public final Object getBuildings() throws LuaException {
        IColony colony = getColony();

        IRegisteredStructureManager manager = colony.getBuildingManager();
        List<Object> buildingData = new ArrayList<>();
        for (Map.Entry<BlockPos, IBuilding> building : manager.getBuildings().entrySet()) {
            buildingData.add(MineColonies.buildingToObject(manager, building.getValue(), building.getKey()));
        }

        return buildingData;
    }

    @LuaFunction(mainThread = true)
    public final Object getWorkOrders() throws LuaException {
        IColony colony = getColony();

        List<Object> worksData = new ArrayList<>();
        for (IWorkOrder workOrder : colony.getWorkManager().getWorkOrders().values())
            worksData.add(MineColonies.workOrderToObject(workOrder));

        return worksData;
    }

    @LuaFunction(mainThread = true)
    public final Object getResearch() throws LuaException {
        IColony colony = getColony();

        IGlobalResearchTree globalTree = IGlobalResearchTree.getInstance();

        Map<String, Object> result = new HashMap<>();
        for (ResourceLocation branch : globalTree.getBranches()) {
            try {
                result.put(branch.toString(), MineColonies.getResearch(branch, globalTree.getPrimaryResearch(branch), colony));
            } catch (CommandSyntaxException ex) {
                AdvancedPeripherals.debug("Error getting research for branch " + branch.toString() + ": " + ex.getMessage(), org.apache.logging.log4j.Level.WARN);
                ex.printStackTrace();
            }
        }

        return result;
    }

    @LuaFunction(mainThread = true)
    public final Object getWorkOrderResources(int id) throws LuaException {
        IColony colony = getColony();

        IWorkOrder workOrder = colony.getWorkManager().getWorkOrder(id);
        if (workOrder == null) return null;

        return MineColonies.builderResourcesToObject(colony, workOrder.getClaimedBy());
    }

    @LuaFunction(mainThread = true)
    public final Object getBuilderResources(Map<?, ?> pos) throws LuaException {
        IColony colony = getColony();

        if (!(pos.containsKey("x") && pos.containsKey("y") && pos.containsKey("z")))
            throw new LuaException("Coordinates expected");
        BlockPos blockPos = LuaConverter.convertToBlockPos(pos);

        return MineColonies.builderResourcesToObject(colony, blockPos);
    }

    @LuaFunction(mainThread = true)
    public final Object getRequests() throws LuaException {
        IColony colony = getColony();

        IRequestManager requestManager = colony.getRequestManager();
        //All requests assigned to players
        IPlayerRequestResolver playerResolver = requestManager.getPlayerResolver();
        //All requests not assigned to players
        IRetryingRequestResolver requestResolver = requestManager.getRetryingRequestResolver();

        Set<IToken<?>> tokens = new HashSet<>();
        tokens.addAll(playerResolver.getAllAssignedRequests());
        tokens.addAll(requestResolver.getAllAssignedRequests());

        List<IRequest<?>> requests = new ArrayList<>();
        for (IToken<?> token : tokens) {
            IRequest<?> request = requestManager.getRequestForToken(token);
            if (request.getRequest() instanceof IDeliverable)
                requests.add(request);
        }

        List<Object> result = new ArrayList<>();
        requests.forEach(request -> {
            IDeliverable deliverableRequest = (IDeliverable) request.getRequest();
            Map<String, Object> map = new HashMap<>();
            map.put("id", request.getId().getIdentifier().toString());
            map.put("name", ChatFormatting.stripFormatting(request.getShortDisplayString().getString()));
            map.put("desc", ChatFormatting.stripFormatting(request.getLongDisplayString().getString()));
            map.put("state", request.getState().toString());
            map.put("count", deliverableRequest.getCount());
            map.put("minCount", deliverableRequest.getMinimumCount());
            map.put("items", request.getDisplayStacks().stream().map(LuaConverter::itemStackToObject).collect(Collectors.toList()));
            map.put("target", request.getRequester().getRequesterDisplayName(requestManager, request).getString());
            result.add(map);
        });
        return result;
    }

    private IColony getColony() throws LuaException {
        IMinecoloniesAPI api = IMinecoloniesAPI.getInstance();
        IColony colony = api.getColonyManager().getColonyByPosFromWorld(getLevel(), getPos());
        this.hasPermission = !(owner instanceof PocketPeripheralOwner) || MineColonies.hasAccess(owner.getOwner(), colony);
        if (colony == null || !this.hasPermission)
            throw new LuaException("Here is no colony or you don't have the right permissions");
        return colony;
    }

    private IColony getColonyWithoutPermission() {
        IMinecoloniesAPI api = IMinecoloniesAPI.getInstance();
        return api.getColonyManager().getColonyByPosFromWorld(getLevel(), getPos());
    }
}
