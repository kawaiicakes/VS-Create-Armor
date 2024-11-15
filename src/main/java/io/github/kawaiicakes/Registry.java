package io.github.kawaiicakes;

import io.github.kawaiicakes.block.VerticalSlabBlock;
import io.github.kawaiicakes.block.VerticalStairsBlock;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static io.github.kawaiicakes.VSCreateArmor.MOD_ID;
import static net.minecraft.block.Blocks.NETHERITE_BLOCK;

public class Registry implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {

    }

    static void register() {
        final BlockItem[] initArr = init();

        net.minecraft.registry.Registry.register(
                Registries.ITEM_GROUP,
                new Identifier(MOD_ID, "vscarmor_group"),
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup.vscarmor_group"))
                        .icon(() -> initArr[0].getDefaultStack())
                        .entries(
                                (context, entries) -> {
                                    for (Item item : initArr) {
                                        entries.add(item);
                                    }
                                }
                        )
                        .build()
        );
    }

    private static BlockItem[] init() {
        List<BlockItem> toReturn = new ArrayList<>();

        toReturn.addAll(registerFullSeries("light_steel", 3.0F, 5.0F));
        toReturn.addAll(registerFullSeries("steel", 10.0F, 7.0F));
        toReturn.addAll(registerFullSeries("layered_steel", 28.0F, 8.0F));
        toReturn.addAll(registerFullSeries("reinforced_steel", 50.0F, 20.0F));

        return toReturn.toArray(BlockItem[]::new);
    }

    /*
        --HELPER METHODS BELOW--
     */

    private static List<BlockItem> registerFullSeries(String baseId, float hardness, float blastResistance) {
        final List<BlockItem> toReturn = new ArrayList<>(registerBlockSeries(baseId, hardness, blastResistance));

        for (String color : colors()) {
            // Eminem's been silent since this local dropped
            List<BlockItem> iterationCreation = registerBlockSeries(
                    color + "_" + baseId,
                    hardness,
                    blastResistance
            );

            toReturn.addAll(iterationCreation);
        }

        // TODO: special camo series + special colours here in the next commits

        return toReturn;
    }

    private static List<BlockItem> registerBlockSeries(String baseId, float hardness, float blastResistance) {
        final List<BlockItem> toReturn = new ArrayList<>();

        toReturn.add(registerBasic(baseId, hardness, blastResistance));
        toReturn.add(registerSlab(baseId, hardness, blastResistance));
        toReturn.add(registerVerticalSlab(baseId, hardness, blastResistance));
        toReturn.add(registerStairs(baseId, hardness, blastResistance));
        toReturn.add(registerVerticalStairs(baseId, hardness, blastResistance));

        return toReturn;
    }

    private static BlockItem registerBasic(String id, float hardness, float blastResistance) {
        final Block block = new Block(
                FabricBlockSettings.copyOf(NETHERITE_BLOCK)
                        .hardness(hardness)
                        .resistance(blastResistance)
        );

        net.minecraft.registry.Registry.register(
                Registries.BLOCK,
                new Identifier(MOD_ID, id),
                block
        );

        return net.minecraft.registry.Registry.register(
                Registries.ITEM,
                new Identifier(MOD_ID, id),
                new BlockItem(block, new FabricItemSettings())
        );
    }

    private static BlockItem registerSlab(String id, float hardness, float blastResistance) {
        final Block block = new SlabBlock(
                FabricBlockSettings.copyOf(NETHERITE_BLOCK)
                        .hardness(hardness)
                        .resistance(blastResistance)
        );

        net.minecraft.registry.Registry.register(
                Registries.BLOCK,
                new Identifier(MOD_ID, id + "_slab"),
                block
        );

        return net.minecraft.registry.Registry.register(
                Registries.ITEM,
                new Identifier(MOD_ID, id + "_slab"),
                new BlockItem(block, new FabricItemSettings())
        );
    }

    private static BlockItem registerVerticalSlab(String id, float hardness, float blastResistance) {
        final Block block = new VerticalSlabBlock(
                FabricBlockSettings.copyOf(NETHERITE_BLOCK)
                        .hardness(hardness)
                        .resistance(blastResistance)
        );

        net.minecraft.registry.Registry.register(
                Registries.BLOCK,
                new Identifier(MOD_ID, id + "_vertical_slab"),
                block
        );

        return net.minecraft.registry.Registry.register(
                Registries.ITEM,
                new Identifier(MOD_ID, id + "_vertical_slab"),
                new BlockItem(block, new FabricItemSettings())
        );
    }

    private static BlockItem registerStairs(String id, float hardness, float blastResistance) {
        final Block block = new StairsBlock(
                NETHERITE_BLOCK.getDefaultState(),
                FabricBlockSettings.copyOf(NETHERITE_BLOCK)
                        .hardness(hardness)
                        .resistance(blastResistance)
        );

        net.minecraft.registry.Registry.register(
                Registries.BLOCK,
                new Identifier(MOD_ID, id + "_stairs"),
                block
        );

        return net.minecraft.registry.Registry.register(
                Registries.ITEM,
                new Identifier(MOD_ID, id + "_stairs"),
                new BlockItem(block, new FabricItemSettings())
        );
    }

    private static BlockItem registerVerticalStairs(String id, float hardness, float blastResistance) {
        final Block block = new VerticalStairsBlock(
                NETHERITE_BLOCK.getDefaultState(),
                FabricBlockSettings.copyOf(NETHERITE_BLOCK)
                        .hardness(hardness)
                        .resistance(blastResistance)
        );

        net.minecraft.registry.Registry.register(
                Registries.BLOCK,
                new Identifier(MOD_ID, id + "_vertical_stairs"),
                block
        );

        return net.minecraft.registry.Registry.register(
                Registries.ITEM,
                new Identifier(MOD_ID, id + "_vertical_stairs"),
                new BlockItem(block, new FabricItemSettings())
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
                "pink"
        };
    }
}
