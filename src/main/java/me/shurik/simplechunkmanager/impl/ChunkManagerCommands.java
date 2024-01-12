package me.shurik.simplechunkmanager.impl;

import me.shurik.glimmer.command_builder.api.GlimmerCommand;
import me.shurik.simplechunkmanager.api.BlockChunkLoader;
import me.shurik.simplechunkmanager.api.ChunkLoader;
import me.shurik.simplechunkmanager.api.SimpleChunkManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class ChunkManagerCommands {
    public static final String MOD_CHUNK_ID = "chunk-manager-command";

    public static void registerCommand() {
        GlimmerCommand.Builder commandBuilder = GlimmerCommand.create("chunk-manager");

        commandBuilder.branch().literal("load", (source) -> {
            ServerPlayer player = source.getSource().getPlayerOrException();
            boolean res = SimpleChunkManager.of(source.getSource().getLevel()).addChunkLoaderBlock(MOD_CHUNK_ID, source.getSource().getPlayerOrException().getOnPos());
            if (res)
                player.sendSystemMessage(Component.nullToEmpty("Chunk loader " + player.getOnPos().toShortString() +  " loaded"), false);
            else
                player.sendSystemMessage(Component.nullToEmpty("Failed to create chunk loader at pos" + player.getOnPos().toShortString()), false);
            return 1;
        });

        commandBuilder.branch().literal("unload", (source) -> {
            ServerPlayer player = source.getSource().getPlayerOrException();
            boolean res = SimpleChunkManager.of(source.getSource().getLevel()).removeChunkLoader(MOD_CHUNK_ID, source.getSource().getPlayerOrException().getOnPos());
            if (res)
                player.sendSystemMessage(Component.nullToEmpty("Chunk loader " + player.getOnPos().toShortString() +  " unloaded"), false);
            else
                player.sendSystemMessage(Component.nullToEmpty("Failed to unload chunk loader at pos [" + player.getOnPos().toShortString() + "]"), false);
            return 1;
        });

        commandBuilder.branch().literal("list", (source) -> {
            ServerPlayer player = source.getSource().getPlayerOrException();
            Collection<ChunkLoader<?>> chunkLoaders = SimpleChunkManager.of(source.getSource().getLevel()).getModChunkLoaders(MOD_CHUNK_ID);
            player.sendSystemMessage(Component.nullToEmpty("Chunk loaders (" + chunkLoaders.size() + "):"), false);
            chunkLoaders.forEach((chunkLoader) -> {
                if (chunkLoader instanceof BlockChunkLoader blockCl) {
                    player.sendSystemMessage(Component.nullToEmpty("Chunk loader: " + blockCl.getPos().toShortString()), false);
                }
            });
            return 1;
        });

        GlimmerCommand.register(commandBuilder);
    }
}