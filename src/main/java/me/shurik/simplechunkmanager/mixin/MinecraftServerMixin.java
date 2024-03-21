package me.shurik.simplechunkmanager.mixin;

import me.shurik.simplechunkmanager.api.SimpleChunkManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = MinecraftServer.class, priority = Integer.MIN_VALUE)
public class MinecraftServerMixin {
    @Shadow @Final private Map<ResourceKey<Level>, ServerLevel> levels;

    @Inject(method = "prepareLevels", at = @At(value = "RETURN"))
    void loadChunkLoaderChunks(ChunkProgressListener progressListener, CallbackInfo info) {
        for (ServerLevel serverLevel : this.levels.values()) {
            SimpleChunkManager.VALIDATION.invoker().validate(serverLevel, SimpleChunkManager.of(serverLevel));
            SimpleChunkManager.of(serverLevel).submitAllTickets();
        }
    }
}