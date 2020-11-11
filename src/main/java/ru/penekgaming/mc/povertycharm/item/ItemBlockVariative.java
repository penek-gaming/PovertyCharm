package ru.penekgaming.mc.povertycharm.item;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import ru.penekgaming.mc.povertycharm.block.PovertyBlock;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariative;

@SuppressWarnings({"NullableProblems"})
public class ItemBlockVariative extends ItemBlock {
    public ItemBlockVariative(PovertyBlock block) {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return String.format("%s_%s", block.getTranslationKey(), ((IBlockVariative) block).getVariationName(stack.getItemDamage()));
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }
}
