package ru.penekgaming.mc.povertycharm;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import ru.penekgaming.mc.povertycharm.util.RandomCollection;

import java.util.HashMap;

import static net.minecraftforge.common.config.Config.*;

@net.minecraftforge.common.config.Config(modid = PovertyCharm.MOD_ID)
public class Config {
    @RequiresMcRestart
    public static TrashCan trashCan = new TrashCan();

    public static class TrashCan {
        @Name("XP Drop Chance")
        @Comment("If set to 0, items will never drop")
        @RangeDouble(min = 0.0, max = 1.0)
        public double expDropChance = 0.5;

        @Name("Min XP Drop")
        @RangeInt(min = 0)
        public int minExpDrop = 1;

        @Name("Max XP Drop")
        @Comment("If set to 0, XP will never drop")
        @RangeInt(min = 0)
        public int maxExpDrop = 10;

        @Name("Item Drop Chance")
        @Comment("If set to 0, items will never drop")
        @RangeDouble(min = 0.0, max = 1.0)
        public double dropChance = 0.85;

        @Name("Item Sets Chances")
        @Comment({"Set chance of dropping item set",
                "First value is an ID of set",
                "Second value is weight (not chance) from 0.0(0)1 to 0.9(9)"})
        public HashMap<String, Double> itemSetsDropChances = new HashMap<String, Double>() {{
            put("shit", 0.75);
            put("rich", 0.005);
        }};

        @Name("Item Sets")
        @Comment({"Item sets that may be dropped",
                "First value is an ID of set",
                "Second is the list of items that will be dropped",
                "Format is: \"item_registry_name_1, max_drop_count_1; item_registry_name_2, max_drop_count_2;\"",
                "If drop count not provided, 1 will be dropped"})
        public HashMap<String, String> itemSets = new HashMap<String, String>() {{
            put("shit", "minecraft:dirt, 3; minecraft:sand, 3");
            put("rich", "minecraft:diamond");
        }};

        public RandomCollection<HashMap<Item, Integer>> getItemRandomCollection() {
            RandomCollection<HashMap<Item, Integer>> itemRandomCollection = new RandomCollection<>();

            itemSetsDropChances.forEach((setId, weight) -> {
                if (!itemSets.containsKey(setId))
                    return;

                String setStr = itemSets.get(setId).replace(" ", "");

                String[] itemsStr = setStr.split(";");
                if (itemsStr.length <= 0)
                    return;

                HashMap<Item, Integer> itemSet = null;

                for (String itemStr : itemsStr) {
                    String[] itemNameCount = itemStr.split(",");

                    if (itemNameCount.length <= 0 || itemNameCount[0].isEmpty())
                        continue;

                    int maxCount = 1;

                    if (itemNameCount.length > 1 && !itemNameCount[1].isEmpty()) {
                        try {
                            maxCount = Math.max(1, Integer.parseInt(itemNameCount[1]));
                        } catch (Exception ignored) {
                        }
                    }

                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemNameCount[0]));
                    if (item == null)
                        continue;

                    if (itemSet == null)
                        itemSet = new HashMap<>();

                    try {
                        itemSet.put(item, Math.max(0, maxCount));
                    } catch (Exception ignored) {
                    }
                }

                if (itemSet == null)
                    return;

                itemRandomCollection.add(Math.max(Double.MIN_VALUE, Math.min(1.0 - Double.MIN_VALUE, weight)), itemSet);
            });

            return itemRandomCollection;
        }
    }
}
