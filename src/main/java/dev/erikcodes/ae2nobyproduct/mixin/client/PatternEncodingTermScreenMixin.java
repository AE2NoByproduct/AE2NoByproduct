package dev.erikcodes.ae2nobyproduct.mixin.client;

import appeng.client.gui.me.items.PatternEncodingTermScreen;
import dev.erikcodes.ae2nobyproduct.client.ByproductToggleButton;
import net.minecraft.client.gui.components.Button;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// remap = false: PatternEncodingTermScreen and addToLeftToolbar belong to AE2,
// whose member names are stable dev<->prod, so the Mixin AP must not try to map
// them to Minecraft searge names. The MC `Button` type in the descriptor is
// reobfuscated to SRG by ForgeGradle's reobf at the bytecode level, independent
// of the Mixin refmap, so it still matches AE2's reobfuscated method in prod.
//
// Injection point: the AE2 constructor (<init>) TAIL. AEBaseScreen creates its
// `verticalToolbar` (a VerticalButtonBar) in its constructor and adds its own
// buttons there (e.g. the help button). VerticalButtonBar keeps a persistent
// List<Button> that is never cleared; init()/resize re-registers those same
// instances via widgets.populateScreen and re-positions them each frame. So a
// single constructor-time add mirrors AE2's own pattern: it appears once, never
// duplicates, and survives resizes natively.
@Mixin(value = PatternEncodingTermScreen.class, remap = false)
public abstract class PatternEncodingTermScreenMixin {

    // Inherited from AEBaseScreen: protected final <B extends Button> B addToLeftToolbar(B)
    // declared here with the erased generic signature.
    @Shadow(remap = false)
    protected abstract Button addToLeftToolbar(Button button);

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void ae2nobyproduct$addToggleButton(CallbackInfo ci) {
        addToLeftToolbar(new ByproductToggleButton());
    }
}
