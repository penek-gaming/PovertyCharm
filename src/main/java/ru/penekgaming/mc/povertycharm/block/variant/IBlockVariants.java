package ru.penekgaming.mc.povertycharm.block.variant;

import net.minecraft.util.IStringSerializable;

import java.util.HashMap;

public interface IBlockVariants extends IStringSerializable {
    String toString();
    int getMetadata();
}
