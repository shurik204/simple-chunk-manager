package me.shurik.simplechunkmanager.api;

import me.shurik.simplechunkmanager.impl.access.LevelChunkManagerAccessor;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Simple chunk loaders manager.
 * Added chunk loaders are saved and loaded back automatically.
 */
public interface SimpleChunkManager {
    static SimpleChunkManager of(ServerLevel world) {
        return ((LevelChunkManagerAccessor) world).getChunkManagerInstance();
    }

    /**
     * Add chunk loader to {@code world}
     * @param modId mod ID
     * @param pos block pos
     * @return whether chunk loader was added or not
     */
    boolean addChunkLoaderBlock(String modId, BlockPos pos);

    /**
     * Get chunk loader for given {@code modId} and {@code owner}
     * @param modId mod ID
     * @param owner chunk loader owner
     * @return chunk loader or null if not found
     */
    @Nullable
    ChunkLoader<?> getChunkLoader(String modId, Object owner);

    /**
     * Remove chunk loader from {@code world}
     * @param modId mod ID
     * @param owner chunk loader owner
     * @return whether chunk loader was removed or not
     */
    boolean removeChunkLoader(String modId, Object owner);

    /**
     * Remove all chunk loaders for given {@code modId}
     */
    void removeModChunkLoaders(String modId);

    /**
     * Remove mod's chunk loader blocks from {@code world}
     * @return whether any chunk loader was removed or not
     */
    boolean removeModChunkLoaderBlocks(String modId);

    /**
     * Remove existing chunk loader from {@code world}
     * @param chunkLoader chunk loader to remove
     * @return whether chunk loader was removed or not
     */
    boolean removeChunkLoader(ChunkLoader<?> chunkLoader);

    /**
     * Try to submit ticket for given chunk loader
     * @param chunkLoader chunk loader
     * @return whether ticket was submitted or not
     */
    boolean submitTicket(ChunkLoader<?> chunkLoader);

    /**
     * @return all chunk loaders
     */
    Set<ChunkLoader<?>> getChunkLoaders();

    /**
     * @param modId mod ID
     * @return all chunk loaders for given {@code modId}
     */
    Set<ChunkLoader<?>> getModChunkLoaders(String modId);

    /**
     * Load all chunk loaders.
     * This method is called when a world is loaded.
     */
    void submitAllTickets();

    /**
     * @return server world associated with this chunk manager
     */
    ServerLevel getLevel();

    /**
     * Fired when chunk loaders' tickets are about to be added to {@code world}
     */
    Event<ValidationCallback> VALIDATION = EventFactory.createArrayBacked(ValidationCallback.class, callbacks -> (world, chunkLoaders) -> {
        for (ValidationCallback callback : callbacks) {
            callback.validate(world, SimpleChunkManager.of(world));
        }
    });

    @FunctionalInterface
    interface ValidationCallback {
        /**
         * Fired when chunk loaders' tickets are about to be added to {@code world}
         * @param world Server world
         * @param chunkManager SimpleChunkManager instance
         */
        void validate(ServerLevel world, SimpleChunkManager chunkManager);
    }
}