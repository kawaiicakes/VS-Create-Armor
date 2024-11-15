package io.github.kawaiicakes;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VSCreateArmor implements ModInitializer {
	public static final String MOD_ID = "vscarmor";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Registry.register();
	}
}