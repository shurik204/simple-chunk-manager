package me.shurik.simplechunkmanager.mixin;

import me.shurik.simplechunkmanager.api.SimpleChunkManager;
import me.shurik.simplechunkmanager.impl.SimpleChunkManagerImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.ForcedChunksSavedData;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

/**
 * Store the server instance in FluxNetworks.
 */
@Debug(export = true)
@Mixin(value = MinecraftServer.class, priority = Integer.MIN_VALUE)
public class MinecraftServerMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) { SimpleChunkManagerImpl.setServer((MinecraftServer) (Object) this); }
    @Inject(method = "stopServer", at = @At("TAIL"))
    private void shutdown(CallbackInfo ci) { SimpleChunkManagerImpl.setServer(null); }

    @Inject(method = "prepareLevels", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/LongSet;iterator()Lit/unimi/dsi/fastutil/longs/LongIterator;", shift = At.Shift.BEFORE, remap = false), locals = LocalCapture.CAPTURE_FAILHARD)
    void loadChunkLoaderChunks(ChunkProgressListener progressListener, CallbackInfo info, ServerLevel overworld, BlockPos spawnPos, ServerChunkCache overworldChunkCache, Iterator<ServerLevel> levelsIterator, ServerLevel currentLevel, ForcedChunksSavedData currentLevelForcedChunksData) {
        SimpleChunkManager.VALIDATION.invoker().validate(currentLevel, SimpleChunkManager.getInstance());
        SimpleChunkManager.getInstance().submitTickets(currentLevel, SimpleChunkManager.getInstance().getAllChunkLoaders(currentLevel));

    }
}