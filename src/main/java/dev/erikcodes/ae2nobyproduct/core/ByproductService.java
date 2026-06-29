package dev.erikcodes.ae2nobyproduct.core;

import net.minecraft.world.entity.player.Player;

/**
 * Loader-agnostic decision logic for whether byproducts should be stripped for a given player.
 * Settings come from {@link ByproductConfig} and the per-player toggle from {@link ByproductStore};
 * both are shared across every loader.
 */
public final class ByproductService {
    private ByproductService() {}

    /** Effective on/off for this player given the config + saved toggle. */
    public static boolean effectiveFor(Player player) {
        ByproductConfig cfg = ByproductConfig.get();
        // Only consult per-player persistence when toggling is actually allowed.
        boolean saved = cfg.allowPlayerToggle()
                ? ByproductStore.getToggle(player, cfg.defaultStrip())
                : cfg.defaultStrip();
        return EffectiveState.compute(cfg.enableFeature(), cfg.allowPlayerToggle(), cfg.defaultStrip(), saved);
    }

    /** Whether to strip byproducts when this player encodes now. */
    public static boolean shouldStrip(Player player) {
        return effectiveFor(player);
    }
}
