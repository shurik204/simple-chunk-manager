package me.shurik.simplechunkmanager.mixin;

import me.shurik.simplechunkmanager.api.ChunkLoader;
import me.shurik.simplechunkmanager.impl.SimpleChunkManagerImpl;
import me.shurik.simplechunkmanager.impl.access.ChunkLoadersSavedDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ForcedChunksSavedData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ForcedChunksSavedData.class)
public class ForcedChunksSavedDataMixin implements ChunkLoadersSavedDataAccessor {
    @Unique
    private final Set<ChunkLoader<?>> BLOCK_CHUNK_LOADERS = new HashSet<>();

    @Inject(method = "save", at = @At("HEAD"))
    private void save(CompoundTag tag, CallbackInfoReturnable<CompoundTag> info) {
        SimpleChunkManagerImpl.saveChunkLoadersData(tag, BLOCK_CHUNK_LOADERS);
    }

    @Inject(method = "load", at = @At("RETURN"))
    private static void load(CompoundTag tag, CallbackInfoReturnable<ForcedChunksSavedData> info) {
        SimpleChunkManagerImpl.loadChunkLoadersData(tag, ((ChunkLoadersSavedDataAccessor) info.getReturnValue()).getChunkLoaders());
    }

    @Override
    public Set<ChunkLoader<?>> getChunkLoaders() {
        return BLOCK_CHUNK_LOADERS;
    }

    @Override
    public boolean addChunkLoader(ChunkLoader<?> chunkLoader) {
        return scm$markDirtyIfNeeded(BLOCK_CHUNK_LOADERS.add(chunkLoader));
    }

    @Override
    public boolean removeChunkLoader(ChunkLoader<?> chunkLoader) {
        return scm$markDirtyIfNeeded(BLOCK_CHUNK_LOADERS.remove(chunkLoader));
    }

    @Nullable
    @Override
    public ChunkLoader<?> getChunkLoader(String modId, Object owner) {
        return BLOCK_CHUNK_LOADERS.stream().filter(cl -> cl.getModId().equals(modId) && cl.getChunk().equals(owner)).findFirst().orElse(null);
    }

    @Override
    @Nullable
    public ChunkLoader<?> removeChunkLoader(String modId, Object owner) {
        ChunkLoader<?> removedChunkLoader = BLOCK_CHUNK_LOADERS.stream().filter(cl -> cl.getModId().equals(modId) && cl.getOwner().equals(owner)).findFirst().orElse(null);
        BLOCK_CHUNK_LOADERS.remove(removedChunkLoader);
        if (removedChunkLoader != null) ((ForcedChunksSavedData) (Object) this).setDirty(true);
        return removedChunkLoader;
    }

    @Unique
    private boolean scm$markDirtyIfNeeded(boolean shouldMark) {
        if (shouldMark) ((ForcedChunksSavedData) (Object) this).setDirty(true);
        return shouldMark;
    }
}