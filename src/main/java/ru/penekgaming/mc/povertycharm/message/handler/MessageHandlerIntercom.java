package ru.penekgaming.mc.povertycharm.message.handler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import ru.penekgaming.mc.povertycharm.block.BlockIntercom;
import ru.penekgaming.mc.povertycharm.message.model.MessageIntercom;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityIntercom;

public class MessageHandlerIntercom implements IMessageHandler<MessageIntercom, IMessage> {
    public static boolean isInvalidIntercom(BlockPos pos, MessageContext ctx) {
        if (ctx.side == Side.CLIENT)
            return true;

        EntityPlayerMP player = ctx.getServerHandler().player;

        if (player.getDistance(pos.getX(), pos.getY(), pos.getZ()) > 10)
            return true;

        IBlockState blockState = player.world.getBlockState(pos);

        return !(blockState.getBlock() instanceof BlockIntercom)
                && blockState.getValue(BlockIntercom.STATE) != BlockIntercom.State.CLOSED;
    }

    @Override
    public IMessage onMessage(MessageIntercom message, MessageContext ctx) {
        if (isInvalidIntercom(message.getBlockPos(), ctx))
            return null;

        EntityPlayerMP player = ctx.getServerHandler().player;
        IBlockState blockState = player.world.getBlockState(message.getBlockPos());

        BlockIntercom intercom = (BlockIntercom) blockState.getBlock();

        TileEntityIntercom entityIntercom = intercom.getTileEntity(player.world, message.getBlockPos());

        if (entityIntercom != null
                && (entityIntercom.isPublic())
                && entityIntercom.getOwner().compareTo(player.getUniqueID()) == 0) {
            entityIntercom.setCode(message.getCode().equals("ESCD") ? TileEntityIntercom.DEFAULT_CODE : message.getCode());
            intercom.open(player.world, message.getBlockPos(), blockState);
        }

        if (!message.getCode().equals("ESCD"))
            intercom.openByCode(player.world, message.getBlockPos(), blockState, message.getCode());

        return null;
    }
}
