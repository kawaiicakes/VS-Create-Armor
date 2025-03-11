package io.github.kawaiicakes.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class VerticalWindowSlab extends AbstractWindowSlab {
    public VerticalWindowSlab(Settings settings) {
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
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }
}
