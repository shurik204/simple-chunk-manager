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
     * @return chunk loader's ticket type
     */
    TicketType<ChunkLoader<T>> getTicketType();

    /**
     * @return true if chunk loader is loaded (ticket for it was submitted).
     */
    boolean isLoaded();

    /**
     * Submits chunk loader's ticket to {@code world}
     * @param world world to submit ticket to
     * @return whether ticket was submitted or not
     */
    boolean submitTicket(ServerLevel world);

    /**
     * Withdraws chunk loader's ticket from {@code world}
     * @param world world to withdraw ticket from
     * @return whether ticket was withdrawn or not
     */
    boolean withdrawTicket(ServerLevel world);

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

    enum Type {
        BLOCK
    }
}