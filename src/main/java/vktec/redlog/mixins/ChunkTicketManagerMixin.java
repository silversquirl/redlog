package vktec.redlog.mixins;

import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.collection.SortedArraySet;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vktec.redlog.RedEventLogger;
import vktec.redlog.events.RedChunkTicketEvent;

@Mixin(ChunkTicketManager.class)
public abstract class ChunkTicketManagerMixin {
	@Shadow private native SortedArraySet<ChunkTicket> getTicketSet(long pos);
	@Shadow private static native int getLevel(SortedArraySet<ChunkTicket> set);

	@Inject(method = "addTicket", at = @At("HEAD"))
	private void beginChunkTicket(long pos, ChunkTicket ticket, CallbackInfo ci) {
		ThreadedAnvilChunkStorage tacs = ((TACSTicketManagerAccessor)this).getTACS();
		long time = ((ThreadedAnvilChunkStorageAccessor)tacs).getWorld().getTime();
		int currentLevel = getLevel(this.getTicketSet(pos));
		RedEventLogger.begin(new RedChunkTicketEvent(time, new ChunkPos(pos), ticket, currentLevel));
	}

	@Inject(method = "addTicket", at = @At("TAIL"))
	private void endChunkTicket(CallbackInfo ci) {
		RedEventLogger.end();
	}
}
