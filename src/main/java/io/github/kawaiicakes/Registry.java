package io.github.kawaiicakes;

import io.github.kawaiicakes.block.VerticalSlabBlock;
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
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagBuilder;
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
        registerArmorBlockFamily("light_steel", 3.0F, 5.0F);
        registerArmorBlockFamily("steel", 10.0F, 7.0F);
        registerArmorBlockFamily("layered_steel", 28.0F, 8.0F);
        registerArmorBlockFamily("reinforced_steel", 50.0F, 20.0F);

        for (String color : colors()) {
            registerArmorBlockFamily(color + "_" + "light_steel", 3.0F, 5.0F);
            registerArmorBlockFamily(color + "_" + "steel", 10.0F, 7.0F);
            registerArmorBlockFamily(color + "_" + "layered_steel", 28.0F, 8.0F);
            registerArmorBlockFamily(color + "_" + "reinforced_steel", 50.0F, 20.0F);
        }

        // TODO: add custom patterns prn
    }

    private static void registerArmorBlockFamily(String id, float hardness, float blastResistance) {
        final Block baseBlock = new Block(
                FabricBlockSettings.copyOf(NETHERITE_BLOCK)
                        .hardness(hardness)
                        .resistance(blastResistance)
        );

        final SlabBlock slabBlock = new SlabBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.75F)
                        .resistance(blastResistance * 0.75F)
        );

        final VerticalSlabBlock verticalSlabBlock = new VerticalSlabBlock(
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.75F)
                        .resistance(blastResistance * 0.75F)
        );

        final StairsBlock stairsBlock = new StairsBlock(
                baseBlock.getDefaultState(),
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.875F)
                        .resistance(blastResistance * 0.875F)
        );

        /*
        final VerticalStairsBlock verticalStairsBlock = new VerticalStairsBlock(
                baseBlock.getDefaultState(),
                FabricBlockSettings.copyOf(baseBlock)
                        .hardness(hardness * 0.875F)
                        .resistance(blastResistance * 0.875F)
        );
         */

        registerBlockWithItem(id, baseBlock);
        registerBlockWithItem(id + "_slab", slabBlock);
        registerBlockWithItem(id + "_vertical_slab", verticalSlabBlock);
        registerBlockWithItem(id + "_stairs", stairsBlock);
        // registerBlockWithItem(id + "_vertical_stairs", verticalStairsBlock);

        BLOCK_FAMILIES.put(
                baseBlock,
                new ArmorFamily.Builder(baseBlock)
                        .slab(slabBlock)
                        .verticalSlab(verticalSlabBlock)
                        .stairs(stairsBlock)
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
                "4bo",
                "29",
                "31",
                "32",
                "33",
                "gelb",
                "panzergrau",
                "parade",
                "rotbraun",
                "ship_lower",
                "camo_desert",
                "camo_forest",
                "camo_jungle",
                "camo_mesa",
                "camo_plains",
                "camo_snow",
                "camo_swamp",
                "camo_taiga"
        };
    }

    private static class VSCArmorBlockLootTables extends FabricBlockLootTableProvider {
        public VSCArmorBlockLootTables(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generate() {
            for (BlockItem blockItem : REGISTERED) {
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
            // TODO: BlockFamily subclass w/ access wideners to allow generation of vertical stuff
            for (Map.Entry<Block, BlockFamily> familyEntry : BLOCK_FAMILIES.entrySet()) {
                blockStateModelGenerator.registerCubeAllModelTexturePool(familyEntry.getKey())
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
            TagBuilder builder = getTagBuilder(BlockTags.PICKAXE_MINEABLE);

            for (BlockItem blockItem : REGISTERED) {
                builder.add(Registries.BLOCK.getId(blockItem.getBlock()));
            }
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

        @SuppressWarnings("deprecation")
        private static String sanitizeName(String name) {
            return WordUtils.capitalize(
                    name.replace("block.vscarmor.", "").replace("_", " ")
            );
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
