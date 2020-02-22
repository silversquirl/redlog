package vktec.redlog.mixins;

import carpet.logging.Logger;
import carpet.logging.LoggerRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoggerRegistry.class)
public abstract class LoggerRegistryMixin {
	@Shadow(remap = false) private native static void registerLogger(String name, Logger logger);

	@Inject(method = "initLoggers", at = @At("HEAD"), remap = false)
	private static void initLoggers(CallbackInfo ci) {
		registerLogger("block36", new Logger("block36", null, null));
		registerLogger("blockEvents", new Logger("blockEvents", null, null));
		registerLogger("blockUpdates", new Logger("blockUpdates", null, null));
		registerLogger("stateUpdates", new Logger("stateUpdates", null, null));
		registerLogger("tileTicks", new Logger("tileTicks", null, null));
	}
}
