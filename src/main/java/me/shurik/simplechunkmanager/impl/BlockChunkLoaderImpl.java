package me.shurik.simplechunkmanager.impl;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import me.shurik.simplechunkmanager.api.BlockChunkLoader;
import me.shurik.simplechunkmanager.mixin.DistanceManagerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.Comparator;
import java.util.List;

@ApiStatus.Internal
public class BlockChunkLoaderImpl implements BlockChunkLoader {
    private static final String ID_KEY = "id";
    private static final String BLOCK_POS_KEY = "block";
    private static final String FORCED_CHUNKS_KEY = "forcedChunks";

    private final ResourceLocation id;
    private final BlockPos pos;
    private final LongSet forcedChunks = new LongOpenHashSet();


    public BlockChunkLoaderImpl(ResourceLocation id, BlockPos pos) {
        this(id, pos, List.of(new ChunkPos(pos)));
    }

    private BlockChunkLoaderImpl(ResourceLocation id, BlockPos pos, LongSet forcedChunks) {
        this.id = id;
        this.pos = pos;
        this.forcedChunks.addAll(forcedChunks);
    }

    public BlockChunkLoaderImpl(ResourceLocation id, BlockPos pos, List<ChunkPos> forcedChunks) {
        this.id = id;
        this.pos = pos;
        forcedChunks.forEach(chunkPos -> this.forcedChunks.add(chunkPos.toLong()));
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public LongSet getForcedChunks() {
        return forcedChunks;
    }

    @Override
    public TicketType<ChunkPos> createTicketType() {
        return TicketType.create(getStringId(), Comparator.comparingLong(ChunkPos::toLong));
    }

    @Override
    public void submitTickets(ServerLevel level) {
        DistanceManagerAccessor distanceManager = (DistanceManagerAccessor) level.getChunkSource().chunkMap.getDistanceManager();
        TicketType<ChunkPos> ticketType = createTicketType();

        forcedChunks.longStream().forEach(chunkPosL ->
                distanceManager.invokeAddTicket(chunkPosL, new Ticket<>(ticketType, ChunkMap.FORCED_TICKET_LEVEL, new ChunkPos(chunkPosL))));
    }

    @Override
    public void withdrawTickets(ServerLevel level) {
        DistanceManagerAccessor distanceManager = (DistanceManagerAccessor) level.getChunkSource().chunkMap.getDistanceManager();
        TicketType<ChunkPos> ticketType = createTicketType();

        forcedChunks.longStream().forEach(chunkPosL ->
                distanceManager.invokeRemoveTicket(chunkPosL, new Ticket<>(ticketType, ChunkMap.FORCED_TICKET_LEVEL, new ChunkPos(chunkPosL))));
    }
//    TODO: allow adding and removing chunks
//    @Override
//    public boolean addChunk(long chunkPos) {
//        return forcedChunks.add(chunkPos);
//    }
//
//    @Override
//    public boolean removeChunk(long chunkPos) {
//        return forcedChunks.remove(chunkPos);
//    }

    @Override
    public void writeSaveData(CompoundTag tag) {
        tag.putString(ID_KEY, getStringId());
        tag.put(BLOCK_POS_KEY, NbtUtils.writeBlockPos(pos));
        tag.putLongArray(FORCED_CHUNKS_KEY, forcedChunks.toLongArray());
    }

    public static BlockChunkLoaderImpl loadSaveData(CompoundTag tag) {
        ResourceLocation id = new ResourceLocation(tag.getString(ID_KEY));
        BlockPos pos = NbtUtils.readBlockPos(tag.getCompound(BLOCK_POS_KEY));
        LongSet forcedChunks = new LongOpenHashSet(tag.getLongArray(FORCED_CHUNKS_KEY));
        return new BlockChunkLoaderImpl(id, pos, forcedChunks);
    }

    @Override
    public int hashCode() {
        return id.hashCode() * pos.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BlockChunkLoaderImpl other && id.equals(other.id) && pos.equals(other.pos);
    }
}