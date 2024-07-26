package de.srendi.advancedperipherals.common.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.peripheral.monitor.MonitorEdgeState;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.monitor.UltimateBlockMonitor;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.data.models.model.ModelLocationUtils.getModelLocation;
import static net.minecraft.data.models.model.TextureMapping.getBlockTexture;

public class BlockStatesAndModelsProvider extends BlockStateProvider {
    private final DataGenerator generator;
    private Consumer<BlockStateGenerator> addBlockState = null;
    private BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput = null;

    public BlockStatesAndModelsProvider(DataGenerator packOutput, ExistingFileHelper exFileHelper) {
        super(packOutput, AdvancedPeripherals.MOD_ID, exFileHelper);
        this.generator = packOutput;
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
        peripheralBlock(APBlocks.REDSTONE_INTEGRATOR.get(), generateModel(APBlocks.REDSTONE_INTEGRATOR.get(), false, "side", "front", "top", "bottom"));
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
                y = (int) (orientation.top().toYRot() + 180) % 360;
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

            builder.texture(side, blockTexture(block, sideTexture));
        }

        // Add default bottom texture if not specified
        if (!Arrays.asList(sides).contains("down") && !Arrays.asList(sides).contains("bottom")) {
            builder.texture("down", AdvancedPeripherals.getRL(ModelProvider.BLOCK_FOLDER + "/" + "bottom"));
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
        return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath() + "_" + offset);
    }

    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    private String name(Block block) {
        return key(block).getPath();
    }

    @Override
    public void run(CachedOutput cache) throws IOException {
        doMonitors(this.generator, cache);
        super.run(cache);
    }

    //// from dan200.computercraft.data.BlockModelGenerator ////

    private static final ModelTemplate MONITOR_BASE = new ModelTemplate(
        Optional.of( new ResourceLocation( AdvancedPeripherals.MOD_ID, "block/monitor_base" ) ),
        Optional.empty(),
        TextureSlot.FRONT, TextureSlot.SIDE, TextureSlot.TOP, TextureSlot.BACK
    );

    private void doMonitors(DataGenerator generator, CachedOutput output) {
        DataGenerator.PathProvider blockStatePath = generator.createPathProvider( DataGenerator.Target.RESOURCE_PACK, "blockstates" );
        DataGenerator.PathProvider modelPath = generator.createPathProvider( DataGenerator.Target.RESOURCE_PACK, "models" );
        Map<Block, BlockStateGenerator> blockStates = new HashMap<>();
        this.addBlockState = gen -> {
            Block block = gen.getBlock();
            if( blockStates.containsKey( block ) )
            {
                throw new IllegalStateException( "Duplicate blockstate definition for " + block );
            }
            blockStates.put( block, gen );
        };

        Map<ResourceLocation, Supplier<JsonElement>> models = new HashMap<>();
        this.modelOutput = (id, contents) -> {
            if(models.containsKey(id)) {
                throw new IllegalStateException("Duplicate model definition for " + id);
            }
            models.put(id, contents);
        };
        Set<Item> explicitItems = new HashSet<>();
        BlockModelGenerators blockGen = new BlockModelGenerators(addBlockState, modelOutput, explicitItems::add);

        registerMonitor(blockGen, (UltimateBlockMonitor) APBlocks.ULTIMATE_MONITOR.get());

        for( Block block : ForgeRegistries.BLOCKS )
        {
            if( !blockStates.containsKey( block ) ) continue;

            Item item = Item.BY_BLOCK.get( block );
            if( item == null || explicitItems.contains( item ) ) continue;

            ResourceLocation model = ModelLocationUtils.getModelLocation( item );
            if( !models.containsKey( model ) )
            {
                models.put( model, new DelegatedModel( ModelLocationUtils.getModelLocation( block ) ) );
            }
        }

        saveCollection( output, blockStates, x -> blockStatePath.json( ForgeRegistries.BLOCKS.getKey( x ) ) );
        saveCollection( output, models, modelPath::json );
    }

    private static <T> void saveCollection( CachedOutput output, Map<T, ? extends Supplier<JsonElement>> items, Function<T, Path> getLocation )
    {
        for( Map.Entry<T, ? extends Supplier<JsonElement>> entry : items.entrySet() )
        {
            Path path = getLocation.apply( entry.getKey() );
            try
            {
                DataProvider.saveStable( output, entry.getValue().get(), path );
            }
            catch( Exception exception )
            {
                ComputerCraft.log.error( "Couldn't save {}", path, exception );
            }
        }
    }

    private void registerMonitor(BlockModelGenerators generators, UltimateBlockMonitor block)
    {
        monitorModel( generators, block, "", 16, 4, 0, 32 );
        monitorModel( generators, block, "_d", 20, 7, 0, 36 );
        monitorModel( generators, block, "_l", 19, 4, 1, 33 );
        monitorModel( generators, block, "_ld", 31, 7, 1, 45 );
        monitorModel( generators, block, "_lr", 18, 4, 2, 34 );
        monitorModel( generators, block, "_lrd", 30, 7, 2, 46 );
        monitorModel( generators, block, "_lru", 24, 5, 2, 40 );
        monitorModel( generators, block, "_lrud", 27, 6, 2, 43 );
        monitorModel( generators, block, "_lu", 25, 5, 1, 39 );
        monitorModel( generators, block, "_lud", 28, 6, 1, 42 );
        monitorModel( generators, block, "_r", 17, 4, 3, 35 );
        monitorModel( generators, block, "_rd", 29, 7, 3, 47 );
        monitorModel( generators, block, "_ru", 23, 5, 3, 41 );
        monitorModel( generators, block, "_rud", 26, 6, 3, 44 );
        monitorModel( generators, block, "_u", 22, 5, 0, 38 );
        monitorModel( generators, block, "_ud", 21, 6, 0, 37 );

        addBlockState.accept( MultiVariantGenerator.multiVariant( block )
            .with( createHorizontalFacingDispatch() )
            .with( createVerticalFacingDispatch( UltimateBlockMonitor.ORIENTATION ) )
            .with( createModelDispatch( UltimateBlockMonitor.STATE, edge -> getModelLocation( block, edge == MonitorEdgeState.NONE ? "" : "_" + edge.getSerializedName() ) ) )
        );
        modelOutput.accept( ModelLocationUtils.getModelLocation(block.asItem()), new DelegatedModel(monitorModel( generators, block, "_item", 15, 4, 0, 32 ) ));
    }

    private static final ResourceLocation TRANSPARENT_TEXTURE = new ResourceLocation(AdvancedPeripherals.MOD_ID, "block/transparent");

    private ResourceLocation monitorModel( BlockModelGenerators generators, UltimateBlockMonitor block, String corners, int front, int side, int top, int back )
    {
        TextureMapping textureMap = new TextureMapping();
        textureMap.put(TextureSlot.FRONT, getBlockTexture(block, "_" + front));
        // textureMap.put(TextureSlot.SIDE, getBlockTexture(block, "_" + side));
        // textureMap.put(TextureSlot.TOP, getBlockTexture(block, "_" + top));
        // textureMap.put(TextureSlot.BACK, getBlockTexture(block, "_" + back));
        textureMap.put(TextureSlot.SIDE, TRANSPARENT_TEXTURE);
        textureMap.put(TextureSlot.TOP, TRANSPARENT_TEXTURE);
        textureMap.put(TextureSlot.BACK, TRANSPARENT_TEXTURE);
        return MONITOR_BASE.create(
            getModelLocation( block, corners ),
            textureMap,
            modelOutput
        );
    }

    private static PropertyDispatch createHorizontalFacingDispatch()
    {
        var dispatch = PropertyDispatch.property( BlockStateProperties.HORIZONTAL_FACING );
        for( Direction direction : BlockStateProperties.HORIZONTAL_FACING.getPossibleValues() )
        {
            dispatch.select( direction, Variant.variant().with( VariantProperties.Y_ROT, toYAngle( direction ) ) );
        }
        return dispatch;
    }

    private static PropertyDispatch createVerticalFacingDispatch( Property<Direction> property )
    {
        var dispatch = PropertyDispatch.property( property );
        for( Direction direction : property.getPossibleValues() )
        {
            dispatch.select( direction, Variant.variant().with( VariantProperties.X_ROT, toXAngle( direction ) ) );
        }
        return dispatch;
    }

    private static <T extends Comparable<T>> PropertyDispatch createModelDispatch( Property<T> property, Function<T, ResourceLocation> makeModel )
    {
        var variant = PropertyDispatch.property( property );
        for( T value : property.getPossibleValues() )
        {
            variant.select( value, Variant.variant().with( VariantProperties.MODEL, makeModel.apply( value ) ) );
        }
        return variant;
    }

    private static VariantProperties.Rotation toXAngle( Direction direction )
    {
        switch( direction )
        {
            default:
                return VariantProperties.Rotation.R0;
            case UP:
                return VariantProperties.Rotation.R270;
            case DOWN:
                return VariantProperties.Rotation.R90;
        }
    }

    private static VariantProperties.Rotation toYAngle( Direction direction )
    {
        switch( direction )
        {
            default:
                return VariantProperties.Rotation.R0;
            case NORTH:
                return VariantProperties.Rotation.R0;
            case SOUTH:
                return VariantProperties.Rotation.R180;
            case EAST:
                return VariantProperties.Rotation.R90;
            case WEST:
                return VariantProperties.Rotation.R270;
        }
    }
}
