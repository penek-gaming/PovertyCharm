package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.material.Material;

import java.util.HashMap;

public class PovertyBlocks {
    public static final HashMap<String, PovertyBlock> BLOCKS = new HashMap<>();

    public static final PovertyBlock ASPHALT = new PovertyBlock("asphalt", Material.ROCK);
    public static final ConcreteBlock CONCRETE = new ConcreteBlock();
    public static final BricksBlock BRICKS = new BricksBlock();
    public static final TileBlock TILE = new TileBlock();
    public static final SmallTileBlock SMALL_TILE = new SmallTileBlock();
    public static final PovertyBlock PENEK_GAMING = new PovertyBlock("pg", Material.WOOD);
    public static final PipeBlock PIPE = new PipeBlock("pipe", PipeBlock.Part.STRAIGHT);
    public static final PipeBlock PIPE_VALVE = new PipeBlock("pipe_valve", PipeBlock.Part.VALVE);
    public static final TireBlock TIRE = new TireBlock("tire", Material.CLOTH);
}
