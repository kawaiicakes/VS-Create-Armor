package io.github.kawaiicakes;

import io.github.kawaiicakes.block.WindowBlock;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;

// TODO (1.1) - Eighths
// TODO (1.1) - Reactive armour
// TODO (1.1) - Alphabet shit.
// TODO (1.1) - Sandbag
// TODO (1.1) - Tooltip includes armour stats
// TODO (1.1) - Borderless variants. Tiling is key.
// TODO (1.1) - Porthole texture/model improvement. Infrastructure is already in place
// TODO (1.1) - LargeWindow
// TODO (1.1) - Hatches, bulkhead doors
// TODO (2.0) - Connecting textures
// NOTE: Fixed culling issues + wrong blockstate model used for 1.0 already.
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
					if (item.getBlock() instanceof WindowBlock window)
						BlockRenderLayerMap.INSTANCE.putBlock(item.getBlock(), window.getRenderLayer());
				}
		);
	}
}