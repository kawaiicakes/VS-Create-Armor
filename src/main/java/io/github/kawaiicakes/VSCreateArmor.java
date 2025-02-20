package io.github.kawaiicakes;

import net.fabricmc.api.ModInitializer;

// TODO - Figure out what that rotated layered steel texture is for?
// TODO - Implement the alphabet shit.
// TODO - Implement the sandbag.
public class VSCreateArmor implements ModInitializer {
	public static final String MOD_ID = "vscarmor";

	@Override
	public void onInitialize() {
		Registry.register();
	}
}