package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockStatesAndModelsProvider extends BlockStateProvider {

    public BlockStatesAndModelsProvider(DataGenerator packOutput, ExistingFileHelper exFileHelper) {
        super(packOutput, AdvancedPeripherals.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        peripheralBlock(APBlocks.ENVIRONMENT_DETECTOR.get(), "front");
        peripheralBlock(APBlocks.CHAT_BOX.get(), "front");
        peripheralBlock(APBlocks.PLAYER_DETECTOR.get(), "side", "front");
        peripheralBlock(APBlocks.ME_BRIDGE.get(), "front");
        peripheralBlock(APBlocks.RS_BRIDGE.get(), "front");
        peripheralBlock(APBlocks.ENERGY_DETECTOR.get(), "front", "back");
        peripheralBlock(APBlocks.PERIPHERAL_CASING.get());
        peripheralBlock(APBlocks.INVENTORY_MANAGER.get(), "front");
        peripheralBlock(APBlocks.REDSTONE_INTEGRATOR.get(), "front");
        peripheralBlock(APBlocks.BLOCK_READER.get(), generateModel(APBlocks.BLOCK_READER.get(), false, "north", "south", "east", "west", "up", "down"));
        peripheralBlock(APBlocks.GEO_SCANNER.get(), "front");
        peripheralBlock(APBlocks.COLONY_INTEGRATOR.get(), generateModel(APBlocks.COLONY_INTEGRATOR.get())
                .texture("particle", blockTexture(APBlocks.COLONY_INTEGRATOR.get()))
                .texture("up", blockTexture(net.minecraft.world.level.block.Blocks.OAK_LOG, "top"))
                .texture("down", blockTexture(net.minecraft.world.level.block.Blocks.OAK_LOG, "top")));
        peripheralBlock(APBlocks.NBT_STORAGE.get(), "front");
    }

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
                y = (int) (orientation.top().toYRot() + 180) % 360;
            }
            builder.rotationX(x);
            builder.rotationY(y);
            return builder.build();
        });
    }

    private void peripheralBlock(Block block, String... sides) {
        peripheralBlock(block, generateModel(block, true, sides));
    }

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
            if (side.equals("side")) {
                for (Direction direction : Direction.Plane.HORIZONTAL)
                    builder.texture(direction.toString(), blockTexture(block, sideTexture));
            }
            if (side.equals("north"))
                particleTexture = blockTexture(block, "north");

            if (side.equals("front")) {
                side = "north";
                particleTexture = blockTexture(block, "front");
            }
            if (side.equals("back")) side = "south";
            builder.texture(side, blockTexture(block, sideTexture));


        }
        builder.texture("particle", particleTexture);
        return builder;
    }

    private BlockModelBuilder generateModel(Block block) {
        return models().cubeAll(name(block), blockTexture(block));
    }

    private ResourceLocation blockTexture(Block block, String offset) {
        ResourceLocation name = key(block);
        return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath() + "_" + offset);
    }

    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    private String name(Block block) {
        return key(block).getPath();
    }

}
