package io.github.kawaiicakes.client.model;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class VerticalModels {
    public static final Model VSLAB_SOUTH = block(TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    public static final Model VSLAB_NORTH = block("slab_north", "_north", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    public static final Model VSLAB_EAST = block("slab_east", "_east", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    public static final Model VSLAB_WEST = block("slab_west", "_west", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);

    private static Model block(TextureKey... requiredTextureKeys) {
        return new Model(
                Optional.of(new Identifier("minecraft", "block/" + "slab")),
                Optional.empty(),
                requiredTextureKeys
        );
    }

    private static Model block(String parent, String variant, TextureKey... requiredTextureKeys) {
        return new Model(
                Optional.of(new Identifier("minecraft", "block/" + parent)),
                Optional.of(variant),
                requiredTextureKeys
        );
    }
}
