package io.github.projectet.ae2things.storage;

import java.util.List;

import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.item.DISKDrive;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.IncludeExclude;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.core.localization.GuiText;
import appeng.core.localization.Tooltips;

public class DISKCellHandler implements ICellHandler {

    public static final DISKCellHandler INSTANCE = new DISKCellHandler();

    @Override
    public boolean isCell(ItemStack is) {
        return is.getItem() instanceof DISKDrive;
    }

    @Override
    public DISKCellInventory getCellInventory(ItemStack is, ISaveProvider container) {
        return DISKCellInventory.createInventory(is, container, AE2Things.currentStorageManager());
    }

    public void addCellInformationToTooltip(ItemStack stack, List<Component> lines) {
        // Explicitly don't pass a storage manager since this only needs info stored on the item
        var handler = DISKCellInventory.createInventory(stack, null, null);

        if (handler == null)
            return;

        if (handler.hasDiskUUID()) {
            lines.add(Component.literal("Disk UUID: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(handler.getDiskUUID().toString()).withStyle(ChatFormatting.AQUA)));
            lines.add(Tooltips.bytesUsed(handler.getNbtItemCount(), handler.getTotalBytes()));
        }

        if (handler.isPreformatted()) {
            var list = (handler.getPartitionListMode() == IncludeExclude.WHITELIST ? GuiText.Included
                    : GuiText.Excluded)
                    .text();

            if (handler.isFuzzy()) {
                lines.add(GuiText.Partitioned.withSuffix(" - ").append(list).append(" ").append(GuiText.Fuzzy.text()));
            } else {
                lines.add(
                        GuiText.Partitioned.withSuffix(" - ").append(list).append(" ").append(GuiText.Precise.text()));
            }
        }
    }
}
