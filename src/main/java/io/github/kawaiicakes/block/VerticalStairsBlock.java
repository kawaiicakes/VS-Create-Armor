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

    // FIXME - these aren't the final shapes, only placeholders. in fact, I don't think I can even set it up with this method bc unlike top/bottom, left/right is a relative direction in-game...
    protected static final VoxelShape[] LEFT_SHAPES = composeShapes(
            TOP_SHAPE, BOTTOM_NORTH_WEST_CORNER_SHAPE, BOTTOM_NORTH_EAST_CORNER_SHAPE,
            BOTTOM_SOUTH_WEST_CORNER_SHAPE, BOTTOM_SOUTH_EAST_CORNER_SHAPE
    );
    // FIXME
    protected static final VoxelShape[] RIGHT_SHAPES = composeShapes(
            BOTTOM_SHAPE, TOP_NORTH_WEST_CORNER_SHAPE, TOP_NORTH_EAST_CORNER_SHAPE,
            TOP_SOUTH_WEST_CORNER_SHAPE, TOP_SOUTH_EAST_CORNER_SHAPE
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
        return (state.get(HALF) == BlockHalf.RIGHT ? RIGHT_SHAPES : LEFT_SHAPES)
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
