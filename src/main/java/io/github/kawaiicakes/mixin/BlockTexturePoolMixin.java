package io.github.kawaiicakes.mixin;

import io.github.kawaiicakes.client.model.VerticalModels;
import io.github.kawaiicakes.data.ArmorFamily;
import io.github.kawaiicakes.data.ArmorStateModelGenerator;
import io.github.kawaiicakes.data.ArmorTexturePool;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiConsumer;

/*
    NOTE: vscarmor.mixins.json should keep these mixins in the common array even though these classes are
    in net.minecraft.data.client, otherwise they do not inject. If this causes issues in the future,
    welp... have fun, I guess
 */
@Mixin(BlockStateModelGenerator.BlockTexturePool.class)
public abstract class BlockTexturePoolMixin implements ArmorTexturePool {
    @Shadow @Final BlockStateModelGenerator field_22836;
    @Shadow
    private Identifier baseModelId;

    @Shadow
    protected abstract Identifier ensureModel(Model model, Block block);

    @Inject(
            method = "family",
            at = @At("TAIL")
    )
    private void handleArmorVariants(BlockFamily family, CallbackInfoReturnable<BlockStateModelGenerator.BlockTexturePool> cir) {
        if (family instanceof ArmorFamily armorFamily) {
            armorFamily.getArmorVariants()
                    .forEach(
                            (variant, block) -> {
                                BiConsumer<BlockStateModelGenerator.BlockTexturePool, Block> biConsumer = BlockStateModelGenerator.ARMOR_VARIANT_POOL_FUNCTIONS
                                        .get(variant);
                                if (biConsumer != null) {
                                    biConsumer.accept(((BlockStateModelGenerator.BlockTexturePool)(Object) this), block);
                                }
                            }
                    );
        }
    }

    @Override
    public BlockStateModelGenerator.BlockTexturePool vSCreateArmor$verticalSlab(Block block) {
        if (this.baseModelId == null) {
            throw new IllegalStateException("Full block not generated yet");
        } else {
            Identifier south = this.ensureModel(VerticalModels.VSLAB, block);
            this.field_22836.blockStateCollector.accept(ArmorStateModelGenerator.createVerticalSlabBlockState(
                    block, south, this.baseModelId
            ));
            this.field_22836.registerParentedItemModel(block, south);
            return ((BlockStateModelGenerator.BlockTexturePool)(Object) this);
        }
    }

    @Override
    public BlockStateModelGenerator.BlockTexturePool vSCreateArmor$verticalStairs(Block block) {
        return ((BlockStateModelGenerator.BlockTexturePool)(Object) this);
    }
}
