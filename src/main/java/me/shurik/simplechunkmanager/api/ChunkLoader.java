package me.shurik.simplechunkmanager.api;

import me.shurik.simplechunkmanager.impl.ChunkLoaderImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

public interface ChunkLoader<T extends Comparable<? super T>> extends Comparable<ChunkLoader<T>> {

    /**
     * Returns mod ID of chunk loader
     * @return mod ID
     */
    String getModId();

    /**
     * Returns owner of chunk loader.
     * If chunk loader type is {@link Type#BLOCK}, returns {@link BlockPos} of owner
     * @return chunk loader owner
     */
    T getOwner();

    /**
     * Returns loaded chunk position
     * @return chunk position
     */
    ChunkPos getChunk();

    /**
     * Returns chunk loader type. See {@link Type}.
     * @return chunk loader type
     */
    ChunkLoader.Type getType();

    /**
     * Creates and returns ticket type for chunk loader
     * @return ticket type
     */
    TicketType<ChunkLoader<T>> createTicketType();

    /**
     * @return true if chunk loader is loaded (ticket for it was submitted).
     */
    boolean isLoaded();

    void submitTicket(ServerLevel level) throws IllegalStateException;

    /**
     * Writes chunk loader data to {@link CompoundTag}
     * @param tag tag to write data to
     */
    void writeSaveData(CompoundTag tag);

    /**
     * Returns true if chunk loader matches mod ID and owner
     * @param modId mod ID
     * @param owner owner
     * @return matches or not
     */
    default boolean matches(String modId, Object owner) {
        return getModId().equals(modId) && getOwner().equals(owner);
    }

    /**
     * Reads chunk loader data from {@link CompoundTag}
     * @param tag tag to read data from
     * @return chunk loader
     */
    static ChunkLoader<?> readSaveData(CompoundTag tag) {
        return ChunkLoaderImpl.readSaveData(tag);
    }

    @Override
    default int compareTo(ChunkLoader<T> other) {
        int res = getModId().compareTo(other.getModId());
        return res == 0 ? getOwner().compareTo(other.getOwner()) : res;
    }

    boolean withdrawTicket(ServerLevel level);

    enum Type {
        BLOCK
    }
}