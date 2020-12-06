package ru.penekgaming.mc.povertycharm.message.handler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ru.penekgaming.mc.povertycharm.init.PovertySounds;
import ru.penekgaming.mc.povertycharm.message.model.MessageBeep;

public class MessageHandlerBeep implements IMessageHandler<MessageBeep, IMessage> {
    @Override
    public IMessage onMessage(MessageBeep message, MessageContext ctx) {
        if (MessageHandlerIntercom.isInvalidIntercom(message.getBlockPos(), ctx))
            return null;

        EntityPlayerMP player = ctx.getServerHandler().player;

        player.world.playSound(
                null,
                message.getBlockPos(),
                PovertySounds.INTERCOM_BEEP,
                SoundCategory.BLOCKS,
                0.5f,
                1.0f);

        return null;
    }
}
