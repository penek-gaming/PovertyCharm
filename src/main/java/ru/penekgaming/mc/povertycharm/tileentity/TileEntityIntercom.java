package ru.penekgaming.mc.povertycharm.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.penekgaming.mc.povertycharm.PovertyCharm;

import java.util.UUID;

@SuppressWarnings("NullableProblems")
public class TileEntityIntercom extends TileEntity {
    public static final String DEFAULT_CODE = "NULL";

    private String code = DEFAULT_CODE;
    private UUID uuid = UUID.randomUUID();
    private UUID owner = UUID.randomUUID();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        markDirty();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
        markDirty();
    }

    public boolean isPublic() {
        return code.equals(DEFAULT_CODE);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setString("code", code);
        tagCompound.setUniqueId("uuid", uuid);
        tagCompound.setUniqueId("block_owner", owner);

        return super.writeToNBT(tagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        code = tagCompound.getString("code");
        uuid = tagCompound.getUniqueId("uuid");
        owner = tagCompound.getUniqueId("block_owner");
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        markDirty();
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return false;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();

        PovertyCharm.LOGGER.warn("getUpdateTag");

        compound.setString("code", code.equals(DEFAULT_CODE) ? DEFAULT_CODE : "PSWD");

        return compound;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        PovertyCharm.LOGGER.warn("handleUpdateTag");
        code = tag.getString("code");
    }

//    @Override
//    public SPacketUpdateTileEntity getUpdatePacket() {
//        PovertyCharm.LOGGER.warn("getUpdatePacket");
//        NBTTagCompound nbtTagCompound = new NBTTagCompound();
//        nbtTagCompound.setString("code", code.equals(DEFAULT_CODE) ? DEFAULT_CODE : "PSWD");
//        return new SPacketUpdateTileEntity(pos, 0, nbtTagCompound);
//    }
//
//    @Override
//    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
//        PovertyCharm.LOGGER.warn("onDataPacket");
//        code = pkt.getNbtCompound().getString("code");
//    }
}
