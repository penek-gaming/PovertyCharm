package ru.penekgaming.mc.povertycharm.block.variant;

public enum ConcreteVariants implements IBlockVariants {
    GRAY(0, "gray"),
    GRAY_CRACKED(1, "gray_cracked"),

    WHITE(2, "white"),
    WHITE_CRACKED(3, "white_cracked"),

    GREEN(4, "green"),
    GREEN_CRACKED(5, "green_cracked");

    private final int meta;
    private final String name;

    ConcreteVariants(int meta, String name) {
        this.meta = meta;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMetadata() {
        return meta;
    }
}
