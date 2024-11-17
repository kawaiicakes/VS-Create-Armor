package io.github.kawaiicakes.mixin;

import io.github.kawaiicakes.data.ArmorStateModelGenerator;
import net.minecraft.data.client.BlockStateModelGenerator;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockStateModelGenerator.class)
public abstract class BlockStateModelGeneratorMixin implements ArmorStateModelGenerator {}
