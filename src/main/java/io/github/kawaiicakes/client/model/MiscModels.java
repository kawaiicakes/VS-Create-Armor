package io.github.kawaiicakes.client.model;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static io.github.kawaiicakes.VSCreateArmor.MOD_ID;

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

    public static final Model TEMPLATE_WALL_POST = block(
            MOD_ID, "template_wall_post", "_post", TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model TEMPLATE_WALL_SIDE = block(
            MOD_ID, "template_wall_side", "_side", TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model TEMPLATE_WALL_SIDE_TALL = block(
            MOD_ID, "template_wall_side_tall", "_side_tall",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model WALL_INVENTORY = block(
            MOD_ID, "wall_inventory", "_inventory", TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    private static Model block(String namespace, String parent, String variant, TextureKey... requiredTextureKeys) {
        return new Model(
                Optional.of(new Identifier(namespace, "block/" + parent)),
                Optional.of(variant),
                requiredTextureKeys
        );
    }
}
