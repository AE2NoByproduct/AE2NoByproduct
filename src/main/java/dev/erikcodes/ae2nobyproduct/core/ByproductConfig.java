package dev.erikcodes.ae2nobyproduct.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.architectury.platform.Platform;
import dev.erikcodes.ae2nobyproduct.CommonMod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loader-agnostic settings for the byproduct-stripping logic, stored as a single JSON file in the
 * standard config folder ({@code config/ae2nobyproduct.json}) on every loader via Architectury's
 * {@link Platform#getConfigFolder()}. The mod is server-authoritative, so on a multiplayer server the
 * server's copy is what applies; clients learn the effective state through {@code ModNetworking}.
 *
 * <p>Read lazily and cached. A partial or older file keeps the defaults for any missing option; a
 * broken file is left untouched (defaults are used for that run) so the player can fix it by hand.
 */
public final class ByproductConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static volatile ByproductConfig instance;

    private boolean enableFeature = true;
    private boolean allowPlayerToggle = true;
    private boolean defaultStrip = false;
    private boolean consumeOnUse = false;
    private boolean showMessages = true;

    private ByproductConfig() {}

    public static ByproductConfig get() {
        ByproductConfig local = instance;
        if (local == null) {
            synchronized (ByproductConfig.class) {
                local = instance;
                if (local == null) {
                    local = load();
                    instance = local;
                }
            }
        }
        return local;
    }

    public boolean enableFeature() { return enableFeature; }
    public boolean allowPlayerToggle() { return allowPlayerToggle; }
    public boolean defaultStrip() { return defaultStrip; }
    /** Whether the Byproduct Remover item is consumed (shrinks by 1) on a successful clean. */
    public boolean consumeOnUse() { return consumeOnUse; }
    /** Whether the Byproduct Remover item sends chat feedback when used. */
    public boolean showMessages() { return showMessages; }

    private static Path path() {
        return Platform.getConfigFolder().resolve(CommonMod.MOD_ID + ".json");
    }

    private static ByproductConfig load() {
        ByproductConfig cfg = new ByproductConfig(); // primitive field defaults
        Path p = path();
        if (Files.exists(p)) {
            try {
                // Overlay only the keys actually present, so a partial/old file keeps the defaults
                // for any missing option instead of silently reading false.
                JsonObject o = GSON.fromJson(Files.readString(p), JsonObject.class);
                if (o != null) {
                    cfg.enableFeature = bool(o, "enableFeature", cfg.enableFeature);
                    cfg.allowPlayerToggle = bool(o, "allowPlayerToggle", cfg.allowPlayerToggle);
                    cfg.defaultStrip = bool(o, "defaultStrip", cfg.defaultStrip);
                    cfg.consumeOnUse = bool(o, "consumeOnUse", cfg.consumeOnUse);
                    cfg.showMessages = bool(o, "showMessages", cfg.showMessages);
                }
            } catch (IOException | RuntimeException e) {
                // Leave the user's broken-but-recoverable file untouched; run with defaults this
                // session rather than clobbering it with default values.
                CommonMod.LOGGER.warn("Could not read {}.json; using defaults this run, file left as-is", CommonMod.MOD_ID, e);
                return cfg;
            }
            cfg.save(); // parsed OK: rewrite to backfill any keys missing from an older file
        } else {
            cfg.save(); // no file yet: materialise one with the defaults
        }
        return cfg;
    }

    private static boolean bool(JsonObject o, String key, boolean def) {
        return (o.has(key) && o.get(key).isJsonPrimitive()) ? o.get(key).getAsBoolean() : def;
    }

    private void save() {
        try {
            Files.writeString(path(), GSON.toJson(this));
        } catch (IOException e) {
            CommonMod.LOGGER.warn("Could not write {}.json", CommonMod.MOD_ID, e);
        }
    }
}
