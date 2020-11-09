package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import ru.penekgaming.mc.povertycharm.PovertyCharm;

import java.util.HashMap;
import java.util.Objects;

public class PovertyBlock extends Block {
    public Item item;

    protected PovertyBlock(String name, Material material) {
        super(material);

        setRegistryName(PovertyCharm.MOD_ID,name);
        setTranslationKey(String.format("%s.%s", PovertyCharm.MOD_ID, name));
        setCreativeTab(PovertyCharm.CREATIVE_TAB);
        item = new ItemBlock(this).setRegistryName(Objects.requireNonNull(getRegistryName()));

        PovertyBlocks.BLOCKS.put(name, this);
    }


}
