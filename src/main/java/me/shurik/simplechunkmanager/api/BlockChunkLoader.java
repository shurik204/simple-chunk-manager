package me.shurik.simplechunkmanager.api;

import net.minecraft.core.BlockPos;

public interface BlockChunkLoader extends ChunkLoader<BlockPos> {
    default BlockPos getPos() {
        return getOwner();
    }
}