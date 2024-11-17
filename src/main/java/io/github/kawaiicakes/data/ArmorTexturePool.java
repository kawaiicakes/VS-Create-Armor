package io.github.kawaiicakes.data;

import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;

public interface ArmorTexturePool {
    @SuppressWarnings("RedundantCast")
    default BlockStateModelGenerator.BlockTexturePool vSCreateArmor$verticalSlab(Block block) {
        return (BlockStateModelGenerator.BlockTexturePool)(Object) this;
    }

    @SuppressWarnings("RedundantCast")
    default BlockStateModelGenerator.BlockTexturePool vSCreateArmor$verticalStairs(Block block) {
        return (BlockStateModelGenerator.BlockTexturePool)(Object) this;
    }
}
