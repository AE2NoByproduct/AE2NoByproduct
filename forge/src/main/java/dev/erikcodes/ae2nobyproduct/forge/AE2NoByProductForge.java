package dev.erikcodes.ae2nobyproduct.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.erikcodes.ae2nobyproduct.CommonMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CommonMod.MOD_ID)
public class AE2NoByProductForge {
    public AE2NoByProductForge() {
        // Submit our event bus to let architectury register our content at the right time.
        EventBuses.registerModEventBus(CommonMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        CommonMod.init();
    }
}
