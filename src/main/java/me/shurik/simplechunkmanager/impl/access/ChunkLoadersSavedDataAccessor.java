package me.shurik.simplechunkmanager.impl.access;

import me.shurik.simplechunkmanager.api.BlockChunkLoader;
import me.shurik.simplechunkmanager.impl.BlockChunkLoaderImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ChunkLoadersSavedDataAccessor {
    Set<BlockChunkLoader> getChunkLoaders();
    Set<BlockChunkLoader> getChunkLoaders(ResourceLocation id);
    Set<BlockChunkLoader> getChunkLoaders(String namespace);
    boolean addChunkLoader(BlockChunkLoader chunkLoader);
    @Nullable
    BlockChunkLoader removeChunkLoader(BlockChunkLoader chunkLoader);
    @Nullable
    default BlockChunkLoader removeChunkLoader(ResourceLocation id, BlockPos pos) {
        return removeChunkLoader(new BlockChunkLoaderImpl(id, pos));
    }
}