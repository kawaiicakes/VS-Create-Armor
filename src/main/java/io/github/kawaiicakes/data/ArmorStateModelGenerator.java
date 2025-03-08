package io.github.kawaiicakes.data;

import com.google.common.collect.ImmutableMap;
import io.github.kawaiicakes.block.VerticalSlabBlock;
import io.github.kawaiicakes.block.VerticalStairsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.HorizontalFacingBlock;
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
            .put(ArmorFamily.Variant.PORTHOLE, BlockStateModelGenerator.BlockTexturePool::vSCreateArmor$portholeBlock)
            .build();

    static BlockStateSupplier createVerticalSlabBlockState(Block slabBlock, Identifier slabId, Identifier full) {
        BlockStateVariant fullModel = BlockStateVariant.create().put(VariantSettings.MODEL, full);

        return VariantsBlockStateSupplier.create(slabBlock)
                .coordinate(
                        BlockStateVariantMap.DoubleProperty.create(HorizontalFacingBlock.FACING, VerticalSlabBlock.DOUBLET)
                                .register(Direction.SOUTH, Boolean.FALSE, BlockStateVariant.create().put(VariantSettings.MODEL, slabId))
                                .register(Direction.NORTH, Boolean.FALSE, BlockStateVariant.create()
                                        .put(VariantSettings.MODEL, slabId)
                                        .put(VariantSettings.UVLOCK, true)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                )
                                .register(Direction.EAST, Boolean.FALSE, BlockStateVariant.create()
                                        .put(VariantSettings.MODEL, slabId)
                                        .put(VariantSettings.UVLOCK, true)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                )
                                .register(Direction.WEST, Boolean.FALSE, BlockStateVariant.create()
                                        .put(VariantSettings.MODEL, slabId)
                                        .put(VariantSettings.UVLOCK, true)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                )
                                .register(Direction.SOUTH, Boolean.TRUE, fullModel)
                                .register(Direction.NORTH, Boolean.TRUE, fullModel)
                                .register(Direction.EAST, Boolean.TRUE, fullModel)
                                .register(Direction.WEST, Boolean.TRUE, fullModel)
                );
    }

    static BlockStateSupplier createVerticalStairsBlockState(
            Block stairsBlock,
            Identifier innerModelRightBottomId,
            Identifier regularModelRightId,
            Identifier outerModelRightBottomId,
            Identifier innerModelRightTopId,
            Identifier outerModelRightTopId,
            Identifier innerModelLeftBottomId,
            Identifier regularModelLeftId,
            Identifier outerModelLeftBottomId,
            Identifier innerModelLeftTopId,
            Identifier outerModelLeftTopId
    ) {
        return VariantsBlockStateSupplier.create(stairsBlock)
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
                                                .put(VariantSettings.MODEL, regularModelRightId)
                                )
                                .register(
                                        Direction.WEST,
                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                        VerticalStairsBlock.VerticalStairShape.STRAIGHT,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, regularModelRightId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                .put(VariantSettings.UVLOCK, true)
                                )
                                .register(
                                        Direction.SOUTH,
                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                        VerticalStairsBlock.VerticalStairShape.STRAIGHT,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, regularModelRightId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                .put(VariantSettings.UVLOCK, true)
                                )
                                .register(
                                        Direction.NORTH,
                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                        VerticalStairsBlock.VerticalStairShape.STRAIGHT,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, regularModelRightId)
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
                                                .put(VariantSettings.MODEL, innerModelRightBottomId)
                                )
                                .register(
                                        Direction.WEST,
                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                        VerticalStairsBlock.VerticalStairShape.INNER_BOTTOM,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, innerModelRightBottomId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                .put(VariantSettings.UVLOCK, true)
                                )
                                .register(
                                        Direction.SOUTH,
                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                        VerticalStairsBlock.VerticalStairShape.INNER_BOTTOM,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, innerModelRightBottomId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                .put(VariantSettings.UVLOCK, true)
                                )
                                .register(
                                        Direction.NORTH,
                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                        VerticalStairsBlock.VerticalStairShape.INNER_BOTTOM,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, innerModelRightBottomId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                .put(VariantSettings.UVLOCK, true)
                                )
                                .register(
                                        Direction.EAST,
                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                        VerticalStairsBlock.VerticalStairShape.INNER_TOP,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, innerModelRightTopId)
                                )
                                .register(
                                        Direction.WEST,
                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                        VerticalStairsBlock.VerticalStairShape.INNER_TOP,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, innerModelRightTopId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                .put(VariantSettings.UVLOCK, true)
                                )
                                .register(
                                        Direction.SOUTH,
                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                        VerticalStairsBlock.VerticalStairShape.INNER_TOP,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                .put(VariantSettings.MODEL, innerModelRightTopId)
                                                .put(VariantSettings.UVLOCK, true)
                                )
                                .register(
                                        Direction.NORTH,
                                        VerticalStairsBlock.BlockHalf.RIGHT,
                                        VerticalStairsBlock.VerticalStairShape.INNER_TOP,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, innerModelRightTopId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                .put(VariantSettings.UVLOCK, true)
                                )
                                .register(
                                        Direction.EAST,
                                        VerticalStairsBlock.BlockHalf.LEFT,
                                        VerticalStairsBlock.VerticalStairShape.STRAIGHT,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, regularModelLeftId)
                                )
                                .register(
                                        Direction.WEST,
                                        VerticalStairsBlock.BlockHalf.LEFT,
                                        VerticalStairsBlock.VerticalStairShape.STRAIGHT,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, regularModelLeftId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                .put(VariantSettings.UVLOCK, true)
                                )
                                .register(
                                        Direction.SOUTH,
                                        VerticalStairsBlock.BlockHalf.LEFT,
                                        VerticalStairsBlock.VerticalStairShape.STRAIGHT,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, regularModelLeftId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                .put(VariantSettings.UVLOCK, true)
                                )
                                .register(
                                        Direction.NORTH,
                                        VerticalStairsBlock.BlockHalf.LEFT,
                                        VerticalStairsBlock.VerticalStairShape.STRAIGHT,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, regularModelLeftId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
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
                                                .put(VariantSettings.MODEL, innerModelLeftBottomId)
                                )
                                .register(
                                        Direction.WEST,
                                        VerticalStairsBlock.BlockHalf.LEFT,
                                        VerticalStairsBlock.VerticalStairShape.INNER_BOTTOM,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, innerModelLeftBottomId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                .put(VariantSettings.UVLOCK, true)
                                )
                                .register(
                                        Direction.SOUTH,
                                        VerticalStairsBlock.BlockHalf.LEFT,
                                        VerticalStairsBlock.VerticalStairShape.INNER_BOTTOM,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, innerModelLeftBottomId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                .put(VariantSettings.UVLOCK, true)
                                )
                                .register(
                                        Direction.NORTH,
                                        VerticalStairsBlock.BlockHalf.LEFT,
                                        VerticalStairsBlock.VerticalStairShape.INNER_BOTTOM,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, innerModelLeftBottomId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                .put(VariantSettings.UVLOCK, true)
                                )
                                .register(
                                        Direction.EAST,
                                        VerticalStairsBlock.BlockHalf.LEFT,
                                        VerticalStairsBlock.VerticalStairShape.INNER_TOP,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, innerModelLeftTopId)
                                )
                                .register(
                                        Direction.WEST,
                                        VerticalStairsBlock.BlockHalf.LEFT,
                                        VerticalStairsBlock.VerticalStairShape.INNER_TOP,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, innerModelLeftTopId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                                .put(VariantSettings.UVLOCK, true)
                                )
                                .register(
                                        Direction.SOUTH,
                                        VerticalStairsBlock.BlockHalf.LEFT,
                                        VerticalStairsBlock.VerticalStairShape.INNER_TOP,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, innerModelLeftTopId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                                .put(VariantSettings.UVLOCK, true)
                                )
                                .register(
                                        Direction.NORTH,
                                        VerticalStairsBlock.BlockHalf.LEFT,
                                        VerticalStairsBlock.VerticalStairShape.INNER_TOP,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, innerModelLeftTopId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                                .put(VariantSettings.UVLOCK, true)
                                )
                );
    }

    static BlockStateSupplier createAxisRotatedWindow(
            Block block, Identifier modelId
    ) {
        return VariantsBlockStateSupplier.create(block)
                .coordinate(
                        BlockStateVariantMap.create(Properties.AXIS)
                                .register(
                                        Direction.Axis.Z,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, modelId)
                                )
                                .register(
                                        Direction.Axis.X,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, modelId)
                                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                )
                                .register(
                                        Direction.Axis.Y,
                                        BlockStateVariant.create()
                                                .put(VariantSettings.MODEL, modelId)
                                                .put(VariantSettings.X, VariantSettings.Rotation.R90)
                                )
                );
    }
}
