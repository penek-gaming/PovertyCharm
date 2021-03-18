package ru.penekgaming.mc.povertycharm.proxy;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import ru.penekgaming.mc.povertycharm.PovertyCharm;
import ru.penekgaming.mc.povertycharm.handler.GuiHandler;
import ru.penekgaming.mc.povertycharm.message.handler.MessageHandlerBeep;
import ru.penekgaming.mc.povertycharm.message.handler.MessageHandlerHookahGui;
import ru.penekgaming.mc.povertycharm.message.handler.MessageHandlerIntercom;
import ru.penekgaming.mc.povertycharm.message.model.MessageBeep;
import ru.penekgaming.mc.povertycharm.message.model.MessageHookahGui;
import ru.penekgaming.mc.povertycharm.message.model.MessageIntercom;

public abstract class CommonProxy {
    @SuppressWarnings("UnusedAssignment")
    public void preInit(FMLPreInitializationEvent event) {
        int descriptor = 0;
        PovertyCharm.NETWORK_WRAPPER.registerMessage(MessageHandlerIntercom.class, MessageIntercom.class, descriptor++, Side.SERVER);
        PovertyCharm.NETWORK_WRAPPER.registerMessage(MessageHandlerBeep.class, MessageBeep.class, descriptor++, Side.SERVER);
        PovertyCharm.NETWORK_WRAPPER.registerMessage(MessageHandlerHookahGui.class, MessageHookahGui.class, descriptor++, Side.SERVER);

        NetworkRegistry.INSTANCE.registerGuiHandler(PovertyCharm.INSTANCE, new GuiHandler());
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    public void showGui(BlockPos pos) {
    }
}