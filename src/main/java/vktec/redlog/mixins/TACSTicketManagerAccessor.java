package vktec.redlog.mixins;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets="net/minecraft/server/world/ThreadedAnvilChunkStorage$TicketManager")
public interface TACSTicketManagerAccessor {
	@Accessor(value="field_17443")
	ThreadedAnvilChunkStorage getTACS();
}
