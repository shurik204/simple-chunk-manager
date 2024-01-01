package me.shurik.simplechunkmanager.api;

import it.unimi.dsi.fastutil.longs.LongSet;
import me.shurik.simplechunkmanager.impl.BlockChunkLoaderImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

public interface BlockChunkLoader {
    BlockPos getPos();
    ResourceLocation getId();
    LongSet getForcedChunks();
    TicketType<ChunkPos> createTicketType();
    void submitTickets(ServerLevel level);
    void withdrawTickets(ServerLevel level);
//    TODO: allow adding and removing chunks
//    boolean addChunk(long chunkPos);
//    boolean removeChunk(long chunkPos);
//    default boolean addChunk(ChunkPos chunkPos) {
//        return addChunk(chunkPos.toLong());
//    }
//    default boolean removeChunk(ChunkPos chunkPos) {
//        return removeChunk(chunkPos.toLong());
//    }
    default String getStringId() {
        return getId().toString();
    }

    void writeSaveData(CompoundTag tag);
    static BlockChunkLoader loadSaveData(CompoundTag tag) {
        return BlockChunkLoaderImpl.loadSaveData(tag);
    }
}
