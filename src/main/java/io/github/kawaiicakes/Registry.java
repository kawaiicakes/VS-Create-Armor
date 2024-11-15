package io.github.kawaiicakes;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
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

        toReturn.addAll(registerBlockSeries("steel_sheet", 3.0F, 5.0F));
        toReturn.addAll(registerBlockSeries("steel", 10.0F, 7.0F));
        toReturn.addAll(registerBlockSeries("layered_steel", 28.0F, 8.0F));
        toReturn.addAll(registerBlockSeries("reinforced_steel", 50.0F, 20.0F));

        return toReturn.toArray(BlockItem[]::new);
    }

    /*
        --HELPER METHODS BELOW--
     */

    private static List<BlockItem> registerBlockSeries(String baseId, float hardness, float blastResistance) {
        final List<BlockItem> toReturn = new ArrayList<>();

        BlockItem defaultSteel = registerBasic(baseId, hardness, blastResistance);
        toReturn.add(defaultSteel);
        toReturn.addAll(registerColorSeries(baseId, defaultSteel));
        BlockItem defaultSteelSlab = registerSlab(baseId + "_slab", hardness, blastResistance);
        toReturn.add(defaultSteelSlab);
        toReturn.addAll(registerColorSeries(baseId + "_slab", defaultSteelSlab));
        BlockItem defaultSteelStairs = registerStairs(baseId + "_stairs", hardness, blastResistance);
        toReturn.add(defaultSteelStairs);
        toReturn.addAll(registerColorSeries(baseId + "_stairs", defaultSteelStairs));

        return toReturn;
    }

    private static List<BlockItem> registerColorSeries(String baseId, BlockItem base) {
        final List<BlockItem> toReturn = new ArrayList<>();

        final Block block = base.getBlock();

        for (String color : colors()) {
            net.minecraft.registry.Registry.register(
                    Registries.BLOCK,
                    new Identifier(MOD_ID, color + "_" + baseId),
                    block
            );

            // Eminem's been silent since this local dropped
            BlockItem iterationCreation = net.minecraft.registry.Registry.register(
                    Registries.ITEM,
                    new Identifier(MOD_ID, color + "_" + baseId),
                    new BlockItem(block, new FabricItemSettings())
            );

            toReturn.add(iterationCreation);
        }

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

    // TODO
    private static BlockItem registerSlab(String id, float hardness, float blastResistance) {
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

    // TODO
    private static BlockItem registerStairs(String id, float hardness, float blastResistance) {
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
