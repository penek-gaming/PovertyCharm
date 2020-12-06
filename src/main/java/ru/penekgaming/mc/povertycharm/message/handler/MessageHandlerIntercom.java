package ru.penekgaming.mc.povertycharm.message.handler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import ru.penekgaming.mc.povertycharm.PovertyCharm;
import ru.penekgaming.mc.povertycharm.block.BlockIntercom;
import ru.penekgaming.mc.povertycharm.message.model.MessageIntercom;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityIntercom;

public class MessageHandlerIntercom implements IMessageHandler<MessageIntercom, IMessage> {
    @Override
    public IMessage onMessage(MessageIntercom message, MessageContext ctx) {
        if(ctx.side == Side.CLIENT)
            return null;

        EntityPlayerMP player = ctx.getServerHandler().player;

        if(player.getDistance(message.getBlockPos().getX(), message.getBlockPos().getY(), message.getBlockPos().getZ()) > 10)
            return null;

        IBlockState blockState = player.world.getBlockState(message.getBlockPos());

        if(!(blockState.getBlock() instanceof BlockIntercom))
            return null;

        BlockIntercom intercom = (BlockIntercom) blockState.getBlock();

        TileEntityIntercom entityIntercom = intercom.getTileEntity(player.world, message.getBlockPos());

        if(entityIntercom != null
                && (entityIntercom.isPublic())
                && entityIntercom.getOwner().compareTo(player.getUniqueID()) == 0)
            entityIntercom.setCode(message.getCode().equals("ESCD") ? TileEntityIntercom.DEFAULT_CODE : message.getCode());

        if(!message.getCode().equals("ESCD"))
            intercom.openByCode(player.world, message.getBlockPos(), blockState, message.getCode());

        PovertyCharm.LOGGER.warn(message.getCode());

        return null;
    }
}
