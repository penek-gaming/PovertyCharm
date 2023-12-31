package ru.penekgaming.mc.povertycharm;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.penekgaming.mc.povertycharm.block.PovertyBlock;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariative;
import ru.penekgaming.mc.povertycharm.init.PovertyBlocks;
import ru.penekgaming.mc.povertycharm.init.PovertyItems;
import ru.penekgaming.mc.povertycharm.init.PovertySounds;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityBlock;
import ru.penekgaming.mc.povertycharm.util.PovertyStateMapper;

import java.util.Objects;

@Mod.EventBusSubscriber
public class PovertyRegistry {
    @SubscribeEvent
    public static void addBlocks(RegistryEvent.Register<Block> event) {
        PovertyCharm.LOGGER.info("Registering blocks");
        for (PovertyBlock block : PovertyBlocks.BLOCKS) {
            event.getRegistry().register(block);
            ForgeRegistries.ITEMS.register(block.item);
            if (block instanceof TileEntityBlock)
                GameRegistry.registerTileEntity(
                        ((TileEntityBlock<?>) block).getTileEntityClass(),
                        Objects.requireNonNull(block.getRegistryName())
                );
        }

        PovertyCharm.LOGGER.info("{} blocks should be registered automatically", PovertyBlocks.BLOCKS.size());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerColors(ColorHandlerEvent.Block event) {

    }

    @SubscribeEvent
    public static void addSounds(RegistryEvent.Register<SoundEvent> event) {
        PovertyCharm.LOGGER.info("Registering sounds");
        event.getRegistry().registerAll(PovertySounds.SOUNDS.values().toArray(new SoundEvent[0]));
        PovertyCharm.LOGGER.info("{} sounds should be registered automatically", PovertySounds.SOUNDS.size());
    }

    @SubscribeEvent
    public static void addItems(RegistryEvent.Register<Item> event) {
        PovertyCharm.LOGGER.info("Registering items");

        for (Item item : PovertyItems.ITEM_LIST) {
            event.getRegistry().register(item);
        }

        PovertyCharm.LOGGER.info("{} items should be registered automatically", PovertyItems.ITEM_LIST.size());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(ModelRegistryEvent event) {
        PovertyCharm.LOGGER.info("Registering models");
        for (PovertyBlock block : PovertyBlocks.BLOCKS) {
            if (block.usesCustomStateMapper())
                ModelLoader.setCustomStateMapper(block, new PovertyStateMapper());

            if (!(block instanceof IBlockVariative))
                continue;

            IBlockVariative blockVariative = (IBlockVariative) block;
            for (int i = 0; i < blockVariative.getVariationCount(); i++) {
                ModelLoader.setCustomModelResourceLocation(block.item, i,
                        new ModelResourceLocation(
                                String.format("%s_%s",
                                        Objects.requireNonNull(block.getRegistryName()).toString(),
                                        blockVariative.getVariationName(i)
                                ),
                                "inventory"
                        )
                );
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerRenderers() {
        PovertyCharm.LOGGER.info("Registering item block renderers");

        for (PovertyBlock block : PovertyBlocks.BLOCKS) {
            if (block instanceof IBlockVariative) {
                registerVariativeBlockRenderers(block);
            } else {
                registerBlockRenderer(block);
            }
        }

        for (Item item : PovertyItems.ITEM_LIST) {
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                    .register(item, 0, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory"));

        }
    }

    @SideOnly(Side.CLIENT)
    private static void registerBlockRenderer(PovertyBlock block) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                .register(
                        block.item,
                        0,
                        new ModelResourceLocation(Objects.requireNonNull(block.getRegistryName()), "inventory")
                );
    }

    @SideOnly(Side.CLIENT)
    private static void registerVariativeBlockRenderers(PovertyBlock block) {
        IBlockVariative blockVariative = (IBlockVariative) block;

        for (int i = 0; i < blockVariative.getVariationCount(); i++) {
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                    .register(
                            block.item,
                            i,
                            new ModelResourceLocation(
                                    String.format("%s_%s",
                                            Objects.requireNonNull(block.getRegistryName()).toString(),
                                            blockVariative.getVariationName(i)
                                    ),
                                    "inventory"
                            )
                    );
        }
    }
}
