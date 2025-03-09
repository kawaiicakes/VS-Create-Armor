package io.github.kawaiicakes;

import io.github.kawaiicakes.block.VerticalSlabBlock;
import io.github.kawaiicakes.block.VerticalStairsBlock;
import io.github.kawaiicakes.client.model.VerticalModels;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.data.client.*;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.text.WordUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.github.kawaiicakes.VSCreateArmor.MOD_ID;
import static net.minecraft.block.Blocks.NETHERITE_BLOCK;

public class Registry implements DataGeneratorEntrypoint {
    static List<BlockItem> REGISTERED = new ArrayList<>();

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(VSCArmorBlockLootTables::new);
        pack.addProvider(VSCArmorModelProvider::new);
        pack.addProvider(VSCArmorBlockTagProvider::new);
        pack.addProvider(VSCArmorItemTagProvider::new);
        pack.addProvider(VSCArmorRecipeProvider::new);
        pack.addProvider(VSCArmorLangProvider::new);
    }

    // TODO (1.1) - new organization scheme
    static void register() {
        registerArmor();

        net.minecraft.registry.Registry.register(
                Registries.ITEM_GROUP,
                new Identifier(MOD_ID, "vscarmor_group"),
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup.vscarmor_group"))
                        .icon(() -> REGISTERED.get(0).getDefaultStack())
                        .entries(
                                (context, entries) -> {
                                    for (Item item : REGISTERED) {
                                        entries.add(item);
                                    }
                                }
                        )
                        .build()
        );
    }

    /*
        --HELPER METHODS BELOW--
     */

    private static void registerArmor() {
        for (String color : colors()) {
            String prefix = color.isEmpty() ? "" : color + "_";

            registerArmorBlockFamily(prefix + "light_armor", 3.0F, 5.0F);
            registerArmorBlockFamily(prefix + "steel_armor", 10.0F, 7.0F);
            registerArmorBlockFamily(prefix + "composite_armor", 28.0F, 8.0F);
            registerArmorBlockFamily(prefix + "reinforced_armor", 50.0F, 20.0F);
        }
    }

    // TODO - Add commented colours + patterns.
    public static String[] colors() {
        return new String[] {
                "",
                "white",
                "light_gray",
                "gray",
                "black",
                "brown",
                "red",
                "orange",
                "yellow",
                "lime",
                "green",
                "cyan",
                "light_blue",
                "blue",
                "purple",
                "magenta",
                "pink",
                "4b0",
                "29",
                "31",
                "32",
                "33",
                "dunkelgelb",
                "panzergrau",
                // "parade",
                "rotbraun",
                // "ship_lower",
                // rainbow,
                "camo_desert",
                "camo_forest",
                /*
                "camo_jungle",
                "camo_mesa",
                "camo_plains",
                "camo_snow",
                "camo_swamp",
                "camo_taiga"
                 */
                "camo_bush",
                "camo_arctic"
        };
    }

    private static void registerArmorBlockFamily(String id, float hardness, float blastResistance) {
        final Block baseBlock = new Block(
                FabricBlockSettings.copyOf(NETHERITE_BLOCK)
                        .hardness(hardness)
                        .resistance(blastResistance)
        );

        final SlabBlock slabBlock = new SlabBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.5F)
                        .resistance(blastResistance * 0.5F)
        );

        final VerticalSlabBlock verticalSlabBlock = new VerticalSlabBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.5F)
                        .resistance(blastResistance * 0.5F)
        );

        final StairsBlock stairsBlock = new StairsBlock(
                baseBlock.getDefaultState(),
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.75F)
                        .resistance(blastResistance * 0.75F)
        );

        final VerticalStairsBlock verticalStairsBlock = new VerticalStairsBlock(
                baseBlock.getDefaultState(),
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.75F)
                        .resistance(blastResistance * 0.75F)
        );


        final WallBlock wallBlock = new WallBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.25F)
                        .resistance(blastResistance * 0.25F)
                        .solid()
        );

        registerBlockWithItem(id, baseBlock);
        registerBlockWithItem(id + "_slab", slabBlock);
        registerBlockWithItem(id + "_vertical_slab", verticalSlabBlock);
        registerBlockWithItem(id + "_stairs", stairsBlock);
        registerBlockWithItem(id + "_vertical_stairs", verticalStairsBlock);
        registerBlockWithItem(id + "_wall", wallBlock);

        // Waterline Black cannot exist
        if (id.startsWith("black_")) return;

        final Block wlBaseBlock = new Block(
                FabricBlockSettings.copyOf(NETHERITE_BLOCK)
                        .hardness(hardness)
                        .resistance(blastResistance)
        );

        final SlabBlock wlSlabBlock = new SlabBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.5F)
                        .resistance(blastResistance * 0.5F)
        );

        final VerticalSlabBlock wlVerticalSlabBlock = new VerticalSlabBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.5F)
                        .resistance(blastResistance * 0.5F)
        );

        final StairsBlock wlStairsBlock = new StairsBlock(
                baseBlock.getDefaultState(),
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.75F)
                        .resistance(blastResistance * 0.75F)
        );

        final VerticalStairsBlock wlVerticalStairsBlock = new VerticalStairsBlock(
                baseBlock.getDefaultState(),
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.75F)
                        .resistance(blastResistance * 0.75F)
        );

        final WallBlock wlWallBlock = new WallBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.25F)
                        .resistance(blastResistance * 0.25F)
                        .solid()
        );

        registerBlockWithItem("wl_" + id, wlBaseBlock);
        registerBlockWithItem("wl_" + id + "_slab", wlSlabBlock);
        registerBlockWithItem("wl_" + id + "_vertical_slab", wlVerticalSlabBlock);
        registerBlockWithItem("wl_" + id + "_stairs", wlStairsBlock);
        registerBlockWithItem("wl_" + id + "_vertical_stairs", wlVerticalStairsBlock);
        registerBlockWithItem("wl_" + id + "wall", wlWallBlock);
    }

    private static void registerBlockWithItem(String id, Block baseBlock) {
        net.minecraft.registry.Registry.register(
                Registries.BLOCK,
                new Identifier(MOD_ID, id),
                baseBlock
        );

        REGISTERED.add(
                net.minecraft.registry.Registry.register(
                        Registries.ITEM,
                        new Identifier(MOD_ID, id),
                        new BlockItem(baseBlock, new FabricItemSettings())
                )
        );
    }

    /*
        HELPER METHODS END
     */

    private static class VSCArmorBlockLootTables extends FabricBlockLootTableProvider {
        public VSCArmorBlockLootTables(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        public LootTable.Builder verticalSlabDrops(Block drop) {
            return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F))
                    .with(this.applyExplosionDecay(
                            drop,
                            ItemEntry.builder(drop).apply(
                                    SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))
                                            .conditionally(BlockStatePropertyLootCondition.builder(drop)
                                                    .properties(StatePredicate.Builder.create()
                                                            .exactMatch(VerticalSlabBlock.DOUBLET, true))
                                            ))
                    ))
            );
        }

        @Override
        public void generate() {
            for (BlockItem blockItem : REGISTERED) {
                if (blockItem.getBlock() instanceof SlabBlock slab) {
                    addDrop(slab, slabDrops(slab));
                    continue;
                }

                if (blockItem.getBlock() instanceof VerticalSlabBlock verticalSlab) {
                    addDrop(verticalSlab, verticalSlabDrops(verticalSlab));
                    continue;
                }

                addDrop(blockItem.getBlock(), drops(blockItem));
            }
        }
    }

    private static class VSCArmorModelProvider extends FabricModelProvider {
        public VSCArmorModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            createSimpleModels(blockStateModelGenerator);
            createVerticalSlabs(
                    blockStateModelGenerator,
                    allBlockGradesAndPatternCombinations(),
                    VSCArmorModelProvider::sideTopBottomSimple
            );
            createVerticalStairs(
                    blockStateModelGenerator,
                    allBlockGradesAndPatternCombinations(),
                    VSCArmorModelProvider::sideTopBottomSimple
            );
        }

        /**
         * Helper method for generating vanilla block models from a block colour/type.
         * "Vanilla blocks" include singleton simple cube alls (like an oak plank), slabs, stairs, fences, etc.
         */
        public static void createSimpleModels(BlockStateModelGenerator generator) {
            for (String pattern : allBlockGradesAndPatternCombinations()) {
                Identifier baseBlockId = new Identifier(MOD_ID, pattern);

                Block baseBlock = Registries.BLOCK.get(baseBlockId);
                Block slabBlock = Registries.BLOCK.get(baseBlockId.withSuffixedPath("_slab"));
                Block stairsBlock = Registries.BLOCK.get(baseBlockId.withSuffixedPath("_stairs"));
                Block wallBlock = Registries.BLOCK.get(baseBlockId.withSuffixedPath("_wall"));

                Identifier baseBlockModelId = TextureMap.getId(baseBlock);

                final TextureMap map = TextureMap.all(baseBlockModelId)
                        .put(TextureKey.SIDE, baseBlockModelId)
                        .put(TextureKey.TOP, baseBlockModelId)
                        .put(TextureKey.BOTTOM, baseBlockModelId)
                        .put(TextureKey.END, baseBlockModelId)
                        .put(TextureKey.TEXTURE, baseBlockModelId)
                        .put(TextureKey.WALL, baseBlockModelId);

                generator.new BlockTexturePool(map)
                        .base(baseBlock, Models.CUBE_ALL)
                        .slab(slabBlock)
                        .stairs(stairsBlock)
                        .wall(wallBlock);
            }
        }

        public static void createVerticalSlabs(
                BlockStateModelGenerator generator, String[] patterns, Function<Block, TextureMap> mapFunction
        ) {
            createVerticalSlabs(generator, patterns, mapFunction, "");
        }

        public static void createVerticalSlabs(
                BlockStateModelGenerator generator, String[] patterns, Function<Block, TextureMap> mapFunction,
                String additionalPrefix
        ) {
            for (String pattern : patterns) {
                Identifier baseBlockId = new Identifier(MOD_ID, pattern);
                Block baseBlock = Registries.BLOCK.get(baseBlockId);
                Block vSlabBlock = Registries.BLOCK.get(
                        baseBlockId.withSuffixedPath("_vertical_slab" + additionalPrefix)
                );

                TextureMap map = mapFunction.apply(baseBlock);

                Identifier baseModelId = TextureMap.getId(baseBlock);
                Identifier vSlabBlockModelId = VerticalModels.V_SLAB.upload(vSlabBlock, map, generator.modelCollector);

                generator.blockStateCollector.accept(
                        VariantsBlockStateSupplier
                                .create(vSlabBlock)
                                .coordinate(
                                        BlockStateVariantMap
                                                .create(Properties.HORIZONTAL_FACING, VerticalSlabBlock.DOUBLET)
                                                .register(
                                                        Direction.EAST, Boolean.FALSE,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, vSlabBlockModelId)
                                                )
                                                .register(
                                                        Direction.SOUTH, Boolean.FALSE,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, vSlabBlockModelId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                                .put(VariantSettings.UVLOCK, Boolean.TRUE)
                                                )
                                                .register(
                                                        Direction.WEST, Boolean.FALSE,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, vSlabBlockModelId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                                .put(VariantSettings.UVLOCK, Boolean.TRUE)
                                                )
                                                .register(
                                                        Direction.NORTH, Boolean.FALSE,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, vSlabBlockModelId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                                .put(VariantSettings.UVLOCK, Boolean.TRUE)
                                                )
                                                .register(
                                                        Direction.EAST, Boolean.TRUE,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, baseModelId)
                                                )
                                                .register(
                                                        Direction.SOUTH, Boolean.TRUE,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, baseModelId)
                                                )
                                                .register(Direction.WEST, Boolean.TRUE,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, baseModelId)
                                                )
                                                .register(Direction.NORTH, Boolean.TRUE,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, baseModelId)
                                                )
                                )
                );

                generator.registerParentedItemModel(vSlabBlock, vSlabBlockModelId);
            }
        }

        public static void createVerticalStairs(
                BlockStateModelGenerator generator, String[] patterns, Function<Block, TextureMap> mapFunction
        ) {
            for (String pattern : patterns) {
                Identifier baseBlockId = new Identifier(MOD_ID, pattern);
                Block baseBlock = Registries.BLOCK.get(baseBlockId);
                Block vStairsBlock = Registries.BLOCK.get(
                        baseBlockId.withSuffixedPath("_vertical_stairs")
                );

                TextureMap map = mapFunction.apply(baseBlock);

                Identifier regularModelId = VerticalModels.V_STAIRS_STRAIGHT
                        .upload(vStairsBlock, map, generator.modelCollector);

                Identifier innerModelBottomId = VerticalModels.V_STAIRS_INNER_BOTTOM
                        .upload(vStairsBlock, map, generator.modelCollector);

                Identifier innerModelTopId = VerticalModels.V_STAIRS_INNER_TOP
                        .upload(vStairsBlock, map, generator.modelCollector);

                Identifier outerModelRightBottomId = VerticalModels.V_STAIRS_OUTER_RIGHT_BOTTOM
                        .upload(vStairsBlock, map, generator.modelCollector);

                Identifier outerModelRightTopId = VerticalModels.V_STAIRS_OUTER_RIGHT_TOP
                        .upload(vStairsBlock, map, generator.modelCollector);

                Identifier outerModelLeftBottomId = VerticalModels.V_STAIRS_OUTER_LEFT_BOTTOM
                        .upload(vStairsBlock, map, generator.modelCollector);

                Identifier outerModelLeftTopId = VerticalModels.V_STAIRS_OUTER_LEFT_TOP
                        .upload(vStairsBlock, map, generator.modelCollector);

                generator.blockStateCollector.accept(
                        VariantsBlockStateSupplier.create(vStairsBlock)
                                .coordinate(
                                        BlockStateVariantMap
                                                .create(
                                                        HorizontalFacingBlock.FACING,
                                                        VerticalStairsBlock.HALF,
                                                        VerticalStairsBlock.V_SHAPE
                                                )
                                                .register(
                                                        Direction.EAST,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.STRAIGHT,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, regularModelId)
                                                )
                                                .register(
                                                        Direction.WEST,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.STRAIGHT,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, regularModelId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.SOUTH,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.STRAIGHT,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, regularModelId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.NORTH,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.STRAIGHT,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, regularModelId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.EAST,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.OUTER_BOTTOM,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, outerModelRightBottomId)
                                                )
                                                .register(
                                                        Direction.WEST,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.OUTER_BOTTOM,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, outerModelRightBottomId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.SOUTH,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.OUTER_BOTTOM,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, outerModelRightBottomId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.NORTH,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.OUTER_BOTTOM,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, outerModelRightBottomId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.EAST,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.OUTER_TOP,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, outerModelRightTopId)
                                                )
                                                .register(
                                                        Direction.WEST,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.OUTER_TOP,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, outerModelRightTopId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.SOUTH,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.OUTER_TOP,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                                .put(VariantSettings.MODEL, outerModelRightTopId)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.NORTH,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.OUTER_TOP,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, outerModelRightTopId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.EAST,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.INNER_BOTTOM,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, innerModelBottomId)
                                                )
                                                .register(
                                                        Direction.WEST,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.INNER_BOTTOM,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, innerModelBottomId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.SOUTH,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.INNER_BOTTOM,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, innerModelBottomId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.NORTH,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.INNER_BOTTOM,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, innerModelBottomId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.EAST,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.INNER_TOP,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, innerModelTopId)
                                                )
                                                .register(
                                                        Direction.WEST,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.INNER_TOP,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, innerModelTopId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.SOUTH,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.INNER_TOP,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                                .put(VariantSettings.MODEL, innerModelTopId)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.NORTH,
                                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                                        VerticalStairsBlock.VerticalStairShape.INNER_TOP,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, innerModelTopId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.EAST,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.STRAIGHT,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, regularModelId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.WEST,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.STRAIGHT,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, regularModelId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.SOUTH,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.STRAIGHT,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, regularModelId)
                                                )
                                                .register(
                                                        Direction.NORTH,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.STRAIGHT,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, regularModelId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.EAST,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.OUTER_BOTTOM,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, outerModelLeftBottomId)
                                                )
                                                .register(
                                                        Direction.WEST,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.OUTER_BOTTOM,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, outerModelLeftBottomId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.SOUTH,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.OUTER_BOTTOM,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, outerModelLeftBottomId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.NORTH,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.OUTER_BOTTOM,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, outerModelLeftBottomId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.EAST,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.OUTER_TOP,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, outerModelLeftTopId)
                                                )
                                                .register(
                                                        Direction.WEST,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.OUTER_TOP,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, outerModelLeftTopId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.SOUTH,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.OUTER_TOP,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, outerModelLeftTopId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.NORTH,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.OUTER_TOP,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, outerModelLeftTopId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.EAST,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.INNER_BOTTOM,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, innerModelBottomId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.WEST,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.INNER_BOTTOM,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, innerModelBottomId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.SOUTH,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.INNER_BOTTOM,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, innerModelBottomId)
                                                )
                                                .register(
                                                        Direction.NORTH,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.INNER_BOTTOM,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, innerModelBottomId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.EAST,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.INNER_TOP,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, innerModelTopId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.WEST,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.INNER_TOP,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, innerModelTopId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                                .register(
                                                        Direction.SOUTH,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.INNER_TOP,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, innerModelTopId)
                                                )
                                                .register(
                                                        Direction.NORTH,
                                                        VerticalStairsBlock.BlockHalf.LEFT,
                                                        VerticalStairsBlock.VerticalStairShape.INNER_TOP,
                                                        BlockStateVariant.create()
                                                                .put(VariantSettings.MODEL, innerModelTopId)
                                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                                .put(VariantSettings.UVLOCK, true)
                                                )
                                )
                );

                generator.registerParentedItemModel(vStairsBlock, regularModelId);
            }
        }

        public static String[] allBlockGradesAndPatternCombinations() {
            String[] toReturn = new String[colors().length * 4];

            int colorIndex = 0;
            for (String color : colors()) {

                String prefix = color.isEmpty() ? "" : color + "_";

                toReturn[colorIndex * 4] = (prefix + "light_armor");
                toReturn[(colorIndex * 4) + 1] = (prefix + "steel_armor");
                toReturn[(colorIndex * 4) + 2] = (prefix + "composite_armor");
                toReturn[(colorIndex * 4) + 3] = (prefix + "reinforced_armor");

                colorIndex++;
            }

            return toReturn;
        }

        public static TextureMap sideTopBottomSimple(Block block) {
            return new TextureMap()
                    .put(TextureKey.SIDE, TextureMap.getId(block))
                    .put(TextureKey.TOP, TextureMap.getId(block))
                    .put(TextureKey.BOTTOM, TextureMap.getId(block));
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {}
    }

    // TODO - Tags for slabs, blocks, stairs, etc. for VS + CBC block properties
    private static class VSCArmorBlockTagProvider extends FabricTagProvider<Block> {
        public VSCArmorBlockTagProvider(
                FabricDataOutput output,
                CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
        ) {
            super(output, RegistryKeys.BLOCK, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            TagBuilder pickaxeMineable = getTagBuilder(BlockTags.PICKAXE_MINEABLE);
            TagBuilder beaconBase = getTagBuilder(BlockTags.BEACON_BASE_BLOCKS);
            TagBuilder diamondTools = getTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL);
            TagBuilder witherImmune = getTagBuilder(BlockTags.WITHER_IMMUNE);
            TagBuilder light
                    = getTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier(MOD_ID, "light_armor")));
            TagBuilder steel
                    = getTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier(MOD_ID, "steel_armor")));
            TagBuilder composite
                    = getTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier(MOD_ID, "composite_armor")));
            TagBuilder reinforced
                    = getTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier(MOD_ID, "reinforced_armor")));

            TagBuilder wallBlocks = getTagBuilder(BlockTags.WALLS);

            for (BlockItem blockItem : REGISTERED) {
                pickaxeMineable.add(Registries.BLOCK.getId(blockItem.getBlock()));
                diamondTools.add(Registries.BLOCK.getId(blockItem.getBlock()));
                witherImmune.add(Registries.BLOCK.getId(blockItem.getBlock()));

                if (isFullBlock(blockItem.getBlock())) {
                    beaconBase.add(Registries.BLOCK.getId(blockItem.getBlock()));
                }

                String blockPath = Registries.BLOCK.getId(blockItem.getBlock()).getPath();

                TagBuilder addTo;

                if (blockPath.contains("light_armor")) addTo = light;
                else if (blockPath.contains("steel_armor")) addTo = steel;
                else if (blockPath.contains("composite_armor")) addTo = composite;
                else addTo = reinforced;

                addTo.add(Registries.BLOCK.getId(blockItem.getBlock()));

                if (isWall(blockItem.getBlock()))
                    wallBlocks.add(Registries.BLOCK.getId(blockItem.getBlock()));
            }
        }

        public static boolean isFullBlock(Block block) {
            return !(block instanceof SlabBlock)
                    && !(block instanceof VerticalSlabBlock)
                    && !(block instanceof StairsBlock)
                    && !(block instanceof VerticalStairsBlock);
        }

        public static boolean isWall(Block block) {
            return block instanceof WallBlock;
        }
    }

    private static class VSCArmorItemTagProvider extends FabricTagProvider<Item> {
        public VSCArmorItemTagProvider(
                FabricDataOutput output,
                CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
        ) {
            super(output, RegistryKeys.ITEM, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {

        }
    }

    // TODO (1.1) - rudimentary recipes
    private static class VSCArmorRecipeProvider extends FabricRecipeProvider {
        public VSCArmorRecipeProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generate(Consumer<RecipeJsonProvider> exporter) {

        }
    }

    private static class VSCArmorLangProvider extends FabricLanguageProvider {
        private VSCArmorLangProvider(FabricDataOutput dataGenerator) {
            super(dataGenerator, "en_us");
        }

        // Surely nothing can go horribly wrong here!
        @SuppressWarnings("deprecation")
        private static String sanitizeName(String rawId) {
            String toReturn = WordUtils.capitalize(
                    rawId.replace("block.vscarmor.", "").replace("_", " ")
            );

            toReturn = toReturn
                    .replaceFirst("Ab ", "Alphabet ")
                    .replaceFirst("Wl ", "Waterline ")
                    .replaceFirst("29 ", "Blue #29 ")
                    .replaceFirst("31 ", "Gray #31 ")
                    .replaceFirst("32 ", "Gray #32 ")
                    .replaceFirst("33 ", "Blue #33 ")
                    .replaceFirst("4b0 ", "Soviet 4B0 Green ");

            if (toReturn.contains("Camo ")) {
                String waterline = toReturn.contains("Waterline ") ? "Waterline " : "";
                toReturn = toReturn.replaceFirst("Waterline ", "");

                toReturn = toReturn.replaceFirst("Camo ", "");

                String[] split = toReturn.split(" ", 2);

                toReturn = waterline + split[0] + " Camo " + split[1];
            }

            return toReturn;
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            for (BlockItem blockItem : REGISTERED) {
                String name = sanitizeName(blockItem.getTranslationKey());
                translationBuilder.add(blockItem.getBlock(), name);
                translationBuilder.add(Util.createTranslationKey("item", Registries.ITEM.getId(blockItem)), name);
            }

            try {
                Path existingFilePath = this.dataOutput
                        .getModContainer()
                        .findPath("assets/vscarmor/lang/en_us.existing.json")
                        .orElseThrow();

                translationBuilder.add(existingFilePath);
            } catch (Exception e) {
                throw new RuntimeException("Failed to add existing language file!", e);
            }
        }
    }
}
