package me.shurik.simplechunkmanager.mixin;

import me.shurik.simplechunkmanager.api.SimpleChunkManager;
import me.shurik.simplechunkmanager.impl.access.LevelChunkManagerAccessor;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements LevelChunkManagerAccessor {
    private final SimpleChunkManager smc$INSTANCE = SimpleChunkManager.of((ServerLevel) (Object) this);

    @Override
    public SimpleChunkManager scm$getChunkManager() {
        return smc$INSTANCE;
    }
}
