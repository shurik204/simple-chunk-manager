package me.shurik.simplechunkmanager.api;

import me.shurik.simplechunkmanager.impl.access.LevelChunkManagerAccessor;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.Set;

/**
 * Simple chunk loaders manager.
 * Added chunk loaders are saved and loaded back automatically.
 */
public interface SimpleChunkManager {
    static SimpleChunkManager of(ServerLevel world) {
        return ((LevelChunkManagerAccessor) world).scm$getChunkManager();
    }

    /**
     * Add chunk loader to {@code world}
     * @param modId mod ID
     * @param pos chunk loader position
     * @return {@code true} if chunk loader was added, {@code false} if chunk loader already exists
     */
    boolean addChunkLoaderBlock(String modId, BlockPos pos);

    /**
     * Remove chunk loader from {@code world}
     * @param modId mod ID
     * @param pos chunk loader position
     * @return {@code true} if chunk loader was removed, {@code false} otherwise
     */
    boolean removeChunkLoaderBlock(String modId, BlockPos pos);

    /**
     * Try to submit ticket for given chunk loader
     * @param chunkLoader chunk loader
     * @return whether ticket was submitted or not
     */
    boolean trySubmitTicket(ChunkLoader<?> chunkLoader);

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