package me.shurik.simplechunkmanager.impl;

import me.shurik.simplechunkmanager.api.BlockChunkLoader;
import me.shurik.simplechunkmanager.api.ChunkLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.Comparator;

@ApiStatus.Internal
public class BlockChunkLoaderImpl extends ChunkLoaderImpl<BlockPos> implements BlockChunkLoader {
    // Ticket type must be a constant, otherwise comparison (TicketType == other) in Ticket.equals will fail
    private static final TicketType<ChunkLoader<BlockPos>> TICKET_TYPE = TicketType.create("scm:block_chunk_loader", Comparator.comparing(info -> info));
    protected static final String BLOCK_POS_KEY = "block";

    public BlockChunkLoaderImpl(String modId, BlockPos owner) {
        super(modId, owner, new ChunkPos(owner));
    }

    @Override
    public Type getType() {
        return Type.BLOCK;
    }

    @Override
    public TicketType<ChunkLoader<BlockPos>> getTicketType() {
        return TICKET_TYPE;
    }

    @Override
    public boolean submitTicket(ServerLevel world) throws IllegalStateException {
        if (isLoaded()) {
            return false;
//            throw new IllegalStateException("Was asked to submit tickets for " + this + " in world " + level + ", but it already did!");
        } else {
            world.getChunkSource().addRegionTicket(getTicketType(), getChunk(), 1, this);
            loaded = true;
            return true;
        }
    }

    @Override
    public boolean withdrawTicket(ServerLevel world) {
        if (!isLoaded()) {
            return false;
//            SimpleChunkManagerImpl.LOGGER.warning("Was asked to withdraw ticket for " + this + " in world " + level + ", but it shouldn't have one!");
        } else {
            world.getChunkSource().removeRegionTicket(getTicketType(), getChunk(), 1, this);
            loaded = false;
            return true;
        }
    }

    @Override
    public void writeSaveData(CompoundTag chunkLoaderData) {
        super.writeSaveData(chunkLoaderData);
        chunkLoaderData.put(BLOCK_POS_KEY, NbtUtils.writeBlockPos(getOwner()));
    }

    public static BlockChunkLoader readSaveData(String id, CompoundTag data) {
        return new BlockChunkLoaderImpl(id, NbtUtils.readBlockPos(data.getCompound(BLOCK_POS_KEY)));
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof BlockChunkLoader blockCl && getModId().equals(blockCl.getModId()) && getPos().equals(blockCl.getPos());
    }

    @Override
    public String toString() {
        return String.format("BlockChunkLoader[modId='%s', pos=%s, loaded=%s]", getModId(), getPos(), isLoaded());
    }
}