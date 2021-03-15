package ru.penekgaming.mc.povertycharm.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import ru.penekgaming.mc.povertycharm.block.BlockComputer;
import ru.penekgaming.mc.povertycharm.block.BlockTire;

public class TileEntityComputer extends TileEntity {
    private BlockComputer.Variant variant;

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger(BlockComputer.VARIANT.getName(), variant.getMetadata());

        return super.writeToNBT(tagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        variant = BlockComputer.Variant.values()[tagCompound.getInteger(BlockComputer.VARIANT.getName())];
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        return writeToNBT(compound);
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        writeToNBT(nbtTagCompound);
        return new SPacketUpdateTileEntity(pos, 0, nbtTagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    public BlockComputer.Variant getVariant() {
        return variant;
    }

    public void setVariant(BlockComputer.Variant variant) {
        this.variant = variant;
        markDirty();
    }
}
