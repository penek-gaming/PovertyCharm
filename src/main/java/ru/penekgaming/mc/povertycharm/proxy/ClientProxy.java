package ru.penekgaming.mc.povertycharm.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ru.penekgaming.mc.povertycharm.PovertyRegistry;
import ru.penekgaming.mc.povertycharm.gui.GuiIntercom;
import ru.penekgaming.mc.povertycharm.handler.ClientEventHandlers;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        PovertyRegistry.registerRenderers();
        MinecraftForge.EVENT_BUS.register(ClientEventHandlers.class);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Override
    public void showGui(BlockPos pos) {
        Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiIntercom(pos)));
    }
}
