package io.github.kawaiicakes.client.model;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class MiscModels {
    public static final Model INNER_STAIRS_TOP = block(
            "minecraft", "inner_stairs", "_inner_top",
            TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE
    );

    public static final Model STAIRS_TOP = block(
            "minecraft", "stairs", "_top",
            TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE
    );

    public static final Model OUTER_STAIRS_TOP = block(
            "minecraft", "outer_stairs", "_outer_top",
            TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE
    );

    private static Model block(String namespace, String parent, String variant, TextureKey... requiredTextureKeys) {
        return new Model(
                Optional.of(new Identifier(namespace, "block/" + parent)),
                Optional.of(variant),
                requiredTextureKeys
        );
    }
}
