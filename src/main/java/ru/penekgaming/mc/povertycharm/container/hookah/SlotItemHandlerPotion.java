package ru.penekgaming.mc.povertycharm.container.hookah;

import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityHookah;

import javax.annotation.Nonnull;

public class SlotItemHandlerPotion extends SlotItemHandler {
    private final TileEntityHookah hookah;

    public SlotItemHandlerPotion(IItemHandler itemHandler, final TileEntityHookah hookah, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.hookah = hookah;
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack) {
        return 1;
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return super.isItemValid(stack) && validateStack(stack);
    }

    public static boolean validateStack(@Nonnull ItemStack stack) {
        return (stack.getItem() instanceof ItemPotion) && PotionUtils.getEffectsFromStack(stack).size() > 0;
    }

    @Override
    public void onSlotChanged() {
        hookah.markDirty();
    }
}
