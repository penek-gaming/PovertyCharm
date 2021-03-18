package ru.penekgaming.mc.povertycharm.util;

public class MetaSet {
    private final int count;
    private final int[] lens;

    public MetaSet(int... valueLen) {
        count = valueLen.length;
        int len = 0;

        for (int i : valueLen) {
            len += i;
        }

        if (len > 4)
            throw new IllegalArgumentException("Maximum sum of lengths must not be greater than 4");

        lens = valueLen;
    }

    public int toMeta(int... values) {
        int value = 0, offset = 0;

        if (count != values.length)
            throw new IllegalArgumentException(
                    String.format("This set works with only %d values. %d given", count, values.length)
            );

        for (int i = 0; i < values.length; i++) {
            if (values[i] >= Math.pow(2, lens[i]))
                throw new IllegalArgumentException(
                        String.format("Length of value %d must be not greater than %d!", i, lens[i])
                );

            value |= (int) Math.pow(2, offset) * values[i];
            offset += lens[i];
        }

        return value;
    }

    public int[] fromMeta(int meta) {
        int[] values = new int[count];
        int offset = 0;

        for (int i = 0; i < count; i++) {
            int mask = ((int) Math.pow(2, lens[i]) - 1) * (int) Math.pow(2, offset);
            values[i] = (meta & mask) / (int) Math.pow(2, offset);
            offset += lens[i];
        }

        return values;
    }
}
