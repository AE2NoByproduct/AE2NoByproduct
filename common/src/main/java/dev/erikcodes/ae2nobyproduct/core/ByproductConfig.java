package dev.erikcodes.ae2nobyproduct.core;

import net.minecraft.world.entity.player.Player;

/**
 * Loader-agnostic access to the settings the byproduct-stripping logic needs.
 *
 * <p>The decision logic ({@link EffectiveState} / {@link ByproductService}) lives in
 * {@code common} and is shared by Forge and Fabric. The actual source of these values is
 * loader-specific, so each platform installs a {@link Provider}:
 * <ul>
 *   <li><b>Forge</b> installs a provider backed by its {@code ForgeConfigSpec} config and its
 *       per-player persisted-NBT toggle store.</li>
 *   <li><b>Fabric</b> (this milestone) uses {@link #DEFAULT}: feature on, no per-player toggle,
 *       strip by default, so the shared mixin actually strips byproducts on Fabric without a
 *       config/UI/networking port.</li>
 * </ul>
 *
 * <p>Per-player toggle access ({@link Provider#savedState}/{@link Provider#setSavedState}) is part
 * of this interface because the Forge implementation relies on {@code Player.getPersistentData()},
 * which does not exist in vanilla/Fabric, so keeping it behind the provider lets the persistence
 * implementation stay on the Forge side.
 */
public final class ByproductConfig {

    public interface Provider {
        boolean enableFeature();
        boolean allowPlayerToggle();
        boolean defaultStrip();
        /** Whether the Byproduct Remover item is consumed (shrinks by 1) on a successful clean. */
        boolean consumeOnUse();
        /** Whether the Byproduct Remover item sends chat feedback when used. */
        boolean showMessages();
        /** Per-player saved toggle. Only consulted when {@link #allowPlayerToggle()} is true. */
        boolean savedState(Player player, boolean def);
        void setSavedState(Player player, boolean value);
    }

    /** Default used by loaders that have not wired a config yet (Fabric, this milestone). */
    public static final Provider DEFAULT = new Provider() {
        @Override public boolean enableFeature() { return true; }
        @Override public boolean allowPlayerToggle() { return false; }
        @Override public boolean defaultStrip() { return true; }
        @Override public boolean consumeOnUse() { return false; }
        @Override public boolean showMessages() { return true; }
        @Override public boolean savedState(Player player, boolean def) { return def; }
        @Override public void setSavedState(Player player, boolean value) { /* no persistence */ }
    };

    private static volatile Provider provider = DEFAULT;

    private ByproductConfig() {}

    /** Called once per platform during mod init to supply loader-specific settings. */
    public static void install(Provider p) {
        provider = (p != null) ? p : DEFAULT;
    }

    public static Provider get() {
        return provider;
    }
}
