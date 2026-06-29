package dev.erikcodes.ae2nobyproduct.core;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.AEProcessingPattern;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Shared byproduct-stripping logic over a single Pattern Provider's pattern inventory, used by both the
 * Byproduct Remover item and the {@code /ae2nobyproduct strip-all} command. Each PROCESSING pattern
 * with more than one output is re-encoded keeping only its first (primary) output; crafting, smithing,
 * and stonecutting patterns are left untouched.
 *
 * <p>The AE2 ({@code appeng.*}) symbols keep stable names across loaders; the only per-version
 * difference is that AE2 15.4 (1.20.1) passes the in/out stacks as {@code GenericStack[]} while AE2
 * 19.x (1.21.1) uses {@code List<GenericStack>}, guarded inline with Stonecutter {@code //?} comments.
 */
public final class ByproductStripper {
    private ByproductStripper() {}

    /**
     * Strip byproducts from one pattern inventory.
     *
     * @param apply when {@code false}, only counts how many patterns WOULD change (preview, no
     *              mutation); when {@code true}, re-encodes them in place.
     * @return the number of affected patterns. Preview and apply count identically: both validate the
     *         re-encode, apply additionally writes it back.
     */
    public static int strip(InternalInventory inv, Level level, boolean apply) {
        int count = 0;

        for (int slot = 0; slot < inv.size(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }

            IPatternDetails details = PatternDetailsHelper.decodePattern(stack, level);
            if (!(details instanceof AEProcessingPattern processing)) {
                // Only processing patterns are touched; crafting/smithing/stonecutting are skipped.
                continue;
            }

            //? if >=1.21 {
            /*java.util.List<GenericStack> sparseOutputs = processing.getSparseOutputs();
            *///?} else {
            GenericStack[] sparseOutputs = processing.getSparseOutputs();
            //?}
            GenericStack firstOutput = null;
            int outputCount = 0;
            for (GenericStack out : sparseOutputs) {
                if (out != null) {
                    outputCount++;
                    if (firstOutput == null) {
                        firstOutput = out;
                    }
                }
            }

            // Nothing to do unless there are byproducts to drop.
            if (outputCount <= 1 || firstOutput == null) {
                continue;
            }

            //? if >=1.21 {
            /*java.util.List<GenericStack> sparseInputs = processing.getSparseInputs();
            ItemStack reencoded = PatternDetailsHelper.encodeProcessingPattern(sparseInputs, java.util.List.of(firstOutput));
            *///?} else {
            GenericStack[] sparseInputs = processing.getSparseInputs();
            ItemStack reencoded = PatternDetailsHelper.encodeProcessingPattern(
                sparseInputs, new GenericStack[] { firstOutput });
            //?}
            // Never overwrite a valid pattern with a failed/empty encode; leave it untouched.
            if (reencoded == null || reencoded.isEmpty()) {
                continue;
            }
            if (apply) {
                inv.setItemDirect(slot, reencoded);
            }
            count++;
        }

        return count;
    }
}
