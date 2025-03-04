package io.github.kawaiicakes;

import io.github.kawaiicakes.block.VerticalSlabBlock;
import io.github.kawaiicakes.block.VerticalStairsBlock;
import io.github.kawaiicakes.data.ArmorFamily;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.data.client.*;
import net.minecraft.data.family.BlockFamily;
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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.text.WordUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static io.github.kawaiicakes.VSCreateArmor.MOD_ID;
import static net.minecraft.block.Blocks.NETHERITE_BLOCK;

public class Registry implements DataGeneratorEntrypoint {
    static List<BlockItem> REGISTERED = new ArrayList<>();
    static final Map<Block, BlockFamily> BLOCK_FAMILIES = new HashMap<>();
    static final Map<Block, BlockFamily> WATERLINE_BLOCK_FAMILIES = new HashMap<>();

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
        registerArmorBlockFamily("light_armor", 3.0F, 5.0F);
        registerArmorBlockFamily("steel_armor", 10.0F, 7.0F);
        registerArmorBlockFamily("composite_armor", 28.0F, 8.0F);
        registerArmorBlockFamily("reinforced_armor", 50.0F, 20.0F);

        for (String color : colors()) {
            registerArmorBlockFamily(color + "_" + "light_armor", 3.0F, 5.0F);
            registerArmorBlockFamily(color + "_" + "steel_armor", 10.0F, 7.0F);
            registerArmorBlockFamily(color + "_" + "composite_armor", 28.0F, 8.0F);
            registerArmorBlockFamily(color + "_" + "reinforced_armor", 50.0F, 20.0F);
        }
    }

    // TODO - Add commented colours + patterns.
    private static String[] colors() {
        return new String[] {
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

        registerBlockWithItem(id, baseBlock);
        registerBlockWithItem(id + "_slab", slabBlock);
        registerBlockWithItem(id + "_vertical_slab", verticalSlabBlock);
        registerBlockWithItem(id + "_stairs", stairsBlock);
        registerBlockWithItem(id + "_vertical_stairs", verticalStairsBlock);

        registerBlockWithItem("wl_" + id, wlBaseBlock);
        registerBlockWithItem("wl_" + id + "_slab", wlSlabBlock);
        registerBlockWithItem("wl_" + id + "_vertical_slab", wlVerticalSlabBlock);
        registerBlockWithItem("wl_" + id + "_stairs", wlStairsBlock);
        registerBlockWithItem("wl_" + id + "_vertical_stairs", wlVerticalStairsBlock);

        BLOCK_FAMILIES.put(
                baseBlock,
                new ArmorFamily.Builder(baseBlock)
                        .slab(slabBlock)
                        .verticalSlab(verticalSlabBlock)
                        .stairs(stairsBlock)
                        .verticalStairs(verticalStairsBlock)
                        .build()
        );

        WATERLINE_BLOCK_FAMILIES.put(
                wlBaseBlock,
                new ArmorFamily.Builder(wlBaseBlock)
                        .slab(wlSlabBlock)
                        .verticalSlab(wlVerticalSlabBlock)
                        .stairs(wlStairsBlock)
                        .verticalStairs(wlVerticalStairsBlock)
                        .build()
        );
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

    // TODO - new model for steel armour cubes
    // TODO - Waterline Black shouldn't exist
    // TODO - Fix flipped top/bottom textures for waterline vertical stair states
    private static class VSCArmorModelProvider extends FabricModelProvider {
        public static final TexturedModel.Factory WATERLINE_CUBE
                = TexturedModel.makeFactory(VSCArmorModelProvider::waterlineCube, Models.CUBE_BOTTOM_TOP);

        public static TextureMap waterlineCube(Block block) {
            Identifier blockId = Registries.BLOCK.getId(block);

            String bottomPath = "black";
            if (blockId.getPath().contains("reinforced_armor")) {
                bottomPath += "_reinforced_armor";
            } else if (blockId.getPath().contains("light_armor")) {
                bottomPath += "_light_armor";
            } else if (blockId.getPath().contains("steel_armor")) {
                bottomPath += "_steel_armor";
            } else if (blockId.getPath().contains("composite_armor")) {
                bottomPath += "_composite_armor";
            }

            Identifier bottom = new Identifier(blockId.getNamespace(), bottomPath);

            Identifier top = new Identifier(
                    blockId.getNamespace(),
                    blockId.getPath().replace("wl_", "")
            );

            return new TextureMap()
                    .put(TextureKey.SIDE, TextureMap.getId(block))
                    .put(TextureKey.TOP, top.withPrefixedPath("block/"))
                    .put(TextureKey.BOTTOM, bottom.withPrefixedPath("block/"));
        }

        public VSCArmorModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            for (Map.Entry<Block, BlockFamily> familyEntry : BLOCK_FAMILIES.entrySet()) {
                blockStateModelGenerator.registerCubeAllModelTexturePool(familyEntry.getKey())
                        .family(familyEntry.getValue());
            }

            for (Map.Entry<Block, BlockFamily> familyEntry : WATERLINE_BLOCK_FAMILIES.entrySet()) {
                TexturedModel baseModel = WATERLINE_CUBE.get(familyEntry.getKey());

                blockStateModelGenerator.new BlockTexturePool(baseModel.getTextures())
                        .base(familyEntry.getKey(), baseModel.getModel())
                        .family(familyEntry.getValue());
            }
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
            TagBuilder light
                    = getTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier(MOD_ID, "light_armor")));
            TagBuilder steel
                    = getTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier(MOD_ID, "steel_armor")));
            TagBuilder composite
                    = getTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier(MOD_ID, "composite_armor")));
            TagBuilder reinforced
                    = getTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier(MOD_ID, "reinforced_armor")));

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
            }
        }

        public static boolean isFullBlock(Block block) {
            return !(block instanceof SlabBlock)
                    && !(block instanceof VerticalSlabBlock)
                    && !(block instanceof StairsBlock)
                    && !(block instanceof VerticalStairsBlock);
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
