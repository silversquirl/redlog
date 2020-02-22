package vktec.redlog.mixins;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vktec.redlog.RedlogExtension;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void loadExtension(CallbackInfo ci) {
		RedlogExtension.noop();
	}
}
