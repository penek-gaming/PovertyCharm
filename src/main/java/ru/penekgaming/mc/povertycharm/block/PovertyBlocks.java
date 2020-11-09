package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.material.Material;

import java.util.HashMap;

public class PovertyBlocks {
    public static final HashMap<String, PovertyBlock> BLOCKS = new HashMap<>();

    public static final PovertyBlock ASPHALT = new PovertyBlock("asphalt", Material.ROCK);
    public static final ConcreteBlock CONCRETE = new ConcreteBlock();
    public static final BricksBlock BRICKS = new BricksBlock();
    public static final TileBlock TILE = new TileBlock();
}
