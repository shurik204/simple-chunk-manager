package me.shurik.simplechunkmanager.mixin;

import me.shurik.simplechunkmanager.api.BlockChunkLoader;
import me.shurik.simplechunkmanager.impl.SimpleChunkManagerImpl;
import me.shurik.simplechunkmanager.impl.access.ChunkLoadersSavedDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ForcedChunksSavedData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(ForcedChunksSavedData.class)
public class ForcedChunksSavedDataMixin implements ChunkLoadersSavedDataAccessor {
    private final Set<BlockChunkLoader> BLOCK_CHUNK_LOADERS = new HashSet<>();

    @Inject(method = "save", at = @At("HEAD"))
    private void save(CompoundTag tag, CallbackInfoReturnable<CompoundTag> info) {
        SimpleChunkManagerImpl.saveChunkLoadersData(tag, BLOCK_CHUNK_LOADERS);
    }

    @Inject(method = "load", at = @At("RETURN"))
    private static void load(CompoundTag tag, CallbackInfoReturnable<ForcedChunksSavedData> info) {
        SimpleChunkManagerImpl.loadChunkLoadersData(tag, ((ChunkLoadersSavedDataAccessor) info.getReturnValue()).getChunkLoaders());
    }

    @Override
    public Set<BlockChunkLoader> getChunkLoaders() {
        return BLOCK_CHUNK_LOADERS;
    }

    @Override
    public Set<BlockChunkLoader> getChunkLoaders(ResourceLocation id) {
        return BLOCK_CHUNK_LOADERS.stream().filter(chunkLoader -> chunkLoader.getId().equals(id)).collect(Collectors.toSet());
    }

    @Override
    public Set<BlockChunkLoader> getChunkLoaders(String namespace) {
        return BLOCK_CHUNK_LOADERS.stream().filter(chunkLoader -> chunkLoader.getId().getNamespace().equals(namespace)).collect(Collectors.toSet());
    }

    @Override
    public boolean addChunkLoader(BlockChunkLoader chunkLoader) {
        boolean result = BLOCK_CHUNK_LOADERS.add(chunkLoader);
        if (result) ((ForcedChunksSavedData) (Object) this).setDirty(true);
        return result;
    }

    @Override
    @Nullable
    public BlockChunkLoader removeChunkLoader(BlockChunkLoader chunkLoader) {
        BlockChunkLoader removedChunkLoader = BLOCK_CHUNK_LOADERS.stream().filter(cl -> cl.equals(chunkLoader)).findFirst().orElse(null);
        BLOCK_CHUNK_LOADERS.remove(removedChunkLoader);
        if (removedChunkLoader != null) ((ForcedChunksSavedData) (Object) this).setDirty(true);
        return removedChunkLoader;
    }
}