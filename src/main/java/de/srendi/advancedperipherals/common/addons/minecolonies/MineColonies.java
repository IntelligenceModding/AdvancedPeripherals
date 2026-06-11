package de.srendi.advancedperipherals.common.addons.minecolonies;

import com.google.common.collect.ImmutableCollection;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IVisitorData;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.jobs.IJob;
import com.minecolonies.api.colony.managers.interfaces.IRegisteredStructureManager;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.colony.requestsystem.request.IRequest;
import com.minecolonies.api.colony.requestsystem.requestable.deliveryman.Delivery;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.colony.workorders.IWorkOrder;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.api.entity.citizen.VisibleCitizenStatus;
import com.minecolonies.api.research.IGlobalResearch;
import com.minecolonies.api.research.IGlobalResearchTree;
import com.minecolonies.api.research.ILocalResearch;
import com.minecolonies.api.research.ILocalResearchTree;
import com.minecolonies.api.research.IResearchEffect;
import com.minecolonies.api.research.IResearchRequirement;
import com.minecolonies.api.research.requirements.BuildingAlternatesResearchRequirement;
import com.minecolonies.api.research.requirements.BuildingResearchRequirement;
import com.minecolonies.api.research.requirements.ResearchResearchRequirement;
import com.minecolonies.api.research.util.ResearchState;
import com.minecolonies.core.colony.buildings.AbstractBuildingStructureBuilder;
import com.minecolonies.core.colony.buildings.utils.BuildingBuilderResource;
import com.minecolonies.core.datalistener.model.Disease;
import com.minecolonies.core.entity.ai.workers.util.BuildingProgressStage;
import com.minecolonies.core.entity.citizen.citizenhandlers.CitizenSkillHandler;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

    private static Map<String, Object> commonCitizenToLua(ICitizenData citizen) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", citizen.getId());
        map.put("uuid", citizen.getUUID().toString());
        map.put("name", citizen.getName());
        map.put("isChild", citizen.isChild());
        map.put("gender", citizen.isFemale() ? "female" : "male");
        map.put("saturation", citizen.getSaturation());
        map.put("happiness", citizen.getCitizenHappinessHandler().getHappiness(citizen.getColony(), citizen));
        map.put("skills", skillsToLua(citizen.getCitizenSkillHandler().getSkills()));
        Disease disease = citizen.getCitizenDiseaseHandler().getDisease();
        map.put("disease", disease == null ? null : disease.id().toString());
        return map;
    }

    /**
     * Converts a citizen to a map
     *
     * @param citizen the citizen
     * @return a map with information about the citizen
     */
    public static Map<String, Object> citizenToLua(ICitizenData citizen) {
        Map<String, Object> map = commonCitizenToLua(citizen);
        map.put("bedPos", LuaConverter.posToLua(citizen.getBedPos()));
        map.put("pos", LuaConverter.posToLua(citizen.getLastPosition()));
        map.put("homeBuilding", citizen.getHomeBuilding() == null ? null : buildingToLua(citizen.getHomeBuilding()));
        map.put("workBuilding", citizen.getWorkBuilding() == null ? null : jobToLua(citizen.getJob()));
        map.put("jobStatus", citizen.getJobStatus().name());
        VisibleCitizenStatus status = citizen.getStatus();
        map.put("state", status == null ? "Idle" : Component.translatable(status.getTranslationKey()).getString());
        map.put("isIdle", status == null || status == VisibleCitizenStatus.HOUSE);
        map.put("isAsleep", citizen.isAsleep());
        map.put("isMourning", citizen.getCitizenMournHandler().isMourning());
        map.put("needsBetterFood", citizen.needsBetterFood());
        ICitizenData partner = citizen.getPartner();
        map.put("partner", partner == null ? null : partner.getId());
        map.put("children", citizen.getChildren());
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
    public static Map<String, Object> visitorToLua(IVisitorData visitor) {
        Map<String, Object> map = commonCitizenToLua(visitor);
        map.put("pos", LuaConverter.posToLua(visitor.getSittingPosition()));
        map.put("recruitCost", LuaConverter.itemStackToLua(visitor.getRecruitCost()));
        return map;
    }

    /**
     * Converts a skill {@link Skill} into a map
     *
     * @param skills skills as list. Can be obtained via {@link ICitizenData#getCitizenSkillHandler}
     * @return a map with information about the skill
     */
    public static Map<String, Object> skillsToLua(Map<Skill, CitizenSkillHandler.SkillData> skills) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<Skill, CitizenSkillHandler.SkillData> entry : skills.entrySet()) {
            CitizenSkillHandler.SkillData data = entry.getValue();
            map.put(
                entry.getKey().name(),
                Map.of(
                    "level", data.getLevel(),
                    "xp", data.getExperience()
                )
            );
        }
        return map;
    }

    /**
     * Converts a building {@link IBuilding} and job {@link IJob} to a map
     *
     * @param job  the job
     * @return a map with information about the building and job
     */
    public static Map<String, Object> jobToLua(IJob<?> job) {
        Map<String, Object> map = buildingToLua(job.getWorkBuilding());
        map.put("job", job.getJobRegistryEntry().getKey().toString());
        return map;
    }

    /**
     * Converts a building {@link IBuilding} to a map
     *
     * @param building the building
     * @return a map with information about the building
     */
    public static Map<String, Object> buildingToLua(IBuilding building) {
        Map<String, Object> map = new HashMap<>();
        map.put("pos", LuaConverter.posToLua(building.getLocation().getInDimensionLocation()));
        map.put("type", building.getSchematicName());
        map.put("level", building.getBuildingLevel());
        map.put("displayName", building.getBuildingDisplayName());
        return map;
    }

    /**
     * Returns information about the building like structure data, the citizens and some other values
     *
     * @param building        The building as instance
     * @param buildingManager The building manager of the colony
     * @return information about the building
     */
    public static Map<String, Object> buildingToLua(IBuilding building, IRegisteredStructureManager buildingManager) {
        Map<String, Object> map = buildingToLua(building);
        map.put("style", building.getStructurePack());
        map.put("maxLevel", building.getMaxBuildingLevel());
        map.put("built", building.isBuilt());
        map.put("isWorking", building.isPendingConstruction());
        map.put("priority", building.getPickUpPriority());
        map.put(
            "structure",
            Map.of(
                "cornerA", LuaConverter.posToLua(building.getCorners().getA()),
                "cornerB", LuaConverter.posToLua(building.getCorners().getB()),
                "rotation", building.getRotationMirror().rotation().getSerializedName(),
                "isMirrored", building.getRotationMirror().isMirrored()
            )
        );
        map.put(
            "citizens",
            building.getAllAssignedCitizen().stream()
                .map(citizen -> Map.of(
                    "id", citizen.getId(),
                    "name", citizen.getName()
                ))
                .toList()
        );
        map.put("storageBlocks", building.getContainers().size());
        map.put("storageSlots", getStorageSlots(building));
        map.put("guarded", buildingManager.hasGuardBuildingNear(building));
        return map;
    }

    /**
     * Returns the size of all inventories in this building
     *
     * @param building the proper building with racks(Or other inventories)
     * @return the size of all inventories in this building
     */
    public static int getStorageSlots(IBuilding building) {
        int size = 0;
        for (IItemHandler itemHandler : building.getHandlers()) {
            size += itemHandler.getSlots();
        }
        return size;
    }

    public static int getAmountOfConstructionSites(IColony colony) {
        int constructionSites = 0;
        for (IBuilding building : colony.getServerBuildingManager().getBuildings().values()) {
            if (building.isPendingConstruction()) {
                constructionSites++;
            }
        }
        return constructionSites;
    }

    public static Map<String, Object> workOrderToLua(IWorkOrder workOrder, IRegisteredStructureManager buildingManager) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", workOrder.getID());
        map.put("pos", LuaConverter.posToLua(workOrder.getLocation()));
        map.put("type", workOrder.getWorkOrderType().name());
        map.put("displayName", workOrder.getDisplayName().getString());
        map.put("priority", workOrder.getPriority());
        map.put("level", workOrder.getCurrentLevel());
        map.put("targetLevel", workOrder.getTargetLevel());
        BuildingProgressStage stage = workOrder.getStage();
        map.put("stage", stage == null ? null : stage.name());
        map.put("isClaimed", workOrder.isClaimed());
        if (workOrder.isClaimed()) {
            map.put("builderHome", buildingToLua(buildingManager.getBuilding(workOrder.getClaimedBy())));
        }
        return map;
    }

    /**
     * Returns a list with all possible researches
     *
     * @param branch     The branch, there are only a few branches
     * @param researches The primary researches of the branch
     * @param colony     The colony
     * @return a list including maps with all possible researches
     */
    public static List<Map<String, Object>> getResearches(ResourceLocation branch, List<ResourceLocation> researches, IColony colony) throws CommandSyntaxException {
        if (researches == null) {
            return List.of();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (final ResourceLocation researchName : researches) {
            //All global possible researches
            IGlobalResearchTree globalTree = IGlobalResearchTree.getInstance();
            //The research tree of the colony
            ILocalResearchTree colonyTree = colony.getResearchManager().getResearchTree();

            IGlobalResearch research = globalTree.getResearch(branch, researchName);
            if (research == null || research.isHidden()) {
                continue;
            }
            ILocalResearch colonyResearch = colonyTree.getResearch(branch, researchName);

            List<Map<String, Object>> cost = research.getCostList().stream()
                .map(ingredient -> Map.of(
                    "validItems", Stream.of(ingredient.getItems()).map(LuaConverter::itemStackToLua).toList(),
                    "count", ingredient.count()
                ))
                .toList();

            List<Map<String, Object>> requirements = new ArrayList<>();
            for (IResearchRequirement requirement : research.getResearchRequirements()) {
                Map<String, Object> requirementItem = new HashMap<>();
                requirementItem.put("type", requirement.getRegistryEntry().getRegistryName().toString());
                requirementItem.put("desc", requirement.getDesc().getString());
                requirementItem.put("fulfilled", requirement.isFulfilled(colony));
                if (requirement instanceof BuildingResearchRequirement buildingRequirement) {
                    requirementItem.put("building", buildingRequirement.getBuilding().toString());
                    requirementItem.put("level", buildingRequirement.getBuildingLevel());
                } else if (requirement instanceof BuildingAlternatesResearchRequirement buildingAltsRequirement) {
                    requirementItem.put("buildings", buildingAltsRequirement.getBuildings().stream().map(ResourceLocation::toString).toList());
                    requirementItem.put("level", buildingAltsRequirement.getBuildingLevel());
                } else if (requirement instanceof ResearchResearchRequirement researchRequirement) {
                    requirementItem.put("researchId", researchRequirement.getResearchId());
                }
                requirements.add(requirementItem);
            }

            Map<String, Object> map = new HashMap<>();
            map.put("id", researchName.toString());
            map.put("displayName", MutableComponent.create(research.getName()).getString());
            map.put("requirements", requirements);
            map.put("cost", cost);
            map.put("researchEffects", research.getEffects().stream().map(IResearchEffect::getId).map(ResourceLocation::toString).toList());
            map.put("status", colonyResearch == null ? ResearchState.NOT_STARTED.name() : colonyResearch.getState().name());
            map.put(
                "requiredTime",
                colonyResearch == null
                    ? 0
                    : IGlobalResearchTree.getInstance().getBranchData(colonyResearch.getBranch()).getBaseTime(colonyResearch.getDepth())
            );
            map.put("progress", colonyResearch == null ? 0 : colonyResearch.getProgress());

            List<Map<String, Object>> childrenResearchs = getResearches(branch, research.getChildren(), colony);
            if (!childrenResearchs.isEmpty()) {
                map.put("children", childrenResearchs);
            }

            result.add(map);
        }
        return result;
    }

    /**
     * Returns the resource items which the builder needs
     *
     * @param colony The colony
     * @param building The builder's building
     * @return a map with all needed resources
     */
    public static List<Map<String, Object>> builderResourcesToLua(IColony colony, IBuilding building) {
        if (!(building instanceof AbstractBuildingStructureBuilder builderBuilding)) {
            return null;
        }

        return builderBuilding.getNeededResources().values().stream()
            .sorted(new BuildingBuilderResource.ResourceComparator())
            .map(resource -> {
                ItemStack stack = resource.getItemStack();
                Map<String, Object> map = new HashMap<>();
                map.put("item", LuaConverter.itemStackToLua(stack));
                map.put("displayName", resource.getName());
                map.put("status", resource.getAvailabilityStatus().name());
                map.put("needs", resource.getAmount());
                map.put("available", resource.getAvailable());
                map.put("delivering", resource.getAmountInDelivery());
                return map;
            })
            .toList();
    }

    /**
     * Stolen from minecolonies codebase to get delivery requests.
     * <p>
     * See {@link com.minecolonies.core.client.gui.WindowResourceList#addDeliveryRequestsToList(List, ImmutableCollection)}}
     */
    private static void addDeliveryRequestsToList(IBuilding building, List<Delivery> requestList, ImmutableCollection<IToken<?>> tokens) {
        for (final IToken<?> token : tokens) {
            final IRequest<?> request = building.getColony().getRequestManager().getRequestForToken(token);
            if (request == null) {
                continue;
            }
            if (request.getRequest() instanceof Delivery delivery && delivery.getTarget().getInDimensionLocation().equals(building.getID())) {
                requestList.add(delivery);
            }
            if (request.hasChildren()) {
                addDeliveryRequestsToList(building, requestList, request.getChildren());
            }
        }
    }
}
