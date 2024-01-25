package me.shurik.simplechunkmanager.impl;

import me.shurik.simplechunkmanager.api.ChunkLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

@ApiStatus.Internal
public abstract class ChunkLoaderImpl<T extends Comparable<? super T>> implements ChunkLoader<T> {
    protected static final String ID_KEY = "modId";
    protected static final String TYPE_KEY = "type";

    private final String modId;
    private final T owner;
    private final ChunkPos chunk;
    protected boolean loaded;

    public ChunkLoaderImpl(String modId, T owner, ChunkPos chunk) {
        this.modId = modId;
        this.owner = owner;
        this.chunk = chunk;
    }

    // API methods

    @Override
    public String getModId() {
        return modId;
    }

    @Override
    public T getOwner() {
        return owner;
    }

    @Override
    public ChunkPos getChunk() {
        return chunk;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void writeSaveData(CompoundTag chunkLoaderData) {
        chunkLoaderData.putString(ID_KEY, modId);
        chunkLoaderData.putString(TYPE_KEY, getType().name());
    }

    // Static methods

    public static ChunkLoader<?> readSaveData(CompoundTag data) {
        String id = data.getString(ID_KEY);
        ChunkLoader.Type type = ChunkLoader.Type.valueOf(data.getString(TYPE_KEY));
        if (type == Type.BLOCK) {
            return BlockChunkLoaderImpl.readSaveData(id, data);
        }
        throw new IllegalArgumentException("Unknown chunk loader type: " + type);
    }

    // Object overrides

    @Override
    public int hashCode() {
        return Objects.hash(modId, owner);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj ||
                obj instanceof ChunkLoader<?> other &&
                getModId().equals(other.getModId()) &&
                getOwner().equals(other.getOwner());
    }

    @Override
    public String toString() {
        return "ChunkLoader[modId='" + modId + "', owner='" + owner + "', chunk=" + chunk + "]";
    }
}