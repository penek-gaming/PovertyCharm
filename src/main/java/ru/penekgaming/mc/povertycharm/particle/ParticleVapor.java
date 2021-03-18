package ru.penekgaming.mc.povertycharm.particle;

import net.minecraft.client.particle.ParticleSmokeLarge;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleVapor extends ParticleSmokeLarge {
    protected ParticleVapor(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double p_i1201_8_, double p_i1201_10_, double p_i1201_12_) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, p_i1201_8_, p_i1201_10_, p_i1201_12_);

    }
}
