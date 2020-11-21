package ru.penekgaming.mc.povertycharm.init;

import net.minecraft.block.material.Material;
import ru.penekgaming.mc.povertycharm.block.*;

import java.util.ArrayList;
import java.util.List;

public class PovertyBlocks {
    public static final List<PovertyBlock> BLOCKS = new ArrayList<>();

    public static final PovertyBlock ASPHALT;
    public static final BlockConcrete CONCRETE;
    public static final BlockConcreteBroken CONCRETE_BROKEN;
    public static final BlockConcreteDamaged CONCRETE_DAMAGED;
    public static final BlockBricks BRICKS;
    public static final BlockTile TILE;
    public static final BlockTileBroken TILE_BROKEN;
    public static final BlockSmallTile SMALL_TILE;
    public static final PovertyBlock DEV_BLOCK;
    public static final BlockPipe PIPE;
    public static final BlockPipe PIPE_VALVE;
    public static final BlockTire TIRE;
    public static final BlockTrashCan TRASH_CAN;
    public static final BlockHandholds HANDHOLDS;
    public static final BlockFloorGrid FLOOR_GRID;
    public static final BlockDiagonalGrid DIAGONAL_GRID;
    public static final BlockRadiator RADIATOR;
    public static final BlockRadiatorPipe RADIATOR_PIPE;
    public static final BlockPallet PALLET;
    public static final BlockHatch HATCH;
    public static final BlockWindow WINDOW_VERTICAL;
    public static final BlockWindow WINDOW_VERTICAL_SPLIT;
    public static final BlockWindow WINDOW;
    public static final BlockRibbedPanel RIBBED_PANEL;
    public static final BlockWires WIRES;
    public static final BlockConcreteSlab CONCRETE_SLAB;

    static {
        ASPHALT = new PovertyBlock("asphalt", Material.ROCK);

        CONCRETE = new BlockConcrete("concrete");
        CONCRETE_BROKEN = new BlockConcreteBroken();
        CONCRETE_DAMAGED = new BlockConcreteDamaged();
        CONCRETE_SLAB = new BlockConcreteSlab();

        TILE = new BlockTile();
        TILE_BROKEN = new BlockTileBroken();
        SMALL_TILE = new BlockSmallTile();
        BRICKS = new BlockBricks();

        HANDHOLDS = new BlockHandholds();
        FLOOR_GRID = new BlockFloorGrid();
        DIAGONAL_GRID = new BlockDiagonalGrid();

        RADIATOR = new BlockRadiator("radiator");
        RADIATOR_PIPE = new BlockRadiatorPipe();

        WINDOW = new BlockWindow("window", false, false);
        WINDOW_VERTICAL_SPLIT = new BlockWindow("window_vertical_split", true, true);
        WINDOW_VERTICAL = new BlockWindow("window_vertical", false, true);

        PIPE = new BlockPipe("pipe", BlockPipe.Part.STRAIGHT);
        PIPE_VALVE = new BlockPipe("pipe_valve", BlockPipe.Part.VALVE);

        HATCH = new BlockHatch();
        PALLET = new BlockPallet();
        RIBBED_PANEL = new BlockRibbedPanel();
        DEV_BLOCK = new PovertyBlock("pg", Material.WOOD);
        TRASH_CAN = new BlockTrashCan();
        TIRE = new BlockTire("tire", Material.CLOTH);
        WIRES = new BlockWires();
    }
}
