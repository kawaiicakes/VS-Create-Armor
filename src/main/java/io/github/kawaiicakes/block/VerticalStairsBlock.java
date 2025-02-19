package io.github.kawaiicakes.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;

import static net.minecraft.block.HorizontalFacingBlock.FACING;
import static net.minecraft.block.StairsBlock.*;
import static net.minecraft.state.property.Properties.WATERLOGGED;

@SuppressWarnings("deprecation")
public class VerticalStairsBlock extends Block implements Waterloggable {
    public static final EnumProperty<BlockHalf> HALF = EnumProperty.of("half", BlockHalf.class);
    public static final EnumProperty<VerticalStairShape> V_SHAPE
            = EnumProperty.of("shape", VerticalStairShape.class);

    public static final int[] RIGHT_INDICES = new int[] {
            3, 7, 11, 2, 1
    };

    public static final int[] LEFT_INDICES = new int[] {
            12, 14, 13, 1, 8
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

    private final Block baseBlock;
    private final BlockState baseBlockState;

    public VerticalStairsBlock(BlockState baseBlockState, Settings settings) {
        super(settings);
        this.setDefaultState(
                this.stateManager
                        .getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(HALF, BlockHalf.RIGHT)
                        .with(V_SHAPE, VerticalStairShape.STRAIGHT)
                        .with(WATERLOGGED, Boolean.FALSE)
        );
        this.baseBlock = baseBlockState.getBlock();
        this.baseBlockState = baseBlockState;
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int[] indices = state.get(HALF) == BlockHalf.RIGHT ? RIGHT_INDICES : LEFT_INDICES;
        VoxelShape[] directionShapes = switch (state.get(FACING)) {
            case NORTH -> NORTH_SHAPES;
            case SOUTH -> SOUTH_SHAPES;
            case WEST -> WEST_SHAPES;
            default -> EAST_SHAPES;
        };

        return directionShapes[indices[state.get(V_SHAPE).ordinal()]];
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        this.baseBlock.randomDisplayTick(state, world, pos, random);
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        this.baseBlockState.onBlockBreakStart(world, pos, player);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        this.baseBlock.onBroken(world, pos, state);
    }

    @Override
    public float getBlastResistance() {
        return this.baseBlock.getBlastResistance();
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!state.isOf(state.getBlock())) {
            world.updateNeighbor(this.baseBlockState, pos, Blocks.AIR, pos, false);
            this.baseBlock.onBlockAdded(this.baseBlockState, world, pos, oldState, false);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            this.baseBlockState.onStateReplaced(world, pos, newState, moved);
        }
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        this.baseBlock.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return this.baseBlock.hasRandomTicks(state);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.baseBlock.randomTick(state, world, pos, random);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.baseBlock.scheduledTick(state, world, pos, random);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return this.baseBlockState.onUse(world, player, hand, hit);
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        this.baseBlock.onDestroyedByExplosion(world, pos, explosion);
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

    public static boolean isStairs(BlockState state) {
        return state.getBlock() instanceof VerticalStairsBlock;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    // FIXME
    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        Direction direction = state.get(FACING);
        VerticalStairShape stairShape = state.get(V_SHAPE);
        switch (mirror) {
            case LEFT_RIGHT:
                if (direction.getAxis() == Direction.Axis.Z) {
                    return switch (stairShape) {
                        default -> state.rotate(BlockRotation.CLOCKWISE_180);
                    };
                }
                break;
            case FRONT_BACK:
                if (direction.getAxis() == Direction.Axis.X) {
                    switch (stairShape) {
                        default: return state.rotate(BlockRotation.CLOCKWISE_180);
                    }
                }
        }

        return super.mirror(state, mirror);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, V_SHAPE, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
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
