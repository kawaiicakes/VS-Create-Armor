package io.github.kawaiicakes.data;

import net.minecraft.block.Block;
import net.minecraft.data.family.BlockFamily;

import java.util.HashMap;
import java.util.Map;

public class ArmorFamily extends BlockFamily {
    final Map<Variant, Block> armorVariants = new HashMap<>();

    protected ArmorFamily(Block baseBlock) {
        super(baseBlock);
    }

    public Map<Variant, Block> getArmorVariants() {
        return this.armorVariants;
    }

    public static class Builder extends BlockFamily.Builder {
        public Builder(Block baseBlock) {
            super(baseBlock);
            this.family = new ArmorFamily(baseBlock);
        }

        @Override
        public Builder slab(Block block) {
            return (Builder) super.slab(block);
        }

        @Override
        public Builder stairs(Block block) {
            return (Builder) super.stairs(block);
        }

        public Builder verticalSlab(Block block) {
            ((ArmorFamily) this.family).armorVariants.put(Variant.VERTICAL_SLAB, block);
            return this;
        }

        public Builder verticalStairs(Block block) {
            ((ArmorFamily) this.family).armorVariants.put(Variant.VERTICAL_STAIRS, block);
            return this;
        }
    }

    public enum Variant {
        VERTICAL_SLAB("vertical_slab"),
        VERTICAL_STAIRS("vertical_stairs");

        private final String name;

        Variant(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
