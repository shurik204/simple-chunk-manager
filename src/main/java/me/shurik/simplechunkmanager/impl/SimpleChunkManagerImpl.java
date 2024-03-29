package me.shurik.simplechunkmanager.impl;

import me.shurik.simplechunkmanager.api.BlockChunkLoader;
import me.shurik.simplechunkmanager.api.ChunkLoader;
import me.shurik.simplechunkmanager.api.SimpleChunkManager;
import me.shurik.simplechunkmanager.impl.access.ChunkLoadersSavedDataAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ForcedChunksSavedData;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import java.util.logging.Logger;

@ApiStatus.Internal
public class SimpleChunkManagerImpl implements SimpleChunkManager {
    protected static final Logger LOGGER = Logger.getLogger("SimpleChunkManager");

    private final ServerLevel level;
    private final ChunkLoadersSavedDataAccessor chunkLoadersData;

    public SimpleChunkManagerImpl(ServerLevel level) {
        this.level = level;
        this.chunkLoadersData = getChunkLoadersDataAccessor(level);
    }

    // API methods

    public boolean addChunkLoaderBlock(String modId, BlockPos pos) {
        BlockChunkLoader chunkLoader = new BlockChunkLoaderImpl(modId, pos);
        if (chunkLoadersData.addChunkLoader(chunkLoader)) {
            // if it was added, load it
            chunkLoader.submitTicket(level);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeChunkLoader(String modId, Object owner) {
        ChunkLoader<?> chunkLoader = chunkLoadersData.removeChunkLoader(modId, owner);
        if (chunkLoader != null) {
            chunkLoader.withdrawTicket(level);
            return true;
        }
        return false;
    }

    @Override
    public void removeModChunkLoaders(String modId) {
        // PLACEHOLDER
        removeModChunkLoaderBlocks(modId);
    }

    @Override
    public boolean removeModChunkLoaderBlocks(String modId) {
        return chunkLoadersData.getChunkLoaders().removeIf(chunkLoader -> {
            if (chunkLoader.getModId().equals(modId)) {
                chunkLoader.withdrawTicket(level);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean removeChunkLoader(ChunkLoader<?> chunkLoader) {
        if (chunkLoadersData.removeChunkLoader(chunkLoader)) {
            chunkLoader.withdrawTicket(level);
            return true;
        }
        return false;
    }

    @Override
    public ChunkLoader<?> getChunkLoader(String modId, Object owner) {
        return chunkLoadersData.getChunkLoader(modId, owner);
    }

    public boolean submitTicket(ChunkLoader<?> chunkLoader) {
        return chunkLoader.submitTicket(level);
    }

    @Override
    public Set<ChunkLoader<?>> getChunkLoaders() {
        return chunkLoadersData.getChunkLoaders();
    }

    @Override
    public Set<ChunkLoader<?>> getModChunkLoaders(String modId) {
        return chunkLoadersData.getModChunkLoaders(modId);
    }

    @Override
    public void submitAllTickets() {
        chunkLoadersData.getChunkLoaders().forEach(this::submitTicket);
    }

    @Override
    public ServerLevel getLevel() {
        return level;
    }

    ////// STATIC //////
    /**
     * Load chunk loaders from NBT {@code tag} and add them to {@code chunkLoaders}
     * @param tag Chunk loaders NBT data
     * @param chunkLoaders Set of chunk loaders to populate
     */
    public static void loadChunkLoadersData(CompoundTag tag, Set<ChunkLoader<?>> chunkLoaders) {
        // Get List[ChunkLoader]
        ListTag chunkLoadersTag = tag.getList("chunkLoaders", Tag.TAG_COMPOUND);
        chunkLoadersTag.forEach(chunkLoaderTag -> {
            CompoundTag chunkLoaderData = (CompoundTag) chunkLoaderTag;
            ChunkLoader<?> chunkLoader = ChunkLoader.readSaveData(chunkLoaderData);
            chunkLoaders.add(chunkLoader);
        });
    }

    /**
     * Save chunk loaders from {@code chunkLoaders} to NBT {@code tag}
     * @param tag Chunk loaders NBT data
     * @param chunkLoaders Set of chunk loaders to save
     */
    public static void saveChunkLoadersData(CompoundTag tag, Set<ChunkLoader<?>> chunkLoaders) {
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
    private static ChunkLoadersSavedDataAccessor getChunkLoadersDataAccessor(ServerLevel level) {
        return (ChunkLoadersSavedDataAccessor) level.getDataStorage().computeIfAbsent(ForcedChunksSavedData.factory(), "chunks");
    }
}