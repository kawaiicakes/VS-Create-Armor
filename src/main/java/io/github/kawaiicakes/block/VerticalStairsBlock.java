package io.github.kawaiicakes.block;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class VerticalStairsBlock extends StairsBlock implements Waterloggable {
    public static final EnumProperty<BlockHalf> HALF = EnumProperty.of("half", BlockHalf.class);
    public static final EnumProperty<VerticalStairShape> V_SHAPE
            = EnumProperty.of("shape", VerticalStairShape.class);

    /*
        // Superclass code work-through...

        // { x∈N | x >= 0, x < 16 }

        // (x & 1) != 0;
        // true if x is odd

        // (x & 2) != 0;
        // true if x falls in the sequence 2, 3, 6, 7, 10, 11, 14, 15

        // (x & 4) != 0;
        // true if x falls in the sequence 4, 5, 6, 7, 12, 13, 14, 15

        // (x & 8) != 0;
        // true if x falls in the sequence 8, 9, 10, 11, 12, 13, 14, 15

        // every single evaluation of x in range will be true for at least 8 values...
        // each model part is either present or not based on the different evaluations of x
        // of the 16 values of x, there are 8 values where a model part is present and 8 absent
        // therefore, the code is returning the set of all possible combinations of model parts for the arg base passed

        // (state.get(FACING)).getHorizontal();
        // 0 when south, 1 when west, 2 when north, 3 when east

        // (state.get(SHAPE)).ordinal() * 4
        // 0 when straight, 4 when inner left, 8 when inner right, 12 when outer left, 16 when outer right

        // (state.get(SHAPE)).ordinal() * 4 + (state.get(FACING)).getHorizontal();
        // It appears that this effectively maps every possible combination of shapes & directions to 20 integers (0 to 19).
        // This includes states that are different without visual distinction.
        // Every increment of 4 starting from 0 "switches" the shape used.

        // Each state's integer is matched to the index of its corresponding generated VoxelShape in SHAPE_INDICES.

        // There are 12 visually unique shapes, but 20 blockstates. Each of the 20 blockstates possesses one of the 12.
        // It should be clarified here that this is per block half. 12 unique shapes for 20 blockstates for top & bottom.

        going back to #composeShape...

        0 - base only (invalid)
        1 - base + NW
        2 - base + NE
        3 - base + NW + NE
        4 - base + SW
        5 - base + NW + SW
        6 - base + NE + SW (invalid)
        7 - base + NW + NE + SW
        8 - base + SE
        9 - base + NW + SE (invalid)
        10 - base + NE + SE
        11 - base + NW + NE + SE
        12 - base + SW + SE
        13 - base + NW + SW + SE
        14 - base + NE + SW + SE
        15 - base + NW + NE + SW + SE (invalid)

        for the vertical stairs the same premise exists, but is split between left & right rather than top & bottom.
        Right and left are defined relative to the direction of the base and later expressed in absolute terms.
        0 - base only (invalid)
        1 - base + bottom right (inner bottom right)
        2 - base + top right (inner top right)
        3 - base + bottom right + top right (straight right)
        4 - base + top left (inner top left)
        5 - base + bottom right + top left (invalid)
        6 - base + top right + top left (invalid)
        7 - base + bottom right + top right + top left (outer top right)
        8 - base + bottom left (inner bottom left)
        9 - base + bottom right + bottom left (invalid)
        10 - base + top right + bottom left (invalid)
        11 - base + bottom right + top right + bottom left (outer bottom right)
        12 - base + top left + bottom left (straight left)
        13 - base + bottom right + top left + bottom left (outer bottom left)
        14 - base + top right + top left + bottom left (outer top left)
        15 - base + bottom right + top right + top left + bottom left (invalid)

        TODO continue this

        Shape indices for vertical stairs are now defined as the set of all combinations of shape with right/left.
        0 - straight south
        1 - straight west
        2 - straight north
        3 - straight east
     */

    // TODO
    public static final int[] RIGHT_INDICES = new int[] {
            12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8
    };

    public static final int[] LEFT_INDICES = new int[] {
            12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8
    };

    protected static final VoxelShape[] NORTH_SHAPES = composeShapes(
            VerticalSlabBlock.NORTH, BOTTOM_SOUTH_EAST_CORNER_SHAPE, TOP_SOUTH_EAST_CORNER_SHAPE,
            BOTTOM_SOUTH_WEST_CORNER_SHAPE, TOP_SOUTH_WEST_CORNER_SHAPE
    );

    protected static final VoxelShape[] EAST_SHAPES = composeShapes(
            VerticalSlabBlock.EAST, BOTTOM_NORTH_WEST_CORNER_SHAPE, TOP_NORTH_WEST_CORNER_SHAPE,
            BOTTOM_SOUTH_WEST_CORNER_SHAPE, TOP_SOUTH_WEST_CORNER_SHAPE
    );

    protected static final VoxelShape[] SOUTH_SHAPES = composeShapes(
            VerticalSlabBlock.SOUTH, BOTTOM_NORTH_WEST_CORNER_SHAPE, TOP_NORTH_WEST_CORNER_SHAPE,
            BOTTOM_NORTH_EAST_CORNER_SHAPE, TOP_NORTH_EAST_CORNER_SHAPE
    );

    protected static final VoxelShape[] WEST_SHAPES = composeShapes(
            VerticalSlabBlock.WEST, BOTTOM_NORTH_EAST_CORNER_SHAPE, TOP_NORTH_EAST_CORNER_SHAPE,
            BOTTOM_SOUTH_EAST_CORNER_SHAPE, TOP_SOUTH_EAST_CORNER_SHAPE
    );

    public VerticalStairsBlock(BlockState baseBlockState, AbstractBlock.Settings settings) {
        super(baseBlockState, settings);
        this.setDefaultState(
                this.stateManager
                        .getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(HALF, BlockHalf.RIGHT)
                        .with(V_SHAPE, VerticalStairShape.STRAIGHT)
                        .with(WATERLOGGED, Boolean.FALSE)
        );
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // FIXME
        return (state.get(HALF) == BlockHalf.RIGHT ? WEST_SHAPES : EAST_SHAPES)
                [SHAPE_INDICES[this.getShapeIndexIndex(state)]];
    }

    private int getShapeIndexIndex(BlockState state) {
        return state.get(V_SHAPE).ordinal() * 4 + state.get(FACING).getHorizontal();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
        BlockState blockState = this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing())
                .with(
                        HALF,
                        // FIXME
                        direction != Direction.DOWN
                                && (direction == Direction.UP || !(ctx.getHitPos().y - (double)blockPos.getY() > 0.5))
                                ? BlockHalf.RIGHT
                                : BlockHalf.LEFT
                )
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
        return blockState.with(V_SHAPE, getVerticalStairShape(blockState, ctx.getWorld(), blockPos));
    }

    @Override
    public BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState,
            WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return direction.getAxis().isHorizontal()
                ? state.with(V_SHAPE, getVerticalStairShape(state, world, pos))
                : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private static VerticalStairShape getVerticalStairShape(BlockState state, BlockView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockState blockState = world.getBlockState(pos.offset(direction));
        if (isStairs(blockState) && state.get(HALF) == blockState.get(HALF)) {
            Direction direction2 = blockState.get(FACING);
            if (
                    direction2.getAxis() != state.get(FACING).getAxis()
                            && isDifferentOrientation(state, world, pos, direction2.getOpposite())
            ) {
                if (direction2 == direction.rotateYCounterclockwise()) {
                    return VerticalStairShape.OUTER_TOP;
                }

                return VerticalStairShape.OUTER_BOTTOM;
            }
        }

        BlockState blockState2 = world.getBlockState(pos.offset(direction.getOpposite()));
        if (isStairs(blockState2) && state.get(HALF) == blockState2.get(HALF)) {
            Direction direction3 = blockState2.get(FACING);
            if (
                    direction3.getAxis() != state.get(FACING).getAxis()
                            && isDifferentOrientation(state, world, pos, direction3)
            ) {
                if (direction3 == direction.rotateYCounterclockwise()) {
                    return VerticalStairShape.INNER_TOP;
                }

                return VerticalStairShape.INNER_BOTTOM;
            }
        }

        return VerticalStairShape.STRAIGHT;
    }

    private static boolean isDifferentOrientation(BlockState state, BlockView world, BlockPos pos, Direction dir) {
        BlockState blockState = world.getBlockState(pos.offset(dir));
        return !isStairs(blockState) || blockState.get(FACING) != state.get(FACING) || blockState.get(HALF) != state.get(HALF);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, V_SHAPE, WATERLOGGED);
    }

    public enum BlockHalf implements StringIdentifiable {
        LEFT("left"),
        RIGHT("right");

        private final String name;

        BlockHalf(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }

    public enum VerticalStairShape implements StringIdentifiable {
        STRAIGHT("straight"),
        INNER_TOP("inner_top"),
        INNER_BOTTOM("inner_bottom"),
        OUTER_TOP("outer_top"),
        OUTER_BOTTOM("outer_bottom");

        private final String name;

        VerticalStairShape(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}
