package vktec.redlog.mixins;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ScheduledTick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vktec.redlog.RedEventLogger;
import vktec.redlog.events.RedTickEvent;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
	@Redirect(
		method = "tickBlock",
		at = @At(
			value = "INVOKE",
			target = "net.minecraft.block.BlockState.scheduledTick(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"
		)
	)
	private void logTileTick(BlockState block, ServerWorld world, BlockPos pos, Random rand, ScheduledTick<Block> tick) {
		RedEventLogger.begin(new RedTickEvent(world.getTime(), block, pos, tick.priority));
		block.scheduledTick(world, pos, rand);
		RedEventLogger.end();
	}
}
