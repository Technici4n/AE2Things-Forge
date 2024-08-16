package io.github.projectet.ae2things.mixin;

import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.util.DataStorage;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(AbstractContainerMenu.class)
public abstract class CursedInternalSlotMixin {

    @Final
    @Shadow
    public NonNullList<Slot> slots;

    @Inject(method = "doClick", at = @At(value = "INVOKE", target = "net/minecraft/world/item/ItemStack.copyWithCount (I)Lnet/minecraft/world/item/ItemStack;"), slice = @Slice(from = @At(value = "INVOKE", target = "net/minecraft/world/inventory/Slot.hasItem()Z", ordinal = 1)), cancellable = true)
    public void CLONE(int slotIndex, int button, ClickType actionType, Player player, CallbackInfo ci) {
        Slot i = this.slots.get(slotIndex);

        var diskId = i.getItem().get(AE2Things.DATA_DISK_ID);
        if (diskId != null) {
            DataStorage storage = AE2Things.STORAGE_INSTANCE.getOrCreateDisk(diskId);
            ItemStack newStack = new ItemStack(i.getItem().getItem());
            UUID id = UUID.randomUUID();
            newStack.set(AE2Things.DATA_DISK_ID, id);
            newStack.set(AE2Things.DATA_DISK_ITEM_COUNT, storage.itemCount);
            AE2Things.STORAGE_INSTANCE.updateDisk(id, storage);

            newStack.setCount(newStack.getMaxStackSize());
            this.setCarried(newStack);
            ci.cancel();
        }
    }

    @Shadow
    public abstract void setCarried(ItemStack slot);
}
