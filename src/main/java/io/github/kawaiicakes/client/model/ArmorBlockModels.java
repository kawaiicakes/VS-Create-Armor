package io.github.kawaiicakes.client.model;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static io.github.kawaiicakes.VSCreateArmor.MOD_ID;

public class ArmorBlockModels {
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

    public static final Model PORTHOLE = block(
            MOD_ID, "porthole", TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.END
    );

    public static final Model PORTHOLE_EMPTY = block(
            MOD_ID, "porthole_empty", "_empty", TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.END
    );

    public static final Model VERTICAL_PORTHOLE = block(
            MOD_ID,
            "vertical_porthole", "_vertical",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model VERTICAL_PORTHOLE_EMPTY = block(
            MOD_ID,
            "vertical_porthole_empty", "_vertical_empty",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model PORTHOLE_SLAB = block(
            MOD_ID,
            "porthole_slab",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model PORTHOLE_SLAB_TOP = block(
            MOD_ID,
            "porthole_slab_top", "_top",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model PORTHOLE_SLAB_EMPTY = block(
            MOD_ID,
            "porthole_slab_empty", "_empty",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model PORTHOLE_SLAB_TOP_EMPTY = block(
            MOD_ID,
            "porthole_slab_top_empty", "_top_empty",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model PORTHOLE_VERTICAL_SLAB = block(
            MOD_ID, "porthole_vertical_slab",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.END
    );

    public static final Model PORTHOLE_VERTICAL_SLAB_EMPTY = block(
            MOD_ID, "porthole_vertical_slab_empty", "_empty",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.END
    );

    public static final Model VERTICAL_WINDOW = block(
            MOD_ID, "vertical_window", TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.END
    );

    public static final Model VERTICAL_WINDOW_EMPTY = block(
            MOD_ID, "vertical_window_empty", "_empty", TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.END
    );

    public static final Model VERTICAL_WINDOW_VERTICAL = block(
            MOD_ID,
            "vertical_window_vertical", "_vertical",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model VERTICAL_WINDOW_VERTICAL_EMPTY = block(
            MOD_ID,
            "vertical_window_vertical_empty", "_vertical_empty",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model VERTICAL_WINDOW_SLAB = block(
            MOD_ID,
            "vertical_window_slab",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model VERTICAL_WINDOW_SLAB_TOP = block(
            MOD_ID,
            "vertical_window_slab_top", "_top",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model VERTICAL_WINDOW_SLAB_EMPTY = block(
            MOD_ID,
            "vertical_window_slab_empty", "_empty",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model VERTICAL_WINDOW_SLAB_TOP_EMPTY = block(
            MOD_ID,
            "vertical_window_slab_top_empty", "_top_empty",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model VERTICAL_WINDOW_VERTICAL_SLAB = block(
            MOD_ID, "vertical_window_vertical_slab",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.END
    );

    public static final Model VERTICAL_WINDOW_VERTICAL_SLAB_EMPTY = block(
            MOD_ID, "vertical_window_vertical_slab_empty", "_empty",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.END
    );

    public static final Model HORIZONTAL_WINDOW = block(
            MOD_ID, "horizontal_window", TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.END
    );

    public static final Model HORIZONTAL_WINDOW_EMPTY = block(
            MOD_ID, "horizontal_window_empty", "_empty", TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.END
    );

    public static final Model HORIZONTAL_WINDOW_VERTICAL = block(
            MOD_ID,
            "horizontal_window_vertical", "_vertical",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model HORIZONTAL_WINDOW_VERTICAL_EMPTY = block(
            MOD_ID,
            "horizontal_window_vertical_empty", "_vertical_empty",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model HORIZONTAL_WINDOW_SLAB = block(
            MOD_ID,
            "horizontal_window_slab",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model HORIZONTAL_WINDOW_SLAB_TOP = block(
            MOD_ID,
            "horizontal_window_slab_top", "_top",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model HORIZONTAL_WINDOW_SLAB_EMPTY = block(
            MOD_ID,
            "horizontal_window_slab_empty", "_empty",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model HORIZONTAL_WINDOW_SLAB_TOP_EMPTY = block(
            MOD_ID,
            "horizontal_window_slab_top_empty", "_top_empty",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM
    );

    public static final Model HORIZONTAL_WINDOW_VERTICAL_SLAB = block(
            MOD_ID, "horizontal_window_vertical_slab",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.END
    );

    public static final Model HORIZONTAL_WINDOW_VERTICAL_SLAB_EMPTY = block(
            MOD_ID, "horizontal_window_vertical_slab_empty", "_empty",
            TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.END
    );

    public static final Model FENCE_INVENTORY = block(
            MOD_ID, "fence_inventory", "_inventory",
            TextureKey.TEXTURE, TextureKey.TOP, TextureKey.BOTTOM
    );
    public static final Model FENCE_SIDE = block(
            MOD_ID, "fence_side", "_side",
            TextureKey.TEXTURE, TextureKey.TOP, TextureKey.BOTTOM
    );
    public static final Model FENCE_POST = block(
            MOD_ID, "fence_post", "_post",
            TextureKey.TEXTURE, TextureKey.TOP, TextureKey.BOTTOM
    );

    private static Model block(String namespace, String parent, String variant, TextureKey... requiredTextureKeys) {
        return new Model(
                Optional.of(new Identifier(namespace, "block/" + parent)),
                Optional.of(variant),
                requiredTextureKeys
        );
    }

    private static Model block(String namespace, String parent, TextureKey... requiredTextureKeys) {
        return new Model(
                Optional.of(new Identifier(namespace, "block/" + parent)),
                Optional.empty(),
                requiredTextureKeys
        );
    }
}
