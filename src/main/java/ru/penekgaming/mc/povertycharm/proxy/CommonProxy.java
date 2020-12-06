package ru.penekgaming.mc.povertycharm.proxy;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import ru.penekgaming.mc.povertycharm.PovertyCharm;
import ru.penekgaming.mc.povertycharm.message.handler.MessageHandlerIntercom;
import ru.penekgaming.mc.povertycharm.message.model.MessageIntercom;

public abstract class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        int descriptor = 0;
        PovertyCharm.NETWORK_WRAPPER.registerMessage(MessageHandlerIntercom.class, MessageIntercom.class, descriptor++, Side.SERVER);
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    public void showGui(BlockPos pos){}
}