package io.github.kawaiicakes;

import io.github.kawaiicakes.block.PortholeBlock;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

// TODO (1.1) - Eighths
// TODO (1.1) - Reactive armour
// TODO (1.1) - Alphabet shit.
// TODO (1.1) - Sandbag
// TODO (1.1) - Tooltip includes armour stats
// TODO (1.2) - Connecting textures
public class VSCreateArmor implements ModInitializer, ClientModInitializer {
	public static final String MOD_ID = "vscarmor";

	@Override
	public void onInitialize() {
		Registry.register();
	}

	@Override
	public void onInitializeClient() {
		Registry.REGISTERED.forEach(
				item -> {
					if (item.getBlock() instanceof PortholeBlock porthole)
						BlockRenderLayerMap.INSTANCE.putBlock(porthole, RenderLayer.getTranslucent());
				}
		);
	}
}