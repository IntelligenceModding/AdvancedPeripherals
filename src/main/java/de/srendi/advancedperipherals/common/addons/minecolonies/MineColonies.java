package de.srendi.advancedperipherals.common.addons.minecolonies;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IVisitorData;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.managers.interfaces.IRegisteredStructureManager;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.colony.workorders.IWorkOrder;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.api.research.IGlobalResearch;
import com.minecolonies.api.research.IGlobalResearchTree;
import com.minecolonies.api.research.ILocalResearch;
import com.minecolonies.api.research.ILocalResearchTree;
import com.minecolonies.api.research.effects.IResearchEffect;
import com.minecolonies.api.research.util.ResearchState;
import com.minecolonies.coremod.colony.buildings.AbstractBuildingStructureBuilder;
import com.minecolonies.coremod.colony.buildings.utils.BuildingBuilderResource;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MineColonies {

    /**
     * To ensure that the user of a pocket computer has the appropriate rights.
     *
     * @param entity the user of the pocket computer
     * @param colony the colony where the user is in it
     * @return true if the user has the appropriate rights
     */
    public static boolean hasAccess(Entity entity, IColony colony) {
        if (entity instanceof Player player) {
            if (colony != null) {
                return colony.getPermissions().hasPermission(player, Action.ACCESS_HUTS);
            }
        }
        return false;
    }

    /**
     * Converts a citizen to a map
     *
     * @param citizen the citizen
     * @return a map with information about the citizen
     */
    public static Object citizenToObject(ICitizenData citizen) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", citizen.getId());
        map.put("name", citizen.getName());
        map.put("bedPos", LuaConverter.posToObject(citizen.getBedPos()));
        map.put("children", citizen.getChildren());
        map.put("location", LuaConverter.posToObject(citizen.getLastPosition()));
        map.put("state", citizen.getStatus() == null ? "Idle" : new TranslatableComponent(citizen.getStatus().getTranslationKey()).getString());
        map.put("age", citizen.isChild() ? "child" : "adult");
        map.put("gender", citizen.isFemale() ? "female" : "male");
        map.put("saturation", citizen.getSaturation());
        map.put("happiness", citizen.getCitizenHappinessHandler().getHappiness(citizen.getColony()));
        map.put("skills", skillsToObject(citizen.getCitizenSkillHandler().getSkills()));
        map.put("work", citizen.getWorkBuilding() == null ? null : jobToObject(citizen.getWorkBuilding()));
        map.put("home", citizen.getHomeBuilding() == null ? null : homeToObject(citizen.getHomeBuilding()));
        map.put("betterFood", citizen.needsBetterFood());
        map.put("isAsleep", map.get("state").toString().toLowerCase().contains("sleeping"));
        map.put("isIdle", map.get("state").toString().toLowerCase().contains("idle"));
        citizen.getEntity().ifPresent(entity -> {
            map.put("health", entity.getHealth());
            map.put("maxHealth", entity.getMaxHealth());
            map.put("armor", entity.getAttributeValue(Attributes.ARMOR));
            map.put("toughness", entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        });

        return map;
    }

    /**
     * Converts a visitor {@link IVisitorData} to a map
     *
     * @param visitor the visitor
     * @return a map with information about the visitor
     */
    public static Object visitorToObject(IVisitorData visitor) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", visitor.getId());
        map.put("name", visitor.getName());
        map.put("location", LuaConverter.posToObject(visitor.getSittingPosition()));
        map.put("age", visitor.isChild() ? "child" : "adult");
        map.put("gender", visitor.isFemale() ? "female" : "male");
        map.put("saturation", visitor.getSaturation());
        map.put("happiness", visitor.getCitizenHappinessHandler().getHappiness(visitor.getColony()));
        map.put("skills", skillsToObject(visitor.getCitizenSkillHandler().getSkills()));
        map.put("recruitCost", visitor.getRecruitCost().getItem().getRegistryName().toString());
        return map;
    }

    /**
     * Converts a job {@link IBuilding} to a map
     *
     * @param work the home building
     * @return a map with information about the job building
     */
    public static Object jobToObject(IBuilding work) {
        Map<String, Object> map = new HashMap<>();
        map.put("location", LuaConverter.posToObject(work.getLocation().getInDimensionLocation()));
        map.put("type", work.getSchematicName());
        map.put("level", work.getBuildingLevel());
        map.put("name", work.getBuildingDisplayName());
        return map;
    }

    /**
     * Converts a home {@link IBuilding} to a map
     *
     * @param home the home building
     * @return a map with information about the home building
     */
    public static Object homeToObject(IBuilding home) {
        Map<String, Object> map = new HashMap<>();
        map.put("location", LuaConverter.posToObject(home.getLocation().getInDimensionLocation()));
        map.put("type", home.getSchematicName());
        map.put("level", home.getBuildingLevel());
        return map;
    }

    /**
     * Converts a skill {@link Skill} into a map
     *
     * @param skills skills as list. Can be obtained via {@link ICitizenData#getCitizenSkillHandler}
     * @return a map with information about the skill
     */
    public static Object skillsToObject(Map<Skill, Tuple<Integer, Double>> skills) {
        Map<String, Object> map = new HashMap<>();
        for (Skill skill : skills.keySet()) {
            Map<String, Object> skillData = new HashMap<>();
            skillData.put("level", skills.get(skill).getA());
            skillData.put("xp", skills.get(skill).getB());
            map.put(skill.name(), skillData);
        }
        return map;
    }

    /**
     * Returns information about the building like structure data, the citizens and some other values
     *
     * @param buildingManager The building manager of the colony
     * @param building        The building as instance
     * @param pos             The location of the buildings block
     * @return information about the building
     */
    public static Object buildingToObject(IRegisteredStructureManager buildingManager, IBuilding building, BlockPos pos) {
        Map<String, Object> structureData = new HashMap<>();
        structureData.put("cornerA", LuaConverter.posToObject(building.getCorners().getA()));
        structureData.put("cornerB", LuaConverter.posToObject(building.getCorners().getB()));
        structureData.put("rotation", building.getRotation());
        structureData.put("mirror", building.isMirrored());

        List<Object> citizensData = new ArrayList<>();
        for (ICitizenData citizen : building.getAllAssignedCitizen()) {
            Map<String, Object> citizenData = new HashMap<>();
            citizenData.put("id", citizen.getId());
            citizenData.put("name", citizen.getName());
            citizensData.add(citizenData);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("location", LuaConverter.posToObject(pos));
        map.put("type", building.getSchematicName());
        map.put("style", building.getStyle());
        map.put("level", building.getBuildingLevel());
        map.put("maxLevel", building.getMaxBuildingLevel());
        map.put("name", building.getBuildingDisplayName());
        map.put("built", building.isBuilt());
        map.put("isWorkingOn", building.hasWorkOrder());
        map.put("priority", building.getPickUpPriority());
        map.put("structure", structureData);
        map.put("citizens", citizensData);
        map.put("storageBlocks", building.getContainers().size());
        map.put("storageSlots", getStorageSize(building));
        map.put("guarded", buildingManager.hasGuardBuildingNear(building));
        return map;
    }

    /**
     * Returns the size of all inventories in this building
     *
     * @param building the proper building with racks(Or other inventories)
     * @return the size of all inventories in this building
     */
    public static int getStorageSize(IBuilding building) {
        LazyOptional<IItemHandler> capability = building.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        IItemHandler handler = capability.resolve().orElse(null);
        if (handler != null) return handler.getSlots();
        return 0;
    }

    public static int getAmountOfConstructionSites(IColony colony) {
        int constructionSites = 0;
        for (IBuilding building : colony.getBuildingManager().getBuildings().values()) {
            if (building.hasWorkOrder()) constructionSites++;
        }
        return constructionSites;
    }

    public static Object workOrderToObject(IWorkOrder workOrder) {
        Map<String, Object> map = new HashMap<>();

        map.put("builder", LuaConverter.posToObject(workOrder.getClaimedBy()));
        map.put("changed", workOrder.isDirty());
        map.put("id", workOrder.getID());
        map.put("priority", workOrder.getPriority());
        map.put("isClaimed", workOrder.isClaimed());
        map.put("location", LuaConverter.posToObject(workOrder.getLocation()));
        map.put("type", workOrder.getClass().getSimpleName());
        map.put("buildingName", workOrder.getDisplayName().getString());
        map.put("targetLevel", workOrder.getTargetLevel());
        map.put("workOrderType", workOrder.getWorkOrderType().toString());

        return map;
    }

    /**
     * Returns a map with all possible researches
     *
     * @param branch     The branch, there are only a few branches
     * @param researches The primary researches of the branch
     * @param colony     The colony
     * @return a map with all possible researches
     */
    public static List<Object> getResearch(ResourceLocation branch, List<ResourceLocation> researches, IColony colony) {
        List<Object> result = new ArrayList<>();
        if (researches != null) {
            for (ResourceLocation researchName : researches) {
                //All global possible researches
                IGlobalResearchTree globalTree = IGlobalResearchTree.getInstance();
                //The research tree of the colony
                ILocalResearchTree colonyTree = colony.getResearchManager().getResearchTree();

                IGlobalResearch research = globalTree.getResearch(branch, researchName);
                if (research == null) continue;
                ILocalResearch colonyResearch = colonyTree.getResearch(branch, researchName);

                List<String> effects = new ArrayList<>();
                for (IResearchEffect<?> researchEffect : research.getEffects())
                    effects.add(researchEffect.getDesc().toString());

                Map<String, Object> map = new HashMap<>();
                map.put("id", researchName.toString());
                map.put("name", research.getName().getString());
                map.put("researchEffects", effects);
                map.put("status", colonyResearch == null ? ResearchState.NOT_STARTED.toString() : colonyResearch.getState().toString());

                List<Object> childrenResearch = getResearch(branch, research.getChildren(), colony);
                if (!childrenResearch.isEmpty()) map.put("children", childrenResearch);

                result.add(map);
            }
        }
        return result;
    }

    /**
     * Returns the resources(As items) which the builder needs
     *
     * @param colony The colony
     * @param pos    The position of the builder's hut block
     * @return a map with all needed resources
     */
    public static Object builderResourcesToObject(IColony colony, BlockPos pos) {
        IBuilding building = colony.getBuildingManager().getBuilding(pos);
        if (!(building instanceof AbstractBuildingStructureBuilder builderBuilding)) return null;

        //We need to say the building that we want information about it
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        builderBuilding.serializeToView(buffer);
        buffer.release();

        List<BuildingBuilderResource> resources = new ArrayList<>(builderBuilding.getNeededResources().values());
        resources.sort(new BuildingBuilderResource.ResourceComparator());

        List<Object> result = new ArrayList<>();
        for (BuildingBuilderResource resource : resources) {
            Map<String, Object> map = new HashMap<>();

            map.put("item", resource.getItemStack().copy().getItem().getRegistryName().toString());
            map.put("displayName", resource.getName());
            map.put("available", resource.getAvailable());
            map.put("delivering", resource.getAmountInDelivery());
            map.put("status", resource.getAvailabilityStatus().toString());
            map.put("needed", resource.getAmount());
            result.add(map);
        }

        return result;
    }

}
