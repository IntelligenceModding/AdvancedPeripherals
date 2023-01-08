package de.srendi.advancedperipherals.common.data;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStatesAndModelsProvider extends BlockStateProvider {

    public BlockStatesAndModelsProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, AdvancedPeripherals.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        peripheralBlock(Blocks.ENVIRONMENT_DETECTOR.get(), "front");
        peripheralBlock(Blocks.CHAT_BOX.get(), "front");
        peripheralBlock(Blocks.PLAYER_DETECTOR.get(), "side", "front");
        peripheralBlock(Blocks.ME_BRIDGE.get(), "front");
        peripheralBlock(Blocks.RS_BRIDGE.get(), "front");
        peripheralBlock(Blocks.ENERGY_DETECTOR.get(), "front", "back");
        peripheralBlock(Blocks.PERIPHERAL_CASING.get());
        peripheralBlock(Blocks.AR_CONTROLLER.get(), "front");
        peripheralBlock(Blocks.INVENTORY_MANAGER.get(), "front");
        peripheralBlock(Blocks.REDSTONE_INTEGRATOR.get(), "front");
        peripheralBlock(Blocks.BLOCK_READER.get(), generateModel(Blocks.BLOCK_READER.get(), false, "north", "south", "east", "west", "up", "down"));
        peripheralBlock(Blocks.GEO_SCANNER.get(), "front");
        peripheralBlock(Blocks.COLONY_INTEGRATOR.get(), generateModel(Blocks.COLONY_INTEGRATOR.get())
                .texture("up", blockTexture(net.minecraft.world.level.block.Blocks.OAK_LOG, "top"))
                .texture("down", blockTexture(net.minecraft.world.level.block.Blocks.OAK_LOG, "top")));
        peripheralBlock(Blocks.NBT_STORAGE.get(), "front");
    }

    private void peripheralBlock(Block block, ModelFile file) {
        getVariantBuilder(block).forAllStates(state -> {
            ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(file);
            FrontAndTop orientation = state.getValue(BaseBlock.ORIENTATION);
            int x = 0, y;
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
            if (side.equals("front")) side = "north";
            if (side.equals("back")) side = "south";
            builder.texture(side, blockTexture(block, sideTexture));
        }
        return builder;
    }

    private BlockModelBuilder generateModel(Block block) {
        return models().cubeAll(name(block), blockTexture(block));
    }

    private ResourceLocation blockTexture(Block block, String offset) {
        ResourceLocation name = block.getRegistryName();
        return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath() + "_" + offset);
    }

    private String name(Block block) {
        return block.getRegistryName().getPath();
    }

}
