package me.shurik.simplechunkmanager.impl.access;

import me.shurik.simplechunkmanager.api.ChunkLoader;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

public interface ChunkLoadersSavedDataAccessor {
    Set<ChunkLoader<?>> getChunkLoaders();
    default Set<ChunkLoader<?>> getModChunkLoaders(String modId) {
        return getChunkLoaders().stream().filter(chunkLoader -> chunkLoader.getModId().equals(modId)).collect(Collectors.toSet());
    }
    boolean addChunkLoader(ChunkLoader<?> chunkLoader);

    @Nullable ChunkLoader<?> getChunkLoader(String modId, Object owner);

    boolean removeChunkLoader(ChunkLoader<?> chunkLoader);
    @Nullable ChunkLoader<?> removeChunkLoader(String modId, Object owner);
}