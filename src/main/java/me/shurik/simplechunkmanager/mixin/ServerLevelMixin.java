package me.shurik.simplechunkmanager.mixin;

import me.shurik.simplechunkmanager.api.SimpleChunkManager;
import me.shurik.simplechunkmanager.impl.SimpleChunkManagerImpl;
import me.shurik.simplechunkmanager.impl.access.LevelChunkManagerAccessor;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements LevelChunkManagerAccessor {
    private SimpleChunkManager smc$INSTANCE;

    @Unique
    @Override
    public SimpleChunkManager getChunkManagerInstance() {
        if (smc$INSTANCE == null) smc$INSTANCE = new SimpleChunkManagerImpl((ServerLevel) (Object) this);
        return smc$INSTANCE;
    }
}