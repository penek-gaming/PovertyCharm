package ru.penekgaming.mc.povertycharm.block.variant;

import net.minecraft.util.IStringSerializable;

public interface IBlockVariants extends IStringSerializable {
    String toString();
    int getMetadata();
}
