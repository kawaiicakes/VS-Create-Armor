package io.github.kawaiicakes.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

@SuppressWarnings("unused")
public interface WindowBlock {
    boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction);

    default RenderLayer getRenderLayer() {
        return RenderLayer.getTranslucent();
    }

    VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);

    default float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    default boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }
}
