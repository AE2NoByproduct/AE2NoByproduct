package dev.nilsenkonsult.ae2nobyproduct;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(AE2NoByProduct.MOD_ID)
public class AE2NoByProduct {
    public static final String MOD_ID = "ae2nobyproduct";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AE2NoByProduct() {
        LOGGER.info("AE2 No Byproduct loading");
    }
}
