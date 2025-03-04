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

    public static final Model V_STAIRS_RIGHT = block(
            "vertical_stairs_right", "_straight_right",
            TextureKey.BOTTOM, TextureKey.TOP, TextureKey.NORTH, TextureKey.EAST, TextureKey.SOUTH, TextureKey.WEST
    );

    public static final Model V_STAIRS_INNER_RIGHT_BOTTOM = block(
            "inner_vertical_stairs_right_bottom", "_inner_right_bottom",
            TextureKey.BOTTOM, TextureKey.TOP, TextureKey.NORTH, TextureKey.EAST, TextureKey.SOUTH, TextureKey.WEST
    );

    public static final Model V_STAIRS_OUTER_RIGHT_BOTTOM = block(
            "outer_vertical_stairs_right_bottom", "_outer_right_bottom",
            TextureKey.BOTTOM, TextureKey.TOP, TextureKey.NORTH, TextureKey.EAST, TextureKey.SOUTH, TextureKey.WEST
    );

    public static final Model V_STAIRS_INNER_RIGHT_TOP = block(
            "inner_vertical_stairs_right_top", "_inner_right_top",
            TextureKey.BOTTOM, TextureKey.TOP, TextureKey.NORTH, TextureKey.EAST, TextureKey.SOUTH, TextureKey.WEST
    );

    public static final Model V_STAIRS_OUTER_RIGHT_TOP = block(
            "outer_vertical_stairs_right_top", "_outer_right_top",
            TextureKey.BOTTOM, TextureKey.TOP, TextureKey.NORTH, TextureKey.EAST, TextureKey.SOUTH, TextureKey.WEST
    );

    public static final Model V_STAIRS_LEFT = block(
            "vertical_stairs_left", "_straight_left",
            TextureKey.BOTTOM, TextureKey.TOP, TextureKey.NORTH, TextureKey.EAST, TextureKey.SOUTH, TextureKey.WEST
    );

    public static final Model V_STAIRS_INNER_LEFT_BOTTOM = block(
            "inner_vertical_stairs_left_bottom", "_inner_left_bottom",
            TextureKey.BOTTOM, TextureKey.TOP, TextureKey.NORTH, TextureKey.EAST, TextureKey.SOUTH, TextureKey.WEST
    );

    public static final Model V_STAIRS_OUTER_LEFT_BOTTOM = block(
            "outer_vertical_stairs_left_bottom", "_outer_left_bottom",
            TextureKey.BOTTOM, TextureKey.TOP, TextureKey.NORTH, TextureKey.EAST, TextureKey.SOUTH, TextureKey.WEST
    );

    public static final Model V_STAIRS_INNER_LEFT_TOP = block(
            "inner_vertical_stairs_left_top", "_inner_left_top",
            TextureKey.BOTTOM, TextureKey.TOP, TextureKey.NORTH, TextureKey.EAST, TextureKey.SOUTH, TextureKey.WEST
    );

    public static final Model V_STAIRS_OUTER_LEFT_TOP = block(
            "outer_vertical_stairs_left_top", "_outer_left_top",
            TextureKey.BOTTOM, TextureKey.TOP, TextureKey.NORTH, TextureKey.EAST, TextureKey.SOUTH, TextureKey.WEST
    );

    private static Model block(String parent, TextureKey... requiredTextureKeys) {
        return new Model(
                Optional.of(new Identifier(MOD_ID, "block/" + parent)),
                Optional.empty(),
                requiredTextureKeys
        );
    }

    private static Model block(String parent, String variant, TextureKey... requiredTextureKeys) {
        return new Model(
                Optional.of(new Identifier(MOD_ID, "block/" + parent)),
                Optional.of(variant),
                requiredTextureKeys
        );
    }
}
