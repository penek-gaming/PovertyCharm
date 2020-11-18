package ru.penekgaming.mc.povertycharm.block.variant;

import net.minecraft.block.state.BlockStateContainer;

public interface IBlockVariative {
    String getVariationName(int meta);

    int getVariationCount();
}
