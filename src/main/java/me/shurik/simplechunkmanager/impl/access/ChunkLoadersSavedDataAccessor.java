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
    @Nullable
    <T extends Comparable<? super T>> ChunkLoader<?> removeChunkLoader(String modId, T pos);
}