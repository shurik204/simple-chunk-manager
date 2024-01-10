package me.shurik.simplechunkmanager.mixin;

import me.shurik.simplechunkmanager.api.SimpleChunkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.ForcedChunksSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

/**
 * Store the server instance in FluxNetworks.
 */
@Mixin(value = MinecraftServer.class, priority = Integer.MIN_VALUE)
public class MinecraftServerMixin {
    @Inject(method = "prepareLevels", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/LongSet;iterator()Lit/unimi/dsi/fastutil/longs/LongIterator;", shift = At.Shift.BEFORE, remap = false), locals = LocalCapture.CAPTURE_FAILHARD)
    void loadChunkLoaderChunks(ChunkProgressListener progressListener, CallbackInfo info, ServerLevel overworld, BlockPos spawnPos, ServerChunkCache overworldChunkCache, Iterator<ServerLevel> levelsIterator, ServerLevel currentLevel, ForcedChunksSavedData currentLevelForcedChunksData) {
        SimpleChunkManager.VALIDATION.invoker().validate(currentLevel, SimpleChunkManager.of(currentLevel));
        SimpleChunkManager.of(currentLevel).submitAllTickets();
    }
}