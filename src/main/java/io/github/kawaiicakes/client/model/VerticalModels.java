package io.github.kawaiicakes.client.model;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static io.github.kawaiicakes.VSCreateArmor.MOD_ID;

public class VerticalModels {
    public static final Model V_SLAB = block(
            "vertical_slab",
            TextureKey.BOTTOM, TextureKey.TOP, TextureKey.NORTH, TextureKey.EAST, TextureKey.SOUTH, TextureKey.WEST
    );

    public static final Model V_STAIRS = block(
            "vertical_stairs",
            TextureKey.BOTTOM, TextureKey.TOP, TextureKey.NORTH, TextureKey.EAST, TextureKey.SOUTH, TextureKey.WEST
    );

    public static final Model V_STAIRS_INNER = block(
            "inner_vertical_stairs", "_inner",
            TextureKey.BOTTOM, TextureKey.TOP, TextureKey.NORTH, TextureKey.EAST, TextureKey.SOUTH, TextureKey.WEST
    );

    public static final Model V_STAIRS_OUTER = block(
            "outer_vertical_stairs", "_outer",
            TextureKey.BOTTOM, TextureKey.TOP, TextureKey.NORTH, TextureKey.EAST, TextureKey.SOUTH, TextureKey.WEST
    );

    private static Model block(String parent, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(new Identifier(MOD_ID, "block/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    private static Model block(String parent, String variant, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(new Identifier(MOD_ID, "block/" + parent)), Optional.of(variant), requiredTextureKeys);
    }
}
