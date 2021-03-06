package darkevilmac.archimedes.network;

import cpw.mods.fml.relauncher.Side;
import darkevilmac.archimedes.entity.ShipAssemblyInteractor;
import darkevilmac.archimedes.gui.ContainerHelm;
import darkevilmac.movingworld.chunk.AssembleResult;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class AssembleResultMessage extends ArchimedesShipsMessage {
    public AssembleResult result;
    public ShipAssemblyInteractor interactor;
    public boolean prevFlag;

    public AssembleResultMessage() {
        result = null;
        prevFlag = false;
    }

    public AssembleResultMessage(AssembleResult compileResult, boolean prev) {
        result = compileResult;
        prevFlag = prev;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf, Side side) {
        buf.writeBoolean(prevFlag);
        if (result == null) {
            buf.writeByte(AssembleResult.RESULT_NONE);
        } else {
            buf.writeByte(result.getCode());
            buf.writeInt(result.getBlockCount());
            buf.writeInt(result.getTileEntityCount());
            buf.writeFloat(result.getMass());
            result.assemblyInteractor.toByteBuf(buf);
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf, EntityPlayer player, Side side) {
        if (side.isClient()) {
            prevFlag = buf.readBoolean();
            byte resultCode = buf.readByte();
            result = new AssembleResult(resultCode, buf);
            result.assemblyInteractor = new ShipAssemblyInteractor().fromByteBuf(resultCode, buf);
        }
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        if (player.openContainer instanceof ContainerHelm) {
            if (prevFlag) {
                ((ContainerHelm) player.openContainer).tileEntity.setPrevAssembleResult(result);
                ((ContainerHelm) player.openContainer).tileEntity.getPrevAssembleResult().assemblyInteractor = result.assemblyInteractor;
            } else {
                ((ContainerHelm) player.openContainer).tileEntity.setAssembleResult(result);
                ((ContainerHelm) player.openContainer).tileEntity.getAssembleResult().assemblyInteractor = result.assemblyInteractor;
            }
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
    }

}

