package com.dementia.neurocraft.network;

import com.dementia.neurocraft.client.ClientBlockVerify;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.*;

import static com.dementia.neurocraft.client.ClientBlockVerify.hallucinationBlocks;
import static com.dementia.neurocraft.client.ClientBlockVerify.removeHallucinationBlocks;
import static com.dementia.neurocraft.client.ClientHallucinations.playerEntities;

public class CHallBlockListUpdatePacket {
    private final int[] blockPosList;

    public CHallBlockListUpdatePacket(int[] blockPosList) {
        this.blockPosList = blockPosList;
    }

    public CHallBlockListUpdatePacket(FriendlyByteBuf buffer) {
        this(buffer.readVarIntArray());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarIntArray(blockPosList);
    }

    public void handle(CustomPayloadEvent.Context context) {
        if (context.isClientSide()) {
            hallucinationBlocks.addAll(decode(blockPosList));
            if (hallucinationBlocks.size() >= 5) {
                var player = Minecraft.getInstance().player;
                var blockPos = hallucinationBlocks.get(0);

                if (player == null)
                    return;

                removeHallucinationBlocks(blockPos, player);
            }
        } else {
            context.setPacketHandled(false);
        }
    }

    public ArrayList<BlockPos> decode(int[] positions) {
        ArrayList<BlockPos> blockList = new ArrayList<>();
        int x = 0, y = 0, z;
        int c = 1;
        for (int num : positions) {
            if (c % 3 == 1)
                x = num;
            if (c % 3 == 2)
                y = num;
            if (c % 3 == 0) {
                z = num;
                blockList.add(new BlockPos(x, y, z));
            }
            c++;
        }
        return blockList;
    }
}
