package ru.penekgaming.mc.povertycharm.block.variant;

public enum BricksVariants implements IBlockVariants {
    DEFAULT(0, "default"),
    WHITE(0, "white");

    private final int meta;
    private final String name;

    BricksVariants(int meta, String name) {
        this.meta = meta;
        this.name = name;
    }

    @Override
    public int getMetadata() {
        return meta;
    }

    @Override
    public String getName() {
        return name;
    }
}
