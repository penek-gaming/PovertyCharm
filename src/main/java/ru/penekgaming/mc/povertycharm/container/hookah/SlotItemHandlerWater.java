package ru.penekgaming.mc.povertycharm.container.hookah;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityHookah;

import javax.annotation.Nonnull;
import java.util.Objects;

public class SlotItemHandlerWater extends SlotItemHandler {
    private final TileEntityHookah hookah;

    public SlotItemHandlerWater(IItemHandler itemHandler, final TileEntityHookah hookah, int index, int xPosition, int yPosition) {
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
        if (!super.isItemValid(stack))
            return false;

        return validateStack(stack);
    }

    public static boolean validateStack(@Nonnull ItemStack stack) {
        if (stack.getItem() == Items.WATER_BUCKET)
            return true;

        if (stack.getItem() instanceof UniversalBucket) {
            UniversalBucket bucket = (UniversalBucket) stack.getItem();

            return Objects.requireNonNull(bucket.getFluid(stack)).getFluid() == FluidRegistry.WATER
                    && Objects.requireNonNull(bucket.getFluid(stack)).amount >= 1000;
        }

        return false;
    }

    @Override
    public void onSlotChanged() {
        hookah.markDirty();
    }
}
