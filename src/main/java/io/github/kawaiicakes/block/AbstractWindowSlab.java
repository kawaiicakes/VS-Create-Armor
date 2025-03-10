package io.github.kawaiicakes.block;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

// TODO (1.1) - Collision and shape methods are required to be implemented from here for empty blockstates
@SuppressWarnings("deprecation")
public abstract class AbstractWindowSlab extends SlabBlock implements WindowBlock {
    public AbstractWindowSlab(Settings settings) {
        super(settings);
    }

    @Override
    public abstract VoxelShape getCameraCollisionShape(
            BlockState state, BlockView world, BlockPos pos, ShapeContext context
    );

    @Override
    public abstract boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction);
}
