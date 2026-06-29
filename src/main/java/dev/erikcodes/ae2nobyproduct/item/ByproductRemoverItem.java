package dev.erikcodes.ae2nobyproduct.item;

import appeng.blockentity.crafting.PatternProviderBlockEntity;
import dev.erikcodes.ae2nobyproduct.core.ByproductConfig;
import dev.erikcodes.ae2nobyproduct.core.ByproductStripper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Reusable tool that strips byproducts from every processing pattern stored in a single Pattern
 * Provider. Right-click a Pattern Provider block: each PROCESSING pattern with more than one output is
 * re-encoded keeping only its first (primary) output. Server-side only. The network-wide equivalent is
 * the {@code /ae2nobyproduct strip-all} command; both share {@link ByproductStripper}.
 *
 * <p>Config is read from the shared {@link ByproductConfig}, so it behaves identically on every loader.
 */
public class ByproductRemoverItem extends Item {

    public ByproductRemoverItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        // All logic is server-side. Tell the client the interaction succeeded so the
        // arm-swing/animation plays without running gameplay logic twice.
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        ByproductConfig cfg = ByproductConfig.get();
        if (!cfg.enableFeature()) {
            notify(player, "message.ae2nobyproduct.disabled");
            return InteractionResult.CONSUME;
        }

        BlockEntity be = level.getBlockEntity(context.getClickedPos());
        if (!(be instanceof PatternProviderBlockEntity provider)) {
            return InteractionResult.PASS;
        }

        int cleaned = ByproductStripper.strip(provider.getLogic().getPatternInv(), level, true);

        if (cleaned > 0) {
            // Persist the edited inventory and notify the provider's grid logic.
            provider.getLogic().saveChanges();
            provider.setChanged();
            notify(player, "message.ae2nobyproduct.removed", cleaned);
            if (cfg.consumeOnUse()) {
                context.getItemInHand().shrink(1);
            }
        } else {
            notify(player, "message.ae2nobyproduct.none");
        }

        return InteractionResult.CONSUME;
    }

    /** Sends a chat message to the player, unless messages are disabled in the config. */
    private static void notify(Player player, String key, Object... args) {
        if (ByproductConfig.get().showMessages()) {
            player.displayClientMessage(Component.translatable(key, args), false);
        }
    }
}
