package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariants;
import ru.penekgaming.mc.povertycharm.item.ItemBlockVariative;

import java.util.Objects;

@SuppressWarnings({"deprecation", "NullableProblems"})
public abstract class PovertyBlockVariative<T extends Enum<T> & IBlockVariants> extends PovertyBlock {
    private final PropertyEnum<T> variantProperty;
    private final Class<T> variantEnum;

    protected PovertyBlockVariative(String name, Material material, PropertyEnum<T> variantProperty) {
        super(name, material);

        this.variantEnum = variantProperty.getValueClass();
        this.variantProperty = variantProperty;

        setDefaultState(getBlockState().getBaseState().withProperty(variantProperty, variantEnum.getEnumConstants()[0]));
        item = new ItemBlockVariative(this).setRegistryName(Objects.requireNonNull(getRegistryName()));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(variantProperty).getMetadata();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (T v : variantEnum.getEnumConstants()) {
            items.add(new ItemStack(this, 1, v.getMetadata()));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(variantProperty, variantEnum.getEnumConstants()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(variantProperty).getMetadata();
    }

    public String getVariationName(int meta) {
        return variantEnum.getEnumConstants()[meta].getName();
    }

    public int getVariationCount() {
        return variantEnum.getEnumConstants().length;
    }

    @Override
    protected abstract BlockStateContainer createBlockState();
}
