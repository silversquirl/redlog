package vktec.redlog.mixins;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vktec.redlog.RedEventLogger;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
	@Inject(method = "tick", at = @At("RETURN"))
	private void clearIndentAfterTick(CallbackInfo ci) {
		RedEventLogger.clearIndent();
	}
}
