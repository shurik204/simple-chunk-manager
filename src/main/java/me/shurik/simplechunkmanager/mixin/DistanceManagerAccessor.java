package me.shurik.simplechunkmanager.mixin;

import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.Ticket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DistanceManager.class)
public interface DistanceManagerAccessor {
    @Invoker("addTicket")
    void invokeAddTicket(long l, Ticket<?> ticket);

    @Invoker("removeTicket")
    void invokeRemoveTicket(long l, Ticket<?> ticket);
}
