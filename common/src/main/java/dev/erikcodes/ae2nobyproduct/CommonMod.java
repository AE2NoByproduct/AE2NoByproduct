package dev.erikcodes.ae2nobyproduct;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Loader-agnostic entry point. Invoked by each platform's mod initializer
 * ({@code AE2NoByProductForge} on Forge, {@code AE2NoByProductFabric} on Fabric).
 */
public final class CommonMod {
    public static final String MOD_ID = "ae2nobyproduct";
    public static final Logger LOGGER = LogUtils.getLogger();

    private CommonMod() {}

    public static void init() {
        LOGGER.info("AE2 No Byproduct (common) initializing");
    }
}
