package io.github.kawaiicakes.client.model;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static io.github.kawaiicakes.VSCreateArmor.MOD_ID;

public class VerticalModels {
    public static final Model VSLAB = block(TextureKey.BOTTOM, TextureKey.TOP, TextureKey.NORTH, TextureKey.EAST, TextureKey.SOUTH, TextureKey.WEST);

    private static Model block(TextureKey... requiredTextureKeys) {
        return new Model(
                Optional.of(new Identifier(MOD_ID, "block/vertical_slab")),
                Optional.empty(),
                requiredTextureKeys
        );
    }
}
