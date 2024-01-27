package com.dementia.neurocraft.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.List;

import static com.dementia.neurocraft.server.ServerHallucinations.getPlayerEntities;

public class SRefreshClientBlockList {
    private final int[] blockPos;

    public SRefreshClientBlockList(int[] blockPos) {
        this.blockPos = blockPos;
    }

    public SRefreshClientBlockList(FriendlyByteBuf buffer) {
        this(buffer.readVarIntArray());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarIntArray(blockPos);
    }

    public void handle(CustomPayloadEvent.Context context) {
        if (context.isServerSide()) {
            BlockPos bp = new BlockPos(blockPos[0], blockPos[1], blockPos[2]);
            ServerPlayer player = context.getSender();
            if (player == null)
                return;
            Level level = player.level();
            ClientboundBlockUpdatePacket packet = new ClientboundBlockUpdatePacket(bp, level.getBlockState(bp));
            PacketHandler.sendVanillaPacket(packet, player);
        } else {
            context.setPacketHandled(false);
        }
    }

    public static int[] toIntArray(BlockPos bp) {
        return new int[]{bp.getX(), bp.getY(), bp.getZ()};
    }

}
