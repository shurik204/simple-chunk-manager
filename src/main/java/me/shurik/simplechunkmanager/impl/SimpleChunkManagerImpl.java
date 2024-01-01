package me.shurik.simplechunkmanager.impl;

import me.shurik.simplechunkmanager.api.BlockChunkLoader;
import me.shurik.simplechunkmanager.api.SimpleChunkManager;
import me.shurik.simplechunkmanager.impl.access.ChunkLoadersSavedDataAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ForcedChunksSavedData;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

@ApiStatus.Internal
public class SimpleChunkManagerImpl implements SimpleChunkManager {
    private final MinecraftServer server;
    public SimpleChunkManagerImpl(MinecraftServer server) {
        this.server = server;
    }

    /**
     * Add chunk loaders' tickets for {@code level}
     * @param level Server level
     * @param chunkLoaders Chunk loaders
     */
    public void submitTickets(ServerLevel level, Set<BlockChunkLoader> chunkLoaders) {
        chunkLoaders.forEach(chunkLoader -> chunkLoader.submitTickets(level));
    }

    /**
     * Withdraw chunk loaders' tickets from {@code level}
     * @param level Server level
     * @param chunkLoaders Chunk loaders
     */
    public void withdrawTickets(ServerLevel level, Set<BlockChunkLoader> chunkLoaders) {
        chunkLoaders.forEach(chunkLoader -> chunkLoader.withdrawTickets(level));
    }

    /**
     * Get chunk loaders with specified {@code namespace} in {@code level}
     * @param namespace Chunk loader namespace
     * @param level Server level
     * @return Chunk loaders
     */
    public Set<BlockChunkLoader> getChunkLoaders(String namespace, ServerLevel level) {
        return getChunkLoadersAccessor(level).getChunkLoaders(namespace);
    }

    /**
     * Get chunk loaders with specified {@code id} in {@code level}
     * @param id Chunk loader id
     * @param level Server level
     * @return Chunk loaders
     */
    public Set<BlockChunkLoader> getChunkLoaders(ResourceLocation id, ServerLevel level) {
        return getChunkLoadersAccessor(level).getChunkLoaders(id);
    }

    /**
     * Get all chunk loaders in {@code level}
     * @param level Server level
     * @return Chunk loaders
     */
    public Set<BlockChunkLoader> getAllChunkLoaders(ServerLevel level) {
        return getChunkLoadersAccessor(level).getChunkLoaders();
    }

    /**
     * Add chunk loader to {@code level}
     * @param id Chunk loader id
     * @param level Server level
     * @param pos Chunk loader position
     * @return {@code true} if chunk loader was added, {@code false} otherwise
     */
    public boolean addChunkLoader(ResourceLocation id, ServerLevel level, BlockPos pos) {
        BlockChunkLoader chunkLoader = new BlockChunkLoaderImpl(id, pos);
        boolean result = getChunkLoadersAccessor(level).addChunkLoader(chunkLoader);
        if (result) chunkLoader.submitTickets(level);
        return result;
    }

    /**
     * Remove chunk loader from {@code level}
     * @param id Chunk loader id
     * @param level Server level
     * @param pos Chunk loader position
     * @return {@code true} if chunk loader was removed, {@code false} otherwise
     */
    public boolean removeChunkLoader(ResourceLocation id, ServerLevel level, BlockPos pos) {
        BlockChunkLoader chunkLoader = getChunkLoadersAccessor(level).removeChunkLoader(id, pos);
        if (chunkLoader == null) return false;
        chunkLoader.withdrawTickets(level);
        return true;
    }


    ////// STATIC //////
    public static SimpleChunkManagerImpl INSTANCE = null;
    public static void setServer(MinecraftServer server) {
        INSTANCE = server == null ? null : new SimpleChunkManagerImpl(server);
    }

    /**
     * Load chunk loaders from NBT {@code tag} and add them to {@code chunkLoaders}
     * @param tag Chunk loaders NBT data
     * @param chunkLoaders Set of chunk loaders to populate
     */
    public static void loadChunkLoadersData(CompoundTag tag, Set<BlockChunkLoader> chunkLoaders) {
        // Get List[ChunkLoader]
        ListTag chunkLoadersTag = tag.getList("chunkLoaders", Tag.TAG_COMPOUND);
        chunkLoadersTag.forEach(chunkLoaderTag -> {
            CompoundTag chunkLoaderData = (CompoundTag) chunkLoaderTag;
            BlockChunkLoader chunkLoader = BlockChunkLoader.loadSaveData(chunkLoaderData);
            chunkLoaders.add(chunkLoader);
        });
    }

    /**
     * Save chunk loaders from {@code chunkLoaders} to NBT {@code tag}
     * @param tag Chunk loaders NBT data
     * @param chunkLoaders Set of chunk loaders to save
     */
    public static void saveChunkLoadersData(CompoundTag tag, Set<BlockChunkLoader> chunkLoaders) {
        // Create List[ChunkLoader]
        ListTag chunkLoadersTag = new ListTag();
        chunkLoaders.forEach(chunkLoader -> {
            CompoundTag chunkLoaderData = new CompoundTag();
            chunkLoader.writeSaveData(chunkLoaderData);
            chunkLoadersTag.add(chunkLoaderData);
        });
        tag.put("chunkLoaders", chunkLoadersTag);
    }

    /**
     * Get chunk loaders saved data accessor for {@code level}
     * @param level Server level
     * @return Chunk loaders saved data accessor
     */
    private static ChunkLoadersSavedDataAccessor getChunkLoadersAccessor(ServerLevel level) {
        return (ChunkLoadersSavedDataAccessor) level.getDataStorage().computeIfAbsent(ForcedChunksSavedData::load, ForcedChunksSavedData::new, "chunks");
    }
}