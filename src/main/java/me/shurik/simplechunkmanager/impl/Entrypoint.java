package me.shurik.simplechunkmanager.impl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Entrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment() && FabricLoader.getInstance().isModLoaded("glimmer-command-builder"))
            ChunkManagerCommands.registerCommand();
    }
}