package io.github.kawaiicakes.block;

import net.minecraft.block.*;
import net.minecraft.block.enums.StairShape;
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
            3, 11, 7, 2, 1
    };

    public static final int[] LEFT_INDICES = new int[] {
            12, 14, 13, 8, 4
    };

    protected static final VoxelShape[] NORTH_SHAPES = composeShapes(
            VerticalSlabBlock.NORTH, BOTTOM_SOUTH_EAST_CORNER_SHAPE, TOP_SOUTH_EAST_CORNER_SHAPE,
            BOTTOM_SOUTH_WEST_CORNER_SHAPE, TOP_SOUTH_WEST_CORNER_SHAPE
    );

    protected static final VoxelShape[] EAST_SHAPES = composeShapes(
            VerticalSlabBlock.EAST, BOTTOM_SOUTH_WEST_CORNER_SHAPE, TOP_SOUTH_WEST_CORNER_SHAPE,
            BOTTOM_NORTH_WEST_CORNER_SHAPE, TOP_NORTH_WEST_CORNER_SHAPE
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
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);

        Direction playerFacing = ctx.getHorizontalPlayerFacing();

        boolean isRight = switch (playerFacing) {
            case NORTH -> (ctx.getHitPos().x - (double) blockPos.getX()) >= 0.5;
            case SOUTH -> (ctx.getHitPos().x - (double) blockPos.getX()) <= 0.5;
            case WEST -> (ctx.getHitPos().z - (double) blockPos.getZ()) <= 0.5;
            default -> (ctx.getHitPos().z - (double) blockPos.getZ()) >= 0.5;
        };

        BlockState toReturn = this.getDefaultState()
                .with(FACING, playerFacing)
                .with(HALF, isRight ? BlockHalf.RIGHT : BlockHalf.LEFT)
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);

        return toReturn.with(V_SHAPE, getVerticalStairShape(toReturn, ctx.getWorld(), blockPos));
    }

    @Override
    public BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState,
            WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        Direction originalFacing = state.get(FACING);

        return !direction.getAxis().equals(originalFacing.getAxis())
                ? state.with(V_SHAPE, getVerticalStairShape(state, world, pos))
                : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private static VerticalStairShape getVerticalStairShape(BlockState placedState, BlockView world, BlockPos placedPos) {
        VerticalStairShape defaultReturn = VerticalStairShape.STRAIGHT;

        Direction placedFacing = placedState.get(FACING);

        BlockState rightState = world.getBlockState(placedPos.offset(placedFacing.rotateYClockwise()));
        BlockState leftState = world.getBlockState(placedPos.offset(placedFacing.rotateYCounterclockwise()));

        if (!isStairs(rightState) && !isStairs(leftState))
            return defaultReturn;

        BlockState aboveState = world.getBlockState(placedPos.offset(Direction.UP));
        BlockState belowState = world.getBlockState(placedPos.offset(Direction.DOWN));

        BlockHalf placedHalf = placedState.get(HALF);

        boolean aboveForcesStraight = isVStairs(aboveState)
                && aboveState.get(HALF).equals(placedHalf)
                && aboveState.get(FACING).equals(placedFacing)
                && isForcingShape(aboveState.get(V_SHAPE));

        // cut calculation early if above already forces straight
        boolean belowForcesStraight = !aboveForcesStraight
                && isVStairs(belowState)
                && belowState.get(HALF).equals(placedHalf)
                && belowState.get(FACING).equals(placedFacing)
                && isForcingShape(belowState.get(V_SHAPE));

        if (aboveForcesStraight || belowForcesStraight) return defaultReturn;

        if (placedHalf.equals(BlockHalf.RIGHT)) {
            if (shapeIsCongruent(rightState, BlockHalf.RIGHT, placedFacing)) {
                return rightState.get(StairsBlock.HALF).equals(net.minecraft.block.enums.BlockHalf.TOP)
                        ? VerticalStairShape.OUTER_TOP
                        : VerticalStairShape.OUTER_BOTTOM;
            } else if (shapeIsCongruent(leftState, BlockHalf.LEFT, placedFacing)) {
                return leftState.get(StairsBlock.HALF).equals(net.minecraft.block.enums.BlockHalf.TOP)
                        ? VerticalStairShape.INNER_TOP
                        : VerticalStairShape.INNER_BOTTOM;
            }
        } else {
            if (shapeIsCongruent(leftState, BlockHalf.LEFT, placedFacing)) {
                return leftState.get(StairsBlock.HALF).equals(net.minecraft.block.enums.BlockHalf.TOP)
                        ? VerticalStairShape.OUTER_TOP
                        : VerticalStairShape.OUTER_BOTTOM;
            } else if (shapeIsCongruent(rightState, BlockHalf.RIGHT, placedFacing)) {
                return rightState.get(StairsBlock.HALF).equals(net.minecraft.block.enums.BlockHalf.TOP)
                        ? VerticalStairShape.INNER_TOP
                        : VerticalStairShape.INNER_BOTTOM;
            }
        }

        return defaultReturn;
    }

    public static boolean shapeIsCongruent(BlockState stairs, BlockHalf half, Direction horizontal) {
        if (!isStairs(stairs)) return false;
        if (!stairs.get(FACING).equals(horizontal)) return false;

        StairShape shape = stairs.get(SHAPE);

        if (shape.equals(StairShape.STRAIGHT)) return true;

        if (half.equals(BlockHalf.RIGHT)) {
            return shape.equals(StairShape.OUTER_LEFT) || shape.equals(StairShape.INNER_RIGHT);
        } else {
            return shape.equals(StairShape.OUTER_RIGHT) || shape.equals(StairShape.INNER_LEFT);
        }
    }

    public static boolean isForcingShape(VerticalStairShape shape) {
        return shape.equals(VerticalStairShape.STRAIGHT)
                || shape.equals(VerticalStairShape.INNER_TOP)
                || shape.equals(VerticalStairShape.OUTER_BOTTOM);
    }

    public static boolean isVStairs(BlockState state) {
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
