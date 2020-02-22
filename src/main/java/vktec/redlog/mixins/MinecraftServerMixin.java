package vktec.redlog.mixins;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vktec.redlog.RedlogExtension;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void loadExtension(CallbackInfo ci) {
		RedlogExtension.noop();
	}
}
