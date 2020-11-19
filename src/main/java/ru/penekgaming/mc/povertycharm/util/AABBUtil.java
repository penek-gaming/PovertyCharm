package ru.penekgaming.mc.povertycharm.util;

import net.minecraft.util.math.AxisAlignedBB;

public class AABBUtil {


    public static AxisAlignedBB rotateY(AxisAlignedBB in) {
        new AxisAlignedBB(0.0, 0.0, 0.0, 8 / 16.0, 1.0, 4 / 16.0);

        new AxisAlignedBB(1 - in.minX, in.minY, in.minZ,
                1.0, in.maxY, in.maxZ);


        return in;
    }
}
