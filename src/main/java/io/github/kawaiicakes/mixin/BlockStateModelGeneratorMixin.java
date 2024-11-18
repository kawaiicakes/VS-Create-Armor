package io.github.kawaiicakes.mixin;

import io.github.kawaiicakes.data.ArmorStateModelGenerator;
import net.minecraft.data.client.BlockStateModelGenerator;
import org.spongepowered.asm.mixin.Mixin;

/*
    NOTE: vscarmor.mixins.json should keep these mixins in the common array even though these classes are
    in net.minecraft.data.client, otherwise they do not inject. If this causes issues in the future,
    welp... have fun, I guess
 */
@Mixin(BlockStateModelGenerator.class)
public abstract class BlockStateModelGeneratorMixin implements ArmorStateModelGenerator {}
