package io.github.kawaiicakes.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

// TODO - Less cringe placement logic
// TODO (1.1) - Collision and shape methods are required to be implemented from here for empty blockstates
@SuppressWarnings("deprecation")
public abstract class AbstractWindowBlock extends PillarBlock implements WindowBlock {
    public AbstractWindowBlock(Settings settings) {
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
        return (stateFrom.isOf(this)) && stateFrom.get(AXIS).equals(state.get(AXIS));
    }
}
