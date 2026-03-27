package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Arrays;

public class BlockStatesAndModelsProvider extends BlockStateProvider {

    public BlockStatesAndModelsProvider(PackOutput packOutput, ExistingFileHelper exFileHelper) {
        super(packOutput, AdvancedPeripherals.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Define blocks with specific sides and orientations
        peripheralBlock(APBlocks.ENVIRONMENT_DETECTOR.get(), "front", "top");
        peripheralBlock(APBlocks.CHAT_BOX.get(), "front", "top");
        peripheralBlock(APBlocks.ME_BRIDGE.get(), "front", "top");
        peripheralBlock(APBlocks.RS_BRIDGE.get(), "front", "top");
        peripheralBlock(APBlocks.ENERGY_DETECTOR.get(), "front", "back", "top", "east");
        peripheralBlock(APBlocks.FLUID_DETECTOR.get(), "front", "back", "top", "east");
        peripheralBlock(APBlocks.GAS_DETECTOR.get(), "front", "back", "top", "east");
        peripheralBlock(APBlocks.INVENTORY_MANAGER.get(), "front", "top");
        peripheralBlock(APBlocks.GEO_SCANNER.get(), "front", "top");
        peripheralBlock(APBlocks.COLONY_INTEGRATOR.get(), "front", "top");
        peripheralBlock(APBlocks.NBT_STORAGE.get(), "front", "top");

        // Define blocks with custom model generation
        peripheralBlock(APBlocks.PLAYER_DETECTOR.get(), generateModel(APBlocks.PLAYER_DETECTOR.get(), false, "side", "front", "top"));
        // peripheralBlock(APBlocks.REDSTONE_INTEGRATOR.get(), generateModel(APBlocks.REDSTONE_INTEGRATOR.get(), false, "side", "front", "top", "bottom"));
        peripheralBlock(APBlocks.BLOCK_READER.get(), generateModel(APBlocks.BLOCK_READER.get(), false, "north", "south", "east", "west", "up", "down"));
        peripheralBlock(APBlocks.DISTANCE_DETECTOR.get(), generateModel(APBlocks.DISTANCE_DETECTOR.get(), false, "north", "south", "east", "west", "up", "down"));

        // Define a simple block with all sides having the same texture
        peripheralBlock(APBlocks.PERIPHERAL_CASING.get());
    }

    // Helper method to register block states and models with specific sides
    private void peripheralBlock(Block block, String... sides) {
        peripheralBlock(block, generateModel(block, true, sides));
    }

    // Helper method to register block states and models with a pre-generated model
    private void peripheralBlock(Block block, ModelFile file) {
        getVariantBuilder(block).forAllStates(state -> {
            ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(file);
            FrontAndTop orientation = state.getValue(BaseBlock.ORIENTATION);
            int x = 0;
            int y;
            if (orientation.top().getAxis() == Direction.Axis.Y) {
                y = (int) (orientation.front().toYRot() + 180) % 360;
            } else {
                x = orientation.front() == Direction.DOWN ? 90 : 270;
                y = (int) orientation.top().toYRot();
            }
            builder.rotationX(x);
            builder.rotationY(y);
            return builder.build();
        });
    }

    // Helper method to generate a block model with specified sides and textures
    private BlockModelBuilder generateModel(Block block, boolean hasNormalSide, String... sides) {
        ResourceLocation particleTexture = blockTexture(block);
        BlockModelBuilder builder;
        if (hasNormalSide) {
            builder = generateModel(block);
        } else {
            builder = models().withExistingParent(name(block), mcLoc("block/cube_all"));
        }

        for (String sideTexture : sides) {
            String side = sideTexture;

            switch (side) {
                case "side":
                    for (Direction direction : Direction.Plane.HORIZONTAL) {
                        builder.texture(direction.toString(), blockTexture(block, sideTexture));
                    }
                    break;
                case "north":
                    particleTexture = blockTexture(block, side);
                    break;
                case "front":
                    particleTexture = blockTexture(block, side);
                    side = "north";
                    break;
                case "top":
                    side = "up";
                    break;
                case "bottom":
                    side = "down";
                    break;
                case "back":
                    side = "south";
                    break;
                default:
                    break;
            }

            if (side.equals("top")) side = "up";
            if (side.equals("bottom")) side = "down";
            if (side.equals("back")) side = "south";
            builder.texture(side, blockTexture(block, sideTexture));
        }

        // In the case that there is no bottom side defined, we use the default bottom texture.
        if (Arrays.stream(sides).noneMatch(side -> side.equals("bottom"))) {
            builder.texture("down", ModelProvider.BLOCK_FOLDER + "/" + "bottom");
        }
        builder.texture("particle", particleTexture);
        return builder;
    }

    // Helper method to generate a simple block model
    private BlockModelBuilder generateModel(Block block) {
        return models().cubeAll(name(block), blockTexture(block));
    }

    // Helper methods for generating resource locations and block names
    private ResourceLocation blockTexture(Block block, String offset) {
        ResourceLocation name = key(block);
        return ResourceLocation.fromNamespaceAndPath(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath() + "_" + offset);
    }

    private ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private String name(Block block) {
        return key(block).getPath();
    }
}
