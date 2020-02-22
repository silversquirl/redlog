package vktec.redlog.mixins;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vktec.redlog.RedEventLogger;

@Mixin(BlockState.class)
public abstract class BlockStateMixin {
	@Shadow public abstract Block getBlock();

	@Inject(method = "scheduledTick", at = @At("HEAD"))
	private void logTileTick(ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		RedEventLogger logger = RedEventLogger.get("tileTicks");
		if (!logger.enabled()) return;
		logger.log(world, pos);
		RedEventLogger.indent();
	}

	@Inject(method = "scheduledTick", at = @At("TAIL"))
	private void dedentAfterTileTick(CallbackInfo ci) {
		if (RedEventLogger.get("tileTicks").enabled()) RedEventLogger.dedent();
	}

	@Inject(method = "onBlockAction", at = @At("HEAD"))
	private void logBlockEvent(World world, BlockPos pos, int type, int data, CallbackInfoReturnable ci) {
		if (world.isClient) return;

		RedEventLogger logger = RedEventLogger.get("blockEvents");
		if (!logger.enabled()) return;

		String msg;
		if (this.getBlock() instanceof PistonBlock) {
			String typeStr;
			switch (type) {
			case 0:
				typeStr = "extend";
				break;
			case 1:
				typeStr = "retract";
				break;
			case 2:
				typeStr = "drop";
				break;
			default:
				typeStr = "unknown";
				break;
			}
			String dataStr = Direction.byId(data).getName();
			msg = String.format("%s(%d) %s(%d)", typeStr, type, dataStr, data);
		} else {
			msg = String.format("%d %d", type, data);
		}
		logger.log(world, pos, msg);
		RedEventLogger.indent();
	}

	@Inject(method = "onBlockAction", at = @At("TAIL"))
	private void dedentAfterBlockEvent(CallbackInfoReturnable ci) {
		if (RedEventLogger.get("blockEvents").enabled()) RedEventLogger.dedent();
	}

	@Inject(method = "neighborUpdate", at = @At("HEAD"))
	private void logBlockUpdates(World world, BlockPos pos, Block updaterBlock, BlockPos updaterPos, boolean bool, CallbackInfo ci) {
		if (world.isClient) return;
		RedEventLogger logger = RedEventLogger.get("blockUpdates");
		if (!logger.enabled()) return;
		logger.log(world, pos, String.format("from %s", Registry.BLOCK.getId(updaterBlock).getPath()));
		RedEventLogger.indent();
	}

	@Inject(method = "neighborUpdate", at = @At("TAIL"))
	private void dedentAfterBlockUpdate(CallbackInfo ci) {
		if (RedEventLogger.get("blockUpdates").enabled()) RedEventLogger.dedent();
	}

	@Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"))
	private void logStateUpdates(Direction dir, BlockState updaterBlock, IWorld world, BlockPos pos, BlockPos updaterPos, CallbackInfoReturnable ci) {
		RedEventLogger logger = RedEventLogger.get("stateUpdates");
		if (!logger.enabled()) return;
		logger.log((World)world, pos, String.format("from %s", Registry.BLOCK.getId(updaterBlock.getBlock()).getPath()));
		RedEventLogger.indent();
	}

	@Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"))
	private void dedentAfterStateUpdate(CallbackInfoReturnable ci) {
		if (RedEventLogger.get("stateUpdates").enabled()) RedEventLogger.dedent();
	}
}
