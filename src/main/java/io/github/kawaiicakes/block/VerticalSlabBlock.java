package io.github.kawaiicakes.block;

import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.block.SlabBlock.WATERLOGGED;

@SuppressWarnings("deprecation")
public class VerticalSlabBlock extends HorizontalFacingBlock implements Waterloggable {
    public static final BooleanProperty DOUBLET = BooleanProperty.of("doublet");
    protected static final VoxelShape NORTH = Block.createCuboidShape(
            0.0, 0.0, 0.0,
            16.0, 16.0, 8.0
    );
    protected static final VoxelShape EAST = Block.createCuboidShape(
            8.0, 0.0, 0.0,
            16.0, 16.0, 16.0
    );
    protected static final VoxelShape SOUTH = Block.createCuboidShape(
            0.0, 0.0, 8.0,
            16.0, 16.0, 16.0
    );
    protected static final VoxelShape WEST = Block.createCuboidShape(
            0.0, 0.0, 0.0,
            8.0, 16.0, 16.0
    );

    public VerticalSlabBlock(Settings settings) {
        super(settings);
        this.setDefaultState(
                this.getDefaultState()
                        .with(FACING, Direction.SOUTH)
                        .with(DOUBLET, Boolean.FALSE)
                        .with(WATERLOGGED, Boolean.FALSE)
        );
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return state.get(DOUBLET) != Boolean.TRUE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, DOUBLET, WATERLOGGED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(DOUBLET) == Boolean.TRUE
                ? VoxelShapes.fullCube()
                : switch (state.get(FACING)) {
                        case DOWN, UP -> null;
                        case NORTH -> NORTH;
                        case SOUTH -> SOUTH;
                        case WEST -> WEST;
                        case EAST -> EAST;
                };
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockAtPos = ctx.getWorld().getBlockState(blockPos);

        if (blockAtPos.isOf(this)) {
            return blockAtPos.with(DOUBLET, Boolean.TRUE).with(WATERLOGGED, Boolean.FALSE);
        } else {
            FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
            BlockState blockState2 = this.getDefaultState()
                    .with(DOUBLET, Boolean.FALSE)
                    .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);

            return ctx.getHorizontalPlayerFacing().getAxis() == Direction.Axis.X
                    ? ctx.getHitPos().x - (double)ctx.getBlockPos().getX() > 0.5
                            ? blockState2.with(FACING, Direction.EAST)
                            : blockState2.with(FACING, Direction.WEST)
                    : ctx.getHitPos().z - (double)ctx.getBlockPos().getZ() > 0.5
                            ? blockState2.with(FACING, Direction.SOUTH)
                            : blockState2.with(FACING, Direction.NORTH);
        }
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        ItemStack itemStack = context.getStack();
        Boolean doublet = state.get(DOUBLET);
        Direction facing = state.get(FACING);

        if (doublet || !itemStack.isOf(this.asItem())) {
            return false;
        } else if (context.canReplaceExisting()) {
            return context.getSide() == facing.getOpposite();
        } else {
            return true;
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        return state.get(DOUBLET) != Boolean.TRUE && Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
    }

    @Override
    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return state.get(DOUBLET) != Boolean.TRUE && Waterloggable.super.canFillWithFluid(world, pos, state, fluid);
    }

    @Override
    public BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction,
            BlockState neighborState,
            WorldAccess world,
            BlockPos pos, BlockPos neighborPos
    ) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return switch (type) {
            case LAND, AIR -> false;
            case WATER -> world.getFluidState(pos).isIn(FluidTags.WATER);
        };
    }
}
