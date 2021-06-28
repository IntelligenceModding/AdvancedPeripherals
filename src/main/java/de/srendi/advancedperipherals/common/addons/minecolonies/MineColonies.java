package de.srendi.advancedperipherals.common.addons.minecolonies;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IVisitorData;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.IBuildingWorker;
import com.minecolonies.api.colony.managers.interfaces.IBuildingManager;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.api.research.IGlobalResearch;
import com.minecolonies.api.research.IGlobalResearchTree;
import com.minecolonies.api.research.ILocalResearch;
import com.minecolonies.api.research.ILocalResearchTree;
import com.minecolonies.api.research.effects.IResearchEffect;
import com.minecolonies.api.research.util.ResearchState;
import com.minecolonies.coremod.colony.buildings.AbstractBuildingStructureBuilder;
import com.minecolonies.coremod.colony.buildings.utils.BuildingBuilderResource;
import de.srendi.advancedperipherals.common.addons.computercraft.base.Converter;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MineColonies {

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
        map.put("isAsleep", citizen.isAsleep());
        map.put("isIdle", citizen.isIdleAtJob());
        citizen.getEntity().ifPresent(entity -> {
            map.put("health", entity.getHealth());
            map.put("maxHealth", entity.getMaxHealth());
            map.put("armor", entity.getAttributeValue(Attributes.ARMOR));
            map.put("toughness", entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        });

        return map;
    }

    public static Object visitorToObject(IVisitorData citizen) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", citizen.getId());
        map.put("name", citizen.getName());
        map.put("location", Converter.posToObject(citizen.getSittingPosition()));
        map.put("age", citizen.isChild() ? "child" : "adult");
        map.put("gender", citizen.isFemale() ? "female" : "male");
        map.put("saturation", citizen.getSaturation());
        map.put("happiness", citizen.getCitizenHappinessHandler().getHappiness(citizen.getColony()));
        map.put("skills", skillsToObject(citizen.getCitizenSkillHandler().getSkills()));
        map.put("recruitCost", citizen.getRecruitCost().getItem().getRegistryName().toString());
        return map;
    }

    public static Object jobToObject(IBuildingWorker work) {
        Map<String, Object> map = new HashMap<>();
        map.put("location", Converter.posToObject(work.getLocation().getInDimensionLocation()));
        map.put("type", work.getSchematicName());
        map.put("level", work.getBuildingLevel());
        map.put("name", work.getJobName());
        return map;
    }

    public static Object homeToObject(IBuilding home) {
        Map<String, Object> map = new HashMap<>();
        map.put("location", Converter.posToObject(home.getLocation().getInDimensionLocation()));
        map.put("type", home.getSchematicName());
        map.put("level", home.getBuildingLevel());
        return map;
    }

    public static Object skillsToObject(Map<Skill, Tuple<Integer, Double>> skills) {
        Map<Object, Object> map = new HashMap<>();
        for (Map.Entry<Skill, Tuple<Integer, Double>> entry :
                skills.entrySet()) {
            Map<Object, Object> skillData = new HashMap<>();
            skillData.put("level", entry.getValue().getA());
            skillData.put("xp", entry.getValue().getB());
            map.put(entry.getKey().name(), skillData);
        }
        return map;
    }

    public static Object buildingToObject(IBuildingManager buildingManager, IBuilding building, BlockPos pos) {
        Map<Object, Object> footprintData = new HashMap<>();
        footprintData.put("corner1", building.getCorners().getA());
        footprintData.put("corner2", building.getCorners().getB());
        footprintData.put("rotation", building.getRotation());
        footprintData.put("mirror", building.isMirrored());

        List<Object> citizensData = new ArrayList<>();
        for (ICitizenData citizen : building.getAssignedCitizen()) {
            Map<Object, Object> citizenData = new HashMap<>();
            citizenData.put("id", citizen.getId());
            citizenData.put("name", citizen.getName());
            citizensData.add(citizenData);
        }
        for (int i = citizensData.size(); i < building.getMaxInhabitants(); ++i) {
            citizensData.add(new HashMap<>());
        }

        Map<Object, Object> map = new HashMap<>();
        map.put("location", Converter.posToObject(pos));
        map.put("type", building.getSchematicName());
        map.put("style", building.getStyle());
        map.put("level", building.getBuildingLevel());
        map.put("maxLevel", building.getMaxBuildingLevel());
        map.put("name", building.getCustomBuildingName());
        map.put("built", building.isBuilt());
        map.put("wip", building.hasWorkOrder());
        map.put("priority", building.getPickUpPriority());
        map.put("footprint", footprintData);
        map.put("citizens", citizensData);
        map.put("storageBlocks", building.getContainers().size());
        map.put("storageSlots", getStorageSize(building));
        map.put("guarded", buildingManager.hasGuardBuildingNear(building));
        return map;
    }

    public static int getStorageSize(IBuilding building) {
        LazyOptional<IItemHandler> capability = building.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        IItemHandler handler = capability.resolve().orElse(null);
        if (handler != null) {
            return handler.getSlots();
        }
        return 0;
    }

    public static List<Object> getResearch(ResourceLocation branch, List<ResourceLocation> names, IGlobalResearchTree tree, ILocalResearchTree colonyTree) {
        List<Object> result = new ArrayList<>();
        if (names != null) {
            for (ResourceLocation name : names) {
                IGlobalResearch research = tree.getResearch(branch, name);
                if (research == null) continue;
                ILocalResearch colonyResearch = colonyTree.getResearch(branch, name);

                List<String> effects = research.getEffects().stream()
                        .map(IResearchEffect::getDesc).map(TranslationTextComponent::getString).collect(Collectors.toList());

                Map<Object, Object> map = new HashMap<>();
                map.put("id", name.toString());
                map.put("name", research.getName().getString());
                map.put("effects", effects);
                map.put("status", (colonyResearch == null ? ResearchState.NOT_STARTED : colonyResearch.getState()).toString());

                List<Object> children = getResearch(branch, research.getChildren(), tree, colonyTree);
                if (!children.isEmpty()) {
                    map.put("children", children);
                }
                result.add(map);
            }
        }
        return result;
    }

    public static Object getBuilderResources(IColony colony, BlockPos pos) {
        IBuilding building = colony.getBuildingManager().getBuilding(pos);
        if (!(building instanceof AbstractBuildingStructureBuilder))
            return null;

        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        building.serializeToView(buffer);
        buffer.release();

        final List<BuildingBuilderResource> resources = new ArrayList<>(((AbstractBuildingStructureBuilder) building).getNeededResources().values());
        resources.sort(new BuildingBuilderResource.ResourceComparator());

        List<Object> result = new ArrayList<>();
        for (BuildingBuilderResource resource : resources) {
            Map<Object, Object> map = new HashMap<>();
            BuildingBuilderResource resourceCopy = new BuildingBuilderResource(resource.getItemStack(), resource.getAmount(), resource.getAvailable());
            resourceCopy.setAmountInDelivery(resource.getAmountInDelivery());

            ItemStack stack = resourceCopy.getItemStack().copy();
            stack.setCount(resourceCopy.getAmount());
            map.put("item", stack.getItem().getRegistryName().toString());
            map.put("available", resourceCopy.getAvailable());
            map.put("delivering", resourceCopy.getAmountInDelivery());
            map.put("status", resourceCopy.getAvailabilityStatus().toString());
            result.add(map);
        }

        return result;
    }

}