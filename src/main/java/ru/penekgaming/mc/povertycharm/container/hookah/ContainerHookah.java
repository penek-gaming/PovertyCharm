package ru.penekgaming.mc.povertycharm.container.hookah;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityHookah;

public class ContainerHookah extends Container {
    public final TileEntityHookah hookah;

    public ContainerHookah(InventoryPlayer inventoryPlayer, final TileEntityHookah hookah) {
        IItemHandler inventory = hookah.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);

        int invIndex = 0;

        for (int i = 0; i < 3; i++, invIndex++) {
            addSlotToContainer(new SlotItemHandlerCoal(inventory, hookah, invIndex, 88 + i * 18, 20));
        }

        for (int i = 0; i < 3; i++, invIndex++) {
            addSlotToContainer(new SlotItemHandlerPotion(inventory, hookah, invIndex, 88 + i * 18, 44));
        }

        addSlotToContainer(new SlotItemHandlerWater(inventory, hookah, invIndex, 88, 86));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 121 + i * 18));
            }
        }

        for (int k = 0; k < 9; k++) {
            addSlotToContainer(new Slot(inventoryPlayer, k, 8 + k * 18, 179));
        }

        this.hookah = hookah;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size();

            if (index < containerSlots) {
                if (!this.mergeItemStack(itemstack1, containerSlots, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        //hookah.notifyUpdate();

        return itemstack;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        ItemStack itemStack = super.slotClick(slotId, dragType, clickTypeIn, player);
        hookah.notifyUpdate();
        return itemStack;
    }
}
