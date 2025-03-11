package io.github.kawaiicakes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.kawaiicakes.block.*;
import io.github.kawaiicakes.client.model.ArmorBlockModels;
import io.github.kawaiicakes.client.model.VerticalModels;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
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
import org.jetbrains.annotations.NotNull;

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
        pack.addProvider(ValkyrienSkiesPropertyProvider::new);
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

    // TODO - Add ship_lower
    // TODO (1.1) - Add commented colours + patterns.
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

        final FenceBlock fenceBlock = new FenceBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.25F)
                        .resistance(blastResistance * 0.25F)
                        .solid()
        );

        final WallBlock wallBlock = new WallBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.25F)
                        .resistance(blastResistance * 0.25F)
                        .solid()
        );

        final PortholeBlock portholeBlock = new PortholeBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.75F)
                        .resistance(blastResistance * 0.75F)
                        .solid()
        );

        final PortholeSlab portholeSlab = new PortholeSlab(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.25F)
                        .resistance(blastResistance * 0.25F)
                        .solid()
        );

        final PortholeVerticalSlab portholeVerticalSlab = new PortholeVerticalSlab(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.25F)
                        .resistance(blastResistance * 0.25F)
                        .solid()
        );

        final VerticalWindowBlock verticalWindowBlock = new VerticalWindowBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.75F)
                        .resistance(blastResistance * 0.75F)
                        .solid()
        );

        final VerticalWindowSlab verticalWindowSlab = new VerticalWindowSlab(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.25F)
                        .resistance(blastResistance * 0.25F)
                        .solid()
        );

        final VerticalWindowVerticalSlab verticalWindowVerticalSlab = new VerticalWindowVerticalSlab(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.25F)
                        .resistance(blastResistance * 0.25F)
                        .solid()
        );

        final HorizontalWindowBlock horizontalWindowBlock = new HorizontalWindowBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.75F)
                        .resistance(blastResistance * 0.75F)
                        .solid()
        );

        final HorizontalWindowSlab horizontalWindowSlab = new HorizontalWindowSlab(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.25F)
                        .resistance(blastResistance * 0.25F)
                        .solid()
        );

        final HorizontalWindowVerticalSlab horizontalWindowVerticalSlab = new HorizontalWindowVerticalSlab(
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
        registerBlockWithItem(id + "_fence", fenceBlock);
        registerBlockWithItem(id + "_wall", wallBlock);
        registerBlockWithItem(id + "_porthole", portholeBlock);
        registerBlockWithItem(id + "_porthole_slab", portholeSlab);
        registerBlockWithItem(id + "_porthole_vertical_slab", portholeVerticalSlab);
        registerBlockWithItem(id + "_vertical_window", verticalWindowBlock);
        registerBlockWithItem(id + "_vertical_window_slab", verticalWindowSlab);
        registerBlockWithItem(id + "_vertical_window_vertical_slab", verticalWindowVerticalSlab);
        registerBlockWithItem(id + "_horizontal_window", horizontalWindowBlock);
        registerBlockWithItem(id + "_horizontal_window_slab", horizontalWindowSlab);
        registerBlockWithItem(id + "_horizontal_window_vertical_slab", horizontalWindowVerticalSlab);

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

        final FenceBlock wlFenceBlock = new FenceBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.25F)
                        .resistance(blastResistance * 0.25F)
                        .solid()
        );

        final WallBlock wlWallBlock = new WallBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.25F)
                        .resistance(blastResistance * 0.25F)
                        .solid()
        );

        final PortholeBlock wlPortholeBlock = new PortholeBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.75F)
                        .resistance(blastResistance * 0.75F)
                        .solid()
        );

        final PortholeSlab wlPortholeSlab = new PortholeSlab(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.25F)
                        .resistance(blastResistance * 0.25F)
                        .solid()
        );

        final PortholeVerticalSlab wlPortholeVerticalSlab = new PortholeVerticalSlab(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.25F)
                        .resistance(blastResistance * 0.25F)
                        .solid()
        );

        final VerticalWindowBlock wlVerticalWindowBlock = new VerticalWindowBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.75F)
                        .resistance(blastResistance * 0.75F)
                        .solid()
        );

        final VerticalWindowSlab wlVerticalWindowSlab = new VerticalWindowSlab(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.25F)
                        .resistance(blastResistance * 0.25F)
                        .solid()
        );

        final VerticalWindowVerticalSlab wlVerticalWindowVerticalSlab = new VerticalWindowVerticalSlab(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.25F)
                        .resistance(blastResistance * 0.25F)
                        .solid()
        );

        final HorizontalWindowBlock wlHorizontalWindowBlock = new HorizontalWindowBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.75F)
                        .resistance(blastResistance * 0.75F)
                        .solid()
        );

        final HorizontalWindowSlab wlHorizontalWindowSlab = new HorizontalWindowSlab(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.25F)
                        .resistance(blastResistance * 0.25F)
                        .solid()
        );

        final HorizontalWindowVerticalSlab wlHorizontalWindowVerticalSlab = new HorizontalWindowVerticalSlab(
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
        registerBlockWithItem("wl_" + id + "_fence", wlFenceBlock);
        registerBlockWithItem("wl_" + id + "_wall", wlWallBlock);
        registerBlockWithItem("wl_" + id + "_porthole", wlPortholeBlock);
        registerBlockWithItem("wl_" + id + "_porthole_slab", wlPortholeSlab);
        registerBlockWithItem("wl_" + id + "_porthole_vertical_slab", wlPortholeVerticalSlab);
        registerBlockWithItem("wl_" + id + "_vertical_window", wlVerticalWindowBlock);
        registerBlockWithItem("wl_" + id + "_vertical_window_slab", wlVerticalWindowSlab);
        registerBlockWithItem("wl_" + id + "_vertical_window_vertical_slab", wlVerticalWindowVerticalSlab);
        registerBlockWithItem("wl_" + id + "_horizontal_window", wlHorizontalWindowBlock);
        registerBlockWithItem("wl_" + id + "_horizontal_window_slab", wlHorizontalWindowSlab);
        registerBlockWithItem("wl_" + id + "_horizontal_window_vertical_slab", wlHorizontalWindowVerticalSlab);
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

                if (blockItem.getBlock() instanceof AbstractWindowVerticalSlab verticalSlab) {
                    addDrop(verticalSlab, verticalSlabDrops(verticalSlab));
                    continue;
                }

                if (blockItem.getBlock() instanceof AbstractWindowSlab verticalSlab) {
                    addDrop(verticalSlab, slabDrops(verticalSlab));
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
            createWaterlineModels(blockStateModelGenerator);
        }

        public static void createSimpleModels(BlockStateModelGenerator generator) {
            for (String pattern : allBlockGradesAndPatternCombinations()) {
                Identifier baseBlockId = new Identifier(MOD_ID, pattern);

                Block baseBlock = Registries.BLOCK.get(baseBlockId);
                Block slabBlock = Registries.BLOCK.get(baseBlockId.withSuffixedPath("_slab"));
                Block stairsBlock = Registries.BLOCK.get(baseBlockId.withSuffixedPath("_stairs"));
                Block wallBlock = Registries.BLOCK.get(baseBlockId.withSuffixedPath("_wall"));
                Block fenceBlock = Registries.BLOCK.get(baseBlockId.withSuffixedPath("_fence"));

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
                        .fence(fenceBlock)
                        .stairs(stairsBlock)
                        .wall(wallBlock);

                createVerticalSlab(
                        generator,
                        pattern,
                        VSCArmorModelProvider::sideTopBottomSimple
                );
                createVerticalStairs(
                        generator,
                        pattern,
                        VSCArmorModelProvider::sideTopBottomSimple
                );

                createWindow(
                        "_porthole",
                        generator,
                        pattern,
                        block -> window(block, "", "", ""),
                        ArmorBlockModels.PORTHOLE, ArmorBlockModels.PORTHOLE_EMPTY,
                        ArmorBlockModels.VERTICAL_PORTHOLE, ArmorBlockModels.VERTICAL_PORTHOLE_EMPTY
                );

                createWindowSlab(
                        "_porthole_slab", "_porthole_vertical",
                        generator,
                        pattern,
                        block -> window(block, "", "", ""),
                        ArmorBlockModels.PORTHOLE_SLAB, ArmorBlockModels.PORTHOLE_SLAB_EMPTY,
                        ArmorBlockModels.PORTHOLE_SLAB_TOP, ArmorBlockModels.PORTHOLE_SLAB_TOP_EMPTY
                );

                createWindowVerticalSlab(
                        "_porthole_vertical_slab", "_porthole",
                        generator,
                        pattern,
                        block -> window(block, "", "", ""),
                        ArmorBlockModels.PORTHOLE_VERTICAL_SLAB, ArmorBlockModels.PORTHOLE_VERTICAL_SLAB_EMPTY
                );

                createWindow(
                        "_vertical_window",
                        generator,
                        pattern,
                        block -> window(block, "", "", ""),
                        ArmorBlockModels.VERTICAL_WINDOW, ArmorBlockModels.VERTICAL_WINDOW_EMPTY,
                        ArmorBlockModels.VERTICAL_WINDOW_VERTICAL, ArmorBlockModels.VERTICAL_WINDOW_VERTICAL_EMPTY
                );

                createWindowSlab(
                        "_vertical_window_slab", "_vertical_window_vertical",
                        generator,
                        pattern,
                        block -> window(block, "", "", ""),
                        ArmorBlockModels.VERTICAL_WINDOW_SLAB, ArmorBlockModels.VERTICAL_WINDOW_SLAB_EMPTY,
                        ArmorBlockModels.VERTICAL_WINDOW_SLAB_TOP, ArmorBlockModels.VERTICAL_WINDOW_SLAB_TOP_EMPTY
                );

                createWindowVerticalSlab(
                        "_vertical_window_vertical_slab", "_vertical_window",
                        generator,
                        pattern,
                        block -> window(block, "", "", ""),
                        ArmorBlockModels.VERTICAL_WINDOW_VERTICAL_SLAB,
                        ArmorBlockModels.VERTICAL_WINDOW_VERTICAL_SLAB_EMPTY
                );

                createWindow(
                        "_horizontal_window",
                        generator,
                        pattern,
                        block -> window(block, "", "", ""),
                        ArmorBlockModels.HORIZONTAL_WINDOW, ArmorBlockModels.HORIZONTAL_WINDOW_EMPTY,
                        ArmorBlockModels.HORIZONTAL_WINDOW_VERTICAL, ArmorBlockModels.HORIZONTAL_WINDOW_VERTICAL_EMPTY
                );

                createWindowSlab(
                        "_horizontal_window_slab", "_horizontal_window_vertical",
                        generator,
                        pattern,
                        block -> window(block, "", "", ""),
                        ArmorBlockModels.HORIZONTAL_WINDOW_SLAB, ArmorBlockModels.HORIZONTAL_WINDOW_SLAB_EMPTY,
                        ArmorBlockModels.HORIZONTAL_WINDOW_SLAB_TOP, ArmorBlockModels.HORIZONTAL_WINDOW_SLAB_TOP_EMPTY
                );

                createWindowVerticalSlab(
                        "_horizontal_window_vertical_slab", "_horizontal_window",
                        generator,
                        pattern,
                        block -> window(block, "", "", ""),
                        ArmorBlockModels.HORIZONTAL_WINDOW_VERTICAL_SLAB,
                        ArmorBlockModels.HORIZONTAL_WINDOW_VERTICAL_SLAB_EMPTY
                );
            }
        }

        public static void createWaterlineModels(BlockStateModelGenerator generator) {
            for (String pattern : allBlockGradesAndPatternCombinations()) {
                if (pattern.startsWith("black_")) continue;

                Identifier topPatternId = new Identifier(MOD_ID, pattern).withPrefixedPath("block/");
                Identifier waterlineBaseId = new Identifier(MOD_ID, "wl_" + pattern);

                String bottomPath = getWaterlineBottomPath(topPatternId);

                Identifier blackPatternId = new Identifier(MOD_ID, bottomPath).withPrefixedPath("block/");

                Block baseBlock = Registries.BLOCK.get(waterlineBaseId);
                Block slabBlock = Registries.BLOCK.get(waterlineBaseId.withSuffixedPath("_slab"));
                Block stairsBlock = Registries.BLOCK.get(waterlineBaseId.withSuffixedPath("_stairs"));
                Block wallBlock = Registries.BLOCK.get(waterlineBaseId.withSuffixedPath("_wall"));
                Block fenceBlock = Registries.BLOCK.get(waterlineBaseId.withSuffixedPath("_fence"));

                Identifier baseBlockModelId = TextureMap.getId(baseBlock);

                final TextureMap map = TextureMap.all(baseBlockModelId)
                        .put(TextureKey.SIDE, baseBlockModelId)
                        .put(TextureKey.TOP, topPatternId)
                        .put(TextureKey.BOTTOM, blackPatternId)
                        .put(TextureKey.END, baseBlockModelId)
                        .put(TextureKey.TEXTURE, baseBlockModelId)
                        .put(TextureKey.WALL, baseBlockModelId);

                final TextureMap invertedMap = TextureMap.all(baseBlockModelId)
                        .put(TextureKey.SIDE, baseBlockModelId)
                        .put(TextureKey.TOP, blackPatternId)
                        .put(TextureKey.BOTTOM, topPatternId)
                        .put(TextureKey.END, baseBlockModelId)
                        .put(TextureKey.TEXTURE, baseBlockModelId)
                        .put(TextureKey.WALL, baseBlockModelId);

                generator.new BlockTexturePool(map)
                        .base(baseBlock, Models.CUBE_BOTTOM_TOP)
                        .slab(slabBlock);

                Identifier innerBottomId
                        = Models.INNER_STAIRS.upload(stairsBlock, map, generator.modelCollector);
                Identifier straightBottomId
                        = Models.STAIRS.upload(stairsBlock, map, generator.modelCollector);
                Identifier outerBottomId
                        = Models.OUTER_STAIRS.upload(stairsBlock, map, generator.modelCollector);
                Identifier innerTopId
                        = ArmorBlockModels.INNER_STAIRS_TOP.upload(stairsBlock, invertedMap, generator.modelCollector);
                Identifier straightTopId
                        = ArmorBlockModels.STAIRS_TOP.upload(stairsBlock, invertedMap, generator.modelCollector);
                Identifier outerTopId
                        = ArmorBlockModels.OUTER_STAIRS_TOP.upload(stairsBlock, invertedMap, generator.modelCollector);
                generator.blockStateCollector.accept(
                        createWaterlineStairsBlockstate(
                                stairsBlock,
                                innerBottomId, straightBottomId, outerBottomId,
                                innerTopId, straightTopId, outerTopId
                        )
                );

                generator.registerParentedItemModel(stairsBlock, straightBottomId);

                Identifier wallPostId
                        = ArmorBlockModels.TEMPLATE_WALL_POST.upload(wallBlock, map, generator.modelCollector);
                Identifier wallSideId
                        = ArmorBlockModels.TEMPLATE_WALL_SIDE.upload(wallBlock, map, generator.modelCollector);
                Identifier wallSideTallId
                        = ArmorBlockModels.TEMPLATE_WALL_SIDE_TALL.upload(wallBlock, map, generator.modelCollector);

                generator.blockStateCollector.accept(
                        BlockStateModelGenerator.createWallBlockState(
                                wallBlock, wallPostId, wallSideId, wallSideTallId
                        )
                );

                Identifier wallInventoryId
                        = ArmorBlockModels.WALL_INVENTORY.upload(wallBlock, map, generator.modelCollector);
                generator.registerParentedItemModel(wallBlock, wallInventoryId);

                Identifier fencePost = ArmorBlockModels.FENCE_POST.upload(fenceBlock, map, generator.modelCollector);
                Identifier fenceSide = ArmorBlockModels.FENCE_SIDE.upload(fenceBlock, map, generator.modelCollector);
                generator.blockStateCollector.accept(
                        BlockStateModelGenerator.createFenceBlockState(fenceBlock, fencePost, fenceSide)
                );
                Identifier fenceInventory = ArmorBlockModels.FENCE_INVENTORY.upload(
                        fenceBlock, map, generator.modelCollector
                );
                generator.registerParentedItemModel(fenceBlock, fenceInventory);

                createVerticalSlab(
                        generator,
                        "wl_" + pattern,
                        VSCArmorModelProvider::sideTopBottomWaterline
                );
                createVerticalStairs(
                        generator,
                        "wl_" + pattern,
                        VSCArmorModelProvider::sideTopBottomWaterline
                );

                createWindow(
                        "_porthole",
                        generator,
                        "wl_" + pattern,
                        block -> window(
                                block, "",
                                MOD_ID + ":" + pattern,
                                MOD_ID + ":" + getWaterlineBottomPath(Registries.BLOCK.getId(block))
                        ),
                        ArmorBlockModels.PORTHOLE, ArmorBlockModels.PORTHOLE_EMPTY,
                        ArmorBlockModels.VERTICAL_PORTHOLE, ArmorBlockModels.VERTICAL_PORTHOLE_EMPTY
                );

                createWindowSlab(
                        "_porthole_slab", "_porthole_vertical",
                        generator,
                        "wl_" + pattern,
                        block -> window(
                                block, "",
                                MOD_ID + ":" + pattern,
                                MOD_ID + ":" + getWaterlineBottomPath(Registries.BLOCK.getId(block))
                        ),
                        ArmorBlockModels.PORTHOLE_SLAB, ArmorBlockModels.PORTHOLE_SLAB_EMPTY,
                        ArmorBlockModels.PORTHOLE_SLAB_TOP, ArmorBlockModels.PORTHOLE_SLAB_TOP_EMPTY
                );

                createWindowVerticalSlab(
                        "_porthole_vertical_slab", "_porthole_vertical",
                        generator,
                        "wl_" + pattern,
                        block -> window(
                                block, "",
                                MOD_ID + ":" + pattern,
                                MOD_ID + ":" + getWaterlineBottomPath(Registries.BLOCK.getId(block))
                        ),
                        ArmorBlockModels.PORTHOLE_VERTICAL_SLAB, ArmorBlockModels.PORTHOLE_VERTICAL_SLAB_EMPTY
                );

                createWindow(
                        "_vertical_window",
                        generator,
                        "wl_" + pattern,
                        block -> window(
                                block, "",
                                MOD_ID + ":" + pattern,
                                MOD_ID + ":" + getWaterlineBottomPath(Registries.BLOCK.getId(block))
                        ),
                        ArmorBlockModels.VERTICAL_WINDOW, ArmorBlockModels.VERTICAL_WINDOW_EMPTY,
                        ArmorBlockModels.VERTICAL_WINDOW_VERTICAL, ArmorBlockModels.VERTICAL_WINDOW_VERTICAL_EMPTY
                );

                createWindowSlab(
                        "_vertical_window_slab", "_vertical_window",
                        generator,
                        "wl_" + pattern,
                        block -> window(
                                block, "",
                                MOD_ID + ":" + pattern,
                                MOD_ID + ":" + getWaterlineBottomPath(Registries.BLOCK.getId(block))
                        ),
                        ArmorBlockModels.VERTICAL_WINDOW_SLAB, ArmorBlockModels.VERTICAL_WINDOW_SLAB_EMPTY,
                        ArmorBlockModels.VERTICAL_WINDOW_SLAB_TOP, ArmorBlockModels.VERTICAL_WINDOW_SLAB_TOP_EMPTY
                );

                createWindowVerticalSlab(
                        "_vertical_window_vertical_slab", "_vertical_window",
                        generator,
                        "wl_" + pattern,
                        block -> window(
                                block, "",
                                MOD_ID + ":" + pattern,
                                MOD_ID + ":" + getWaterlineBottomPath(Registries.BLOCK.getId(block))
                        ),
                        ArmorBlockModels.VERTICAL_WINDOW_VERTICAL_SLAB,
                        ArmorBlockModels.VERTICAL_WINDOW_VERTICAL_SLAB_EMPTY
                );

                createWindow(
                        "_horizontal_window",
                        generator,
                        "wl_" + pattern,
                        block -> window(
                                block, "",
                                MOD_ID + ":" + pattern,
                                MOD_ID + ":" + getWaterlineBottomPath(Registries.BLOCK.getId(block))
                        ),
                        ArmorBlockModels.HORIZONTAL_WINDOW, ArmorBlockModels.HORIZONTAL_WINDOW_EMPTY,
                        ArmorBlockModels.HORIZONTAL_WINDOW_VERTICAL, ArmorBlockModels.HORIZONTAL_WINDOW_VERTICAL_EMPTY
                );

                createWindowSlab(
                        "_horizontal_window_slab", "_horizontal_window",
                        generator,
                        "wl_" + pattern,
                        block -> window(
                                block, "",
                                MOD_ID + ":" + pattern,
                                MOD_ID + ":" + getWaterlineBottomPath(Registries.BLOCK.getId(block))
                        ),
                        ArmorBlockModels.HORIZONTAL_WINDOW_SLAB, ArmorBlockModels.HORIZONTAL_WINDOW_SLAB_EMPTY,
                        ArmorBlockModels.HORIZONTAL_WINDOW_SLAB_TOP, ArmorBlockModels.HORIZONTAL_WINDOW_SLAB_TOP_EMPTY
                );

                createWindowVerticalSlab(
                        "_horizontal_window_vertical_slab", "_horizontal_window",
                        generator,
                        "wl_" + pattern,
                        block -> window(
                                block, "",
                                MOD_ID + ":" + pattern,
                                MOD_ID + ":" + getWaterlineBottomPath(Registries.BLOCK.getId(block))
                        ),
                        ArmorBlockModels.HORIZONTAL_WINDOW_VERTICAL_SLAB,
                        ArmorBlockModels.HORIZONTAL_WINDOW_VERTICAL_SLAB_EMPTY
                );
            }
        }

        public static void createVerticalSlab(
                BlockStateModelGenerator generator, String pattern, Function<Block, TextureMap> mapFunction
        ) {
            Identifier baseBlockId = new Identifier(MOD_ID, pattern);
            Block baseBlock = Registries.BLOCK.get(baseBlockId);
            Block vSlabBlock = Registries.BLOCK.get(
                    baseBlockId.withSuffixedPath("_vertical_slab")
            );

            TextureMap map = mapFunction.apply(baseBlock);

            Identifier baseModelId = TextureMap.getId(baseBlock);
            Identifier vSlabBlockModelId = VerticalModels.V_SLAB.upload(vSlabBlock, map, generator.modelCollector);

            generator.blockStateCollector.accept(
                    createVerticalSlabBlockstate(vSlabBlock, vSlabBlockModelId, baseModelId)
            );

            generator.registerParentedItemModel(vSlabBlock, vSlabBlockModelId);
        }

        public static void createVerticalStairs(
                BlockStateModelGenerator generator, String pattern, Function<Block, TextureMap> mapFunction
        ) {
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
                    createVerticalStairsBlockstate(
                            vStairsBlock, regularModelId, innerModelTopId, innerModelBottomId,
                            outerModelRightBottomId, outerModelRightTopId,
                            outerModelLeftBottomId, outerModelLeftTopId
                    )
            );

            generator.registerParentedItemModel(vStairsBlock, regularModelId);
        }

        /**
         * @param pattern also makes a reference to the base block.
         * @param mapFunction this method passes the base block to {@code Function<Block, TextureMap>#apply}.
         */
        public static void createWindow(
                String windowSuffix,
                BlockStateModelGenerator generator, String pattern, Function<Block, TextureMap> mapFunction,
                Model windowBase, Model emptyWindow, Model verticalWindow, Model emptyVerticalWindow
        ) {
            Identifier baseBlockId = new Identifier(MOD_ID, pattern);
            Block baseBlock = Registries.BLOCK.get(baseBlockId);
            Block portholeBlock = Registries.BLOCK.get(
                    baseBlockId.withSuffixedPath(windowSuffix)
            );
            Identifier blockModelId = windowBase.upload(
                    portholeBlock, mapFunction.apply(baseBlock), generator.modelCollector
            );
            Identifier emptyBlockModelId = emptyWindow.upload(
                    portholeBlock, mapFunction.apply(baseBlock), generator.modelCollector
            );
            Identifier verticalBlockModelId = verticalWindow.upload(
                    portholeBlock, mapFunction.apply(baseBlock), generator.modelCollector
            );
            Identifier verticalEmptyBlockModelId = emptyVerticalWindow.upload(
                    portholeBlock, mapFunction.apply(baseBlock), generator.modelCollector
            );

            generator.blockStateCollector.accept(
                    createWindowBlockstate(
                            portholeBlock,
                            blockModelId, emptyBlockModelId,
                            verticalBlockModelId, verticalEmptyBlockModelId
                    )
            );

            generator.registerParentedItemModel(portholeBlock, blockModelId);
        }

        public static void createWindowSlab(
                String windowSuffix, String suffix2,
                BlockStateModelGenerator generator, String pattern, Function<Block, TextureMap> mapFunction,
                Model windowBase, Model emptyWindow, Model topWindow, Model emptyTopWindow
        ) {
            Identifier baseBlockId = new Identifier(MOD_ID, pattern);
            Block baseBlock = Registries.BLOCK.get(baseBlockId);
            Block portholeSlab = Registries.BLOCK.get(
                    baseBlockId.withSuffixedPath(windowSuffix)
            );


            Identifier portholeDoubleModelId = TextureMap.getSubId(baseBlock, suffix2);

            Identifier portholeDoubleEmptyModelId = TextureMap.getSubId(baseBlock, suffix2 + "_empty");

            Identifier portholeSlabModelId = windowBase.upload(
                    portholeSlab, mapFunction.apply(baseBlock), generator.modelCollector
            );
            Identifier portholeSlabEmptyModelId = emptyWindow.upload(
                    portholeSlab, mapFunction.apply(baseBlock), generator.modelCollector
            );
            Identifier portholeSlabTopModelId = topWindow.upload(
                    portholeSlab, mapFunction.apply(baseBlock), generator.modelCollector
            );
            Identifier portholeSlabTopEmptyModelId = emptyTopWindow.upload(
                    portholeSlab, mapFunction.apply(baseBlock), generator.modelCollector
            );

            generator.blockStateCollector.accept(
                    createWindowSlabBlockstate(
                            portholeSlab,
                            portholeDoubleModelId, portholeDoubleEmptyModelId,
                            portholeSlabModelId, portholeSlabEmptyModelId,
                            portholeSlabTopModelId, portholeSlabTopEmptyModelId
                    )
            );

            generator.registerParentedItemModel(portholeSlab, portholeSlabModelId);
        }

        public static void createWindowVerticalSlab(
                String windowSuffix, String suffix2,
                BlockStateModelGenerator generator, String pattern, Function<Block, TextureMap> mapFunction,
                Model windowSlabModelId, Model emptyWindowSlabModelId
        ) {
            Identifier baseBlockId = new Identifier(MOD_ID, pattern);
            Block baseBlock = Registries.BLOCK.get(baseBlockId);
            Block portholeVSlab = Registries.BLOCK.get(
                    baseBlockId.withSuffixedPath(windowSuffix)
            );

            Identifier portholeDoubleModelId = TextureMap.getSubId(baseBlock, suffix2);
            Identifier portholeDoubleEmptyModelId = TextureMap.getSubId(baseBlock, suffix2 + "_empty");

            Identifier portholeSlabModelId = windowSlabModelId.upload(
                    portholeVSlab, mapFunction.apply(baseBlock), generator.modelCollector
            );
            Identifier portholeSlabEmptyModelId = emptyWindowSlabModelId.upload(
                    portholeVSlab, mapFunction.apply(baseBlock), generator.modelCollector
            );

            generator.blockStateCollector.accept(
                    createWindowVerticalSlabBlockstate(
                            portholeVSlab,
                            portholeSlabModelId, portholeSlabEmptyModelId,
                            portholeDoubleModelId, portholeDoubleEmptyModelId
                    )
            );

            generator.registerParentedItemModel(portholeVSlab, portholeSlabModelId);
        }

        public static BlockStateSupplier createWaterlineStairsBlockstate(
                Block stairsBlock,
                Identifier innerBottomModelId, Identifier regularBottomModelId, Identifier outerBottomModelId,
                Identifier innerTopModelId, Identifier regularTopModelId, Identifier outerTopModelId
        ) {
            return VariantsBlockStateSupplier.create(stairsBlock)
                    .coordinate(
                            BlockStateVariantMap
                                    .create(
                                            Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.STAIR_SHAPE
                                    )
                                    .register(
                                            Direction.EAST, BlockHalf.BOTTOM, StairShape.STRAIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, regularBottomModelId)
                                    )
                                    .register(
                                            Direction.WEST, BlockHalf.BOTTOM, StairShape.STRAIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, regularBottomModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.SOUTH, BlockHalf.BOTTOM, StairShape.STRAIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, regularBottomModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.NORTH, BlockHalf.BOTTOM, StairShape.STRAIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, regularBottomModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.EAST, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, outerBottomModelId)
                                    )
                                    .register(
                                            Direction.WEST, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, outerBottomModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.SOUTH, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, outerBottomModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.NORTH, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, outerBottomModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.EAST, BlockHalf.BOTTOM, StairShape.OUTER_LEFT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, outerBottomModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.WEST, BlockHalf.BOTTOM, StairShape.OUTER_LEFT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, outerBottomModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.SOUTH, BlockHalf.BOTTOM, StairShape.OUTER_LEFT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, outerBottomModelId)
                                    )
                                    .register(
                                            Direction.NORTH, BlockHalf.BOTTOM, StairShape.OUTER_LEFT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, outerBottomModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.EAST, BlockHalf.BOTTOM, StairShape.INNER_RIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, innerBottomModelId)
                                    )
                                    .register(
                                            Direction.WEST, BlockHalf.BOTTOM, StairShape.INNER_RIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, innerBottomModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.SOUTH, BlockHalf.BOTTOM, StairShape.INNER_RIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, innerBottomModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.NORTH, BlockHalf.BOTTOM, StairShape.INNER_RIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, innerBottomModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.EAST, BlockHalf.BOTTOM, StairShape.INNER_LEFT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, innerBottomModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.WEST, BlockHalf.BOTTOM, StairShape.INNER_LEFT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, innerBottomModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.SOUTH, BlockHalf.BOTTOM, StairShape.INNER_LEFT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, innerBottomModelId)
                                    )
                                    .register(
                                            Direction.NORTH, BlockHalf.BOTTOM, StairShape.INNER_LEFT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, innerBottomModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.EAST, BlockHalf.TOP, StairShape.STRAIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, regularTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.WEST, BlockHalf.TOP, StairShape.STRAIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, regularTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.SOUTH, BlockHalf.TOP, StairShape.STRAIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, regularTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.NORTH, BlockHalf.TOP, StairShape.STRAIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, regularTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.EAST, BlockHalf.TOP, StairShape.OUTER_RIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, outerTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.WEST, BlockHalf.TOP, StairShape.OUTER_RIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, outerTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.SOUTH, BlockHalf.TOP, StairShape.OUTER_RIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, outerTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.NORTH, BlockHalf.TOP, StairShape.OUTER_RIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, outerTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.EAST, BlockHalf.TOP, StairShape.OUTER_LEFT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, outerTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.WEST, BlockHalf.TOP, StairShape.OUTER_LEFT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, outerTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.SOUTH, BlockHalf.TOP, StairShape.OUTER_LEFT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, outerTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.NORTH, BlockHalf.TOP, StairShape.OUTER_LEFT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, outerTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.EAST, BlockHalf.TOP, StairShape.INNER_RIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, innerTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.WEST, BlockHalf.TOP, StairShape.INNER_RIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, innerTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.SOUTH, BlockHalf.TOP, StairShape.INNER_RIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, innerTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.NORTH, BlockHalf.TOP, StairShape.INNER_RIGHT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, innerTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.EAST, BlockHalf.TOP, StairShape.INNER_LEFT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, innerTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.WEST, BlockHalf.TOP, StairShape.INNER_LEFT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, innerTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.SOUTH, BlockHalf.TOP, StairShape.INNER_LEFT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, innerTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                                    .register(
                                            Direction.NORTH, BlockHalf.TOP, StairShape.INNER_LEFT,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, innerTopModelId)
                                                    .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                    .put(VariantSettings.UVLOCK, true)
                                    )
                    );
        }

        public static BlockStateSupplier createVerticalSlabBlockstate(
                Block vSlabBlock, Identifier vSlabBlockModelId, Identifier baseModelId
        ) {
            return VariantsBlockStateSupplier
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
                    );
        }

        public static BlockStateSupplier createVerticalStairsBlockstate(
                Block vStairsBlock,
                Identifier regularModelId, Identifier innerModelTopId, Identifier innerModelBottomId,
                Identifier outerModelRightBottomId, Identifier outerModelRightTopId,
                Identifier outerModelLeftBottomId, Identifier outerModelLeftTopId
        ) {
            return VariantsBlockStateSupplier.create(vStairsBlock)
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
                    );
        }

        // TODO (1.1) - Implement empty blockstate
        public static BlockStateSupplier createWindowBlockstate(
                Block windowBlock,
                Identifier windowModelId, Identifier emptyWindowModelId,
                Identifier verticalWindowModelId, Identifier verticalEmptyWindowModelId
        ) {
            return VariantsBlockStateSupplier
                    .create(windowBlock)
                    .coordinate(
                            BlockStateVariantMap
                                    .create(Properties.AXIS)
                                    .register(
                                            Direction.Axis.X,
                                            BlockStateVariant
                                                    .create()
                                                    .put(VariantSettings.MODEL, windowModelId)
                                    )
                                    .register(
                                            Direction.Axis.Y,
                                            BlockStateVariant
                                                    .create()
                                                    .put(VariantSettings.MODEL, verticalWindowModelId)
                                    )
                                    .register(
                                            Direction.Axis.Z,
                                            BlockStateVariant
                                                    .create()
                                                    .put(VariantSettings.MODEL, windowModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                    .put(VariantSettings.UVLOCK, Boolean.TRUE)
                                    )
                    );
        }

        // TODO (1.1) - Implement empty blockstate
        public static BlockStateSupplier createWindowSlabBlockstate(
                Block windowBlock,
                Identifier slabDoubleModel, Identifier slabDoubleEmptyModel,
                Identifier slabModel, Identifier emptySlabModel,
                Identifier slabTopModel, Identifier slabEmptyTopModel
        ) {
            return VariantsBlockStateSupplier
                    .create(windowBlock)
                    .coordinate(
                            BlockStateVariantMap
                                    .create(Properties.SLAB_TYPE)
                                    .register(
                                            SlabType.BOTTOM,
                                            BlockStateVariant
                                                    .create()
                                                    .put(VariantSettings.MODEL, slabModel)
                                    )
                                    .register(
                                            SlabType.TOP,
                                            BlockStateVariant
                                                    .create()
                                                    .put(VariantSettings.MODEL, slabTopModel)
                                    )
                                    .register(
                                            SlabType.DOUBLE,
                                            BlockStateVariant
                                                    .create()
                                                    .put(VariantSettings.MODEL, slabDoubleModel)
                                    )
                    );
        }

        // TODO (1.1) - Implement empty blockstate
        public static BlockStateSupplier createWindowVerticalSlabBlockstate(
                Block vSlabBlock,
                Identifier vSlabBlockModelId, Identifier vSlabBlockEmptyModelId,
                Identifier doubleSlabModelId, Identifier doubleSlabEmptyModelId
        ) {
            return VariantsBlockStateSupplier
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
                                                    .put(VariantSettings.MODEL, doubleSlabModelId)
                                    )
                                    .register(
                                            Direction.SOUTH, Boolean.TRUE,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, doubleSlabModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                    .put(VariantSettings.UVLOCK, Boolean.TRUE)
                                    )
                                    .register(Direction.WEST, Boolean.TRUE,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, doubleSlabModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                    .put(VariantSettings.UVLOCK, Boolean.TRUE)
                                    )
                                    .register(Direction.NORTH, Boolean.TRUE,
                                            BlockStateVariant.create()
                                                    .put(VariantSettings.MODEL, doubleSlabModelId)
                                                    .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                    .put(VariantSettings.UVLOCK, Boolean.TRUE)
                                    )
                    );
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

        public static TextureMap sideTopBottomWaterline(Block block) {
            String pattern = Registries.BLOCK.getId(block).getPath();

            Identifier topPatternId = new Identifier(
                    MOD_ID, pattern.replaceFirst("wl_", "")
            ).withPrefixedPath("block/");

            Identifier waterlineBaseId = new Identifier(MOD_ID, pattern).withPrefixedPath("block/");

            String bottomPath = getWaterlineBottomPath(topPatternId);

            Identifier blackPatternId = new Identifier(MOD_ID, bottomPath).withPrefixedPath("block/");

            return new TextureMap()
                    .put(TextureKey.SIDE, waterlineBaseId)
                    .put(TextureKey.TOP, topPatternId)
                    .put(TextureKey.BOTTOM, blackPatternId);
        }

        @NotNull
        private static String getWaterlineBottomPath(Identifier topPatternId) {
            String bottomPath = "black";
            if (topPatternId.getPath().contains("reinforced_armor")) {
                bottomPath += "_reinforced_armor";
            } else if (topPatternId.getPath().contains("light_armor")) {
                bottomPath += "_light_armor";
            } else if (topPatternId.getPath().contains("steel_armor")) {
                bottomPath += "_steel_armor";
            } else if (topPatternId.getPath().contains("composite_armor")) {
                bottomPath += "_composite_armor";
            }
            return bottomPath;
        }

        /**
         * If in the future better models need to be generated, the infrastructure is already in place here...
         */
        public static TextureMap window(Block block, String windowBaseTextureSuffix, String top, String bottom) {
            Identifier baseBlockTextureId = TextureMap.getId(block);
            Identifier windowTextureId = baseBlockTextureId.withSuffixedPath(windowBaseTextureSuffix);
            Identifier topTextureId = top.isEmpty()
                    ? baseBlockTextureId
                    : new Identifier(top).withPrefixedPath("block/");
            Identifier bottomTextureId = bottom.isEmpty()
                    ? baseBlockTextureId
                    : new Identifier(bottom).withPrefixedPath("block/");

            return new TextureMap()
                    .put(TextureKey.SIDE, baseBlockTextureId)
                    .put(TextureKey.TOP, topTextureId)
                    .put(TextureKey.BOTTOM, bottomTextureId)
                    .put(TextureKey.END, windowTextureId);
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {}
    }

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

            TagBuilder fenceBlocks = getTagBuilder(BlockTags.FENCES);
            TagBuilder wallBlocks = getTagBuilder(BlockTags.WALLS);

            for (BlockItem blockItem : REGISTERED) {
                pickaxeMineable.add(Registries.BLOCK.getId(blockItem.getBlock()));
                diamondTools.add(Registries.BLOCK.getId(blockItem.getBlock()));
                witherImmune.add(Registries.BLOCK.getId(blockItem.getBlock()));

                if (isFullBlock(blockItem.getBlock())) {
                    beaconBase.add(Registries.BLOCK.getId(blockItem.getBlock()));
                }

                String blockPath = Registries.BLOCK.getId(blockItem.getBlock()).getPath();

                String grade;

                if (blockPath.contains("light_armor")) grade = "light_armor";
                else if (blockPath.contains("steel_armor")) grade = "steel_armor";
                else if (blockPath.contains("composite_armor")) grade = "composite_armor";
                else grade = "reinforced_armor";

                // looks for vertical and horizontal too instead of just window in prep for full window block
                if (blockPath.contains("vertical_window") || blockPath.contains("horizontal_window"))
                    grade += "_window_slits";

                if (blockPath.contains("porthole"))
                    grade += "_porthole";

                if (blockPath.contains("slab"))
                    grade += "_slab";
                else if (blockPath.contains("stairs"))
                    grade += "_stairs";
                else if (blockPath.contains("fence"))
                    grade += "_fence";
                else if (blockPath.contains("wall"))
                    grade += "_wall";

                TagBuilder gradeTag = getTagBuilder(
                        TagKey.of(RegistryKeys.BLOCK, new Identifier(MOD_ID, grade))
                );

                gradeTag.add(Registries.BLOCK.getId(blockItem.getBlock()));

                if (isWall(blockItem.getBlock()))
                    wallBlocks.add(Registries.BLOCK.getId(blockItem.getBlock()));

                if (blockItem.getBlock() instanceof FenceBlock)
                    fenceBlocks.add(Registries.BLOCK.getId(blockItem.getBlock()));
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

    // TODO (2.0+) - rudimentary recipes
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

    public static class ValkyrienSkiesPropertyProvider implements DataProvider {
        public final DataOutput.PathResolver pathResolver;

        public ValkyrienSkiesPropertyProvider(FabricDataOutput output) {
            this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "vs_mass");
        }

        @Override
        public CompletableFuture<?> run(DataWriter writer) {
            return DataProvider.writeToPath(
                    writer,
                    properties(),
                    this.pathResolver.resolveJson(new Identifier("valkyrienskies", MOD_ID))
            );
        }

        public static JsonArray properties() {
            JsonArray toReturn = new JsonArray(REGISTERED.size());

            for (BlockItem blockItem : REGISTERED) {
                double frictionCoefficient = 0.2;
                int priority = 420;

                Block block = blockItem.getBlock();

                JsonObject propertyObject = new JsonObject();

                propertyObject.addProperty("block", Registries.BLOCK.getId(block).toString());
                propertyObject.addProperty("mass", getMass(block));
                propertyObject.addProperty("friction", frictionCoefficient);
                propertyObject.addProperty("priority", priority);

                toReturn.add(propertyObject);
            }

            return toReturn;
        }

        public static double getMass(Block block) {
            double reinforcedMass = 4312;
            double compositeMass = 2744;
            double steelMass = 1176;
            double lightMass = 392;

            String blockPath = Registries.BLOCK.getId(block).getPath();

            final double glassWeight = 200;
            double multiplier = 1;
            double glassMultiplier = 0;
            final double grade;

            if (blockPath.contains("light_armor")) grade = lightMass;
            else if (blockPath.contains("steel_armor")) grade = steelMass;
            else if (blockPath.contains("composite_armor")) grade = compositeMass;
            else grade = reinforcedMass;

            if (blockPath.contains("porthole")) {
                multiplier = 0.75;
                glassMultiplier = 0.25;
            }
            // looks for vertical and horizontal too instead of just window in prep for full window block
            else if (blockPath.contains("vertical_window") || blockPath.contains("horizontal_window")) {
                multiplier = 0.4375;
                glassMultiplier = 0.5625;
            }

            if (blockPath.contains("slab"))
                multiplier *= 0.5;
            else if (blockPath.contains("stairs"))
                multiplier *= 0.75;
            else if (blockPath.contains("fence"))
                multiplier = 0.0625;
            else if (blockPath.contains("wall"))
                multiplier = 0.25;

            return (grade * multiplier) + (glassWeight * glassMultiplier);
        }

        @Override
        public String getName() {
            return "VS2 Block Properties";
        }
    }
}
