package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

@SuppressWarnings({"deprecation", "NullableProblems"})
public abstract class PovertyBlockMeta<T extends Comparable<T>> extends PovertyBlock {
    protected PovertyBlockMeta(String name, Material material, IProperty<T> property, T defaultValue) {
        super(name, material);

        setDefaultState(getBlockState().getBaseState().withProperty(property, defaultValue));
    }

    @Override
    public abstract int damageDropped(IBlockState state);

    @Override
    public abstract void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items);

    @Override
    public abstract IBlockState getStateFromMeta(int meta);

    @Override
    public abstract int getMetaFromState(IBlockState state);

    @Override
    protected abstract BlockStateContainer createBlockState();
}