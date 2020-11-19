package ru.penekgaming.mc.povertycharm.init;

import net.minecraft.block.material.Material;
import ru.penekgaming.mc.povertycharm.block.*;

import java.util.HashMap;

public class PovertyBlocks {
    public static final HashMap<String, PovertyBlock> BLOCKS = new HashMap<>();

    public static final PovertyBlock ASPHALT = new PovertyBlock("asphalt", Material.ROCK);
    public static final BlockConcrete CONCRETE = new BlockConcrete("concrete");
    public static final BlockConcreteBroken CONCRETE_BROKEN = new BlockConcreteBroken();
    public static final BlockConcreteDamaged CONCRETE_DAMAGED = new BlockConcreteDamaged();
    public static final BlockBricks BRICKS = new BlockBricks();
    public static final BlockTile TILE = new BlockTile();
    public static final BlockTileBroken TILE_BROKEN = new BlockTileBroken();
    public static final BlockSmallTile SMALL_TILE = new BlockSmallTile();
    public static final PovertyBlock DEV_BLOCK = new PovertyBlock("pg", Material.WOOD);
    public static final BlockPipe PIPE = new BlockPipe("pipe", BlockPipe.Part.STRAIGHT);
    public static final BlockPipe PIPE_VALVE = new BlockPipe("pipe_valve", BlockPipe.Part.VALVE);
    public static final BlockTire TIRE = new BlockTire("tire", Material.CLOTH);
    public static final BlockTrashCan TRASH_CAN = new BlockTrashCan();
    public static final BlockHandholds HANDHOLDS = new BlockHandholds();
    public static final BlockFloorGrid FLOOR_GRID = new BlockFloorGrid();
    public static final BlockDiagonalGrid DIAGONAL_GRID = new BlockDiagonalGrid();
    public static final BlockRadiator RADIATOR = new BlockRadiator("radiator");
    public static final BlockRadiatorPipe RADIATOR_PIPE = new BlockRadiatorPipe();
    public static final BlockPallet PALLET = new BlockPallet();
    public static final BlockHatch HATCH = new BlockHatch();
    public static final BlockWindow WINDOW_VERTICAL = new BlockWindow("window_vertical", false, true);
    public static final BlockWindow WINDOW_VERTICAL_SPLIT = new BlockWindow("window_vertical_split", true, true);
    public static final BlockWindow WINDOW = new BlockWindow("window", false, false);
}
