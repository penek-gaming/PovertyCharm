package ru.penekgaming.mc.povertycharm.message.handler;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ru.penekgaming.mc.povertycharm.container.hookah.ContainerHookah;
import ru.penekgaming.mc.povertycharm.message.model.MessageHookahGui;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityHookah;

public class MessageHandlerHookahGui implements IMessageHandler<MessageHookahGui, IMessage> {
    @Override
    public IMessage onMessage(MessageHookahGui message, MessageContext ctx) {
        if (!(ctx.getServerHandler().player.openContainer instanceof ContainerHookah))
            return null;

        TileEntityHookah hookah = message.getHookah(ctx.getServerHandler().player.world);

        switch (message.getButtonId()) {
            case 0:
                if (hookah.isActive())
                    hookah.reset();
                else
                    hookah.activate();
                break;
        }


        return null;
    }
}
