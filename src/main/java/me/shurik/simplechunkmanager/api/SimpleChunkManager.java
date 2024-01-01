package me.shurik.simplechunkmanager.api;

import me.shurik.simplechunkmanager.impl.SimpleChunkManagerImpl;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.Set;

public interface SimpleChunkManager {
    static SimpleChunkManager getInstance() {
        SimpleChunkManager instance = SimpleChunkManagerImpl.INSTANCE;

        if (instance == null) {
            throw new IllegalStateException("SimpleChunkManager is not initialized");
        }

        return instance;
    }

    /**
     * Fired when chunk loaders' tickets are about to be added to {@code level}
     */
    Event<ValidationCallback> VALIDATION = EventFactory.createArrayBacked(ValidationCallback.class, callbacks -> (level, chunkLoaders) -> {
        for (ValidationCallback callback : callbacks) {
            callback.validate(level, SimpleChunkManager.getInstance());
        }
    });

    void submitTickets(ServerLevel level, Set<BlockChunkLoader> chunkLoaders);
    void withdrawTickets(ServerLevel level, Set<BlockChunkLoader> chunkLoaders);
    Set<BlockChunkLoader> getChunkLoaders(String namespace, ServerLevel level);
    Set<BlockChunkLoader> getChunkLoaders(ResourceLocation id, ServerLevel level);
    Set<BlockChunkLoader> getAllChunkLoaders(ServerLevel level);
    boolean addChunkLoader(ResourceLocation id, ServerLevel level, BlockPos pos);
    boolean removeChunkLoader(ResourceLocation id, ServerLevel level, BlockPos pos);

    @FunctionalInterface
    interface ValidationCallback {
        /**
         * Fired when chunk loaders' tickets are about to be added to {@code level}
         * @param level Server level
         * @param chunkManager SimpleChunkManager instance
         */
        void validate(ServerLevel level, SimpleChunkManager chunkManager);
    }
}