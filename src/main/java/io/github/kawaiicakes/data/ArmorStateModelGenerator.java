package io.github.kawaiicakes.data;

import com.google.common.collect.ImmutableMap;
import io.github.kawaiicakes.block.VerticalSlabBlock;
import net.minecraft.block.Block;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Map;
import java.util.function.BiConsumer;

public interface ArmorStateModelGenerator {
    Map<ArmorFamily.Variant, BiConsumer<BlockStateModelGenerator.BlockTexturePool, Block>> ARMOR_VARIANT_POOL_FUNCTIONS
            = ImmutableMap.<ArmorFamily.Variant, BiConsumer<BlockStateModelGenerator.BlockTexturePool, Block>>builder()
            .put(ArmorFamily.Variant.VERTICAL_SLAB, BlockStateModelGenerator.BlockTexturePool::vSCreateArmor$verticalSlab)
            .put(ArmorFamily.Variant.VERTICAL_STAIRS, BlockStateModelGenerator.BlockTexturePool::vSCreateArmor$verticalStairs)
            .build();

    static BlockStateSupplier createVerticalSlabBlockState(Block slabBlock, Identifier south, Identifier north, Identifier east, Identifier west, Identifier full) {
        BlockStateVariant fullModel = BlockStateVariant.create().put(VariantSettings.MODEL, full);

        return VariantsBlockStateSupplier.create(slabBlock)
                .coordinate(
                        BlockStateVariantMap.DoubleProperty.create(HorizontalFacingBlock.FACING, VerticalSlabBlock.DOUBLET)
                                .register(Direction.SOUTH, Boolean.FALSE, BlockStateVariant.create().put(VariantSettings.MODEL, south))
                                .register(Direction.NORTH, Boolean.FALSE, BlockStateVariant.create().put(VariantSettings.MODEL, north))
                                .register(Direction.EAST, Boolean.FALSE, BlockStateVariant.create().put(VariantSettings.MODEL, east))
                                .register(Direction.WEST, Boolean.FALSE, BlockStateVariant.create().put(VariantSettings.MODEL, west))
                                .register(Direction.SOUTH, Boolean.TRUE, fullModel)
                                .register(Direction.NORTH, Boolean.TRUE, fullModel)
                                .register(Direction.EAST, Boolean.TRUE, fullModel)
                                .register(Direction.WEST, Boolean.TRUE, fullModel)
                );
    }
}
