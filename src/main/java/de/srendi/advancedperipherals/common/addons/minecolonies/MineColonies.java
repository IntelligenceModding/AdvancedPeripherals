package de.srendi.advancedperipherals.common.addons.minecolonies;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IVisitorData;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.IBuildingWorker;
import com.minecolonies.api.colony.managers.interfaces.IBuildingManager;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.api.research.*;
import com.minecolonies.api.research.effects.IResearchEffect;
import com.minecolonies.api.research.util.ResearchState;
import com.minecolonies.coremod.colony.buildings.AbstractBuildingStructureBuilder;
import com.minecolonies.coremod.colony.buildings.utils.BuildingBuilderResource;
import de.srendi.advancedperipherals.common.addons.computercraft.base.Converter;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MineColonies {
    //Todo: 0.7r Add pocket computer version

    /**
     * To ensure that the user of a pocket computer has the appropriate rights.
     *
     * @param entity the user of the pocket computer
     * @param colony the colony where the user is in it
     * @return true if the user has the appropriate rights
     */
    public static boolean hasAccess(Entity entity, IColony colony) {
        if (entity instanceof PlayerEntity) {
            if (colony != null) {
                return colony.getPermissions().hasPermission((PlayerEntity) entity, Action.ACCESS_HUTS);
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
        map.put("bedPos", Converter.posToObject(citizen.getBedPos()));
        map.put("children", citizen.getChildren());
        map.put("location", Converter.posToObject(citizen.getLastPosition()));
        map.put("state", citizen.getStatus() == null ? "Idle" : citizen.getStatus().getTranslatedText());
        map.put("age", citizen.isChild() ? "child" : "adult");
        map.put("gender", citizen.isFemale() ? "female" : "male");
        map.put("saturation", citizen.getSaturation());
        map.put("happiness", citizen.getCitizenHappinessHandler().getHappiness(citizen.getColony()));
        map.put("skills", skillsToObject(citizen.getCitizenSkillHandler().getSkills()));
        map.put("work", citizen.getWorkBuilding() == null ? null : jobToObject(citizen.getWorkBuilding()));
        map.put("home", citizen.getHomeBuilding() == null ? null : homeToObject(citizen.getHomeBuilding()));
        map.put("betterFood", citizen.needsBetterFood());
        //For some reason, ICitizenData#isAsleep and isIdleAtJob does not really work, we use this way
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
     * Converts a visitor to a map
     *
     * @param visitor the visitor
     * @return a map with information about the visitor
     */
    public static Object visitorToObject(IVisitorData visitor) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", visitor.getId());
        map.put("name", visitor.getName());
        map.put("location", Converter.posToObject(visitor.getSittingPosition()));
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
    public static Object jobToObject(IBuildingWorker work) {
        Map<String, Object> map = new HashMap<>();
        map.put("location", Converter.posToObject(work.getLocation().getInDimensionLocation()));
        map.put("type", work.getSchematicName());
        map.put("level", work.getBuildingLevel());
        map.put("name", work.getJobName());
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
        map.put("location", Converter.posToObject(home.getLocation().getInDimensionLocation()));
        map.put("type", home.getSchematicName());
        map.put("level", home.getBuildingLevel());
        return map;
    }

    /**
     * Converts a skill into a map
     * @param skills skills as list. Can be obtained via {@link ICitizenData#getCitizenSkillHandler#getSkills}
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
     * @param building The building as instance
     * @param pos The location of the buildings block
     * @return information about the building
     */
    public static Object buildingToObject(IBuildingManager buildingManager, IBuilding building, BlockPos pos) {
        Map<String, Object> structureData = new HashMap<>();
        structureData.put("cornerA", building.getCorners().getA());
        structureData.put("cornerB", building.getCorners().getB());
        structureData.put("rotation", building.getRotation());
        structureData.put("mirror", building.isMirrored());

        List<Object> citizensData = new ArrayList<>();
        for (ICitizenData citizen : building.getAssignedCitizen()) {
            Map<String, Object> citizenData = new HashMap<>();
            citizenData.put("id", citizen.getId());
            citizenData.put("name", citizen.getName());
            citizensData.add(citizenData);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("location", Converter.posToObject(pos));
        map.put("type", building.getSchematicName());
        map.put("style", building.getStyle());
        map.put("level", building.getBuildingLevel());
        map.put("maxLevel", building.getMaxBuildingLevel());
        map.put("name", building.getCustomBuildingName());
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
        if (handler != null)
            return handler.getSlots();
        return 0;
    }

    /**
     * Returns a map with all possible researches
     *
     * @param branch The branch, there are only a few branches
     * @param researches The primary researches of the branch
     * @param colony The colony
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
                if (research == null)
                    continue;
                ILocalResearch colonyResearch = colonyTree.getResearch(branch, researchName);

                List<String> effects = new ArrayList<>();
                for(IResearchEffect<?> researchEffect : research.getEffects())
                    effects.add(researchEffect.getDesc().toString());

                Map<String, Object> map = new HashMap<>();
                map.put("id", researchName.toString());
                map.put("name", research.getName().getString());
                map.put("researchEffects", effects);
                map.put("status", colonyResearch == null ? ResearchState.NOT_STARTED : colonyResearch.getState());

                List<Object> childrenResearch = getResearch(branch, research.getChildren(), colony);
                if (!childrenResearch.isEmpty())
                    map.put("children", childrenResearch);

                result.add(map);
            }
        }
        return result;
    }

    /**
     * Returns the resources(As items) which the builder needs
     *
     * @param colony The colony
     * @param pos The position of the builder's hut block
     * @return a map with all needed resources
     */
    public static Object builderResourcesToObject(IColony colony, BlockPos pos) {
        IBuilding building = colony.getBuildingManager().getBuilding(pos);
        if (!(building instanceof AbstractBuildingStructureBuilder))
            return null;

        AbstractBuildingStructureBuilder builderBuilding = (AbstractBuildingStructureBuilder) building;

        //We need to say the building that we want information about it
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        builderBuilding.serializeToView(buffer);
        buffer.release();

        List<BuildingBuilderResource> resources = new ArrayList<>(builderBuilding.getNeededResources().values());
        resources.sort(new BuildingBuilderResource.ResourceComparator());

        List<Object> result = new ArrayList<>();
        for (BuildingBuilderResource resource : resources) {
            Map<String, Object> map = new HashMap<>();

            map.put("item", resource.getItemStack().copy());
            map.put("displayName", resource.getName());
            map.put("available", resource.getAvailable());
            map.put("delivering", resource.getAmountInDelivery());
            map.put("status", resource.getAvailabilityStatus().toString());
            result.add(map);
        }

        return result;
    }

}