package io.github.kawaiicakes.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

// TODO (1.1) - Collision and shape methods are required to be implemented from here for empty blockstates
@SuppressWarnings("deprecation")
public abstract class AbstractWindowVerticalSlab extends VerticalSlabBlock implements WindowBlock {
    public AbstractWindowVerticalSlab(Settings settings) {
        super(
                settings
                        .nonOpaque()
                        .allowsSpawning(Blocks::never)
                        .solidBlock(Blocks::never)
                        .suffocates(Blocks::never)
                        .blockVision(Blocks::never)
        );
    }

    @Override
    public abstract VoxelShape getCameraCollisionShape(
            BlockState state, BlockView world, BlockPos pos, ShapeContext context
    );

    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (!stateFrom.isOf(this)) return false;
        if (direction.equals(Direction.UP) || direction.equals(Direction.DOWN)) return false;
        if (!state.get(FACING).getAxis().equals(stateFrom.get(FACING).getAxis())) return false;
        if (!direction.getAxis().equals(state.get(FACING).getAxis())) return false;

        if (stateFrom.get(DOUBLET)) {
            return state.get(DOUBLET) || state.get(FACING).equals(stateFrom.get(FACING).getOpposite());
        } else {
            return state.get(DOUBLET)
                    ? stateFrom.get(FACING).equals(direction)
                    : state.get(FACING).equals(stateFrom.get(FACING).getOpposite());
        }
    }
}
