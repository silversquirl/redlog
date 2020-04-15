package vktec.redlog.mixins;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
import vktec.redlog.events.RedEvent;
import vktec.redlog.events.RedBlockEvent;
import vktec.redlog.events.RedUpdateEvent;

@Mixin(BlockState.class)
public abstract class BlockStateMixin {
	@Shadow public abstract Block getBlock();

	@Inject(method = "scheduledTick", at = @At("HEAD"))
	private void logTileTick(ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		RedEventLogger.get("tileTicks").log(world, (BlockState)(Object)this, pos);
		RedEventLogger.indent();
	}

	@Inject(method = "scheduledTick", at = @At("TAIL"))
	private void dedentAfterTileTick(CallbackInfo ci) {
		RedEventLogger.dedent();
	}

	@Inject(method = "onBlockAction", at = @At("HEAD"))
	private void logBlockEvent(World world, BlockPos pos, int type, int data, CallbackInfoReturnable ci) {
		if (world.isClient) return;
		RedEventLogger.get("blockEvents").log(new RedBlockEvent((ServerWorld)world, (BlockState)(Object)this, pos, type, data));
		RedEventLogger.indent();
	}

	@Inject(method = "onBlockAction", at = @At("TAIL"))
	private void dedentAfterBlockEvent(World world, BlockPos pos, int type, int data, CallbackInfoReturnable ci) {
		if (world.isClient) return;
		RedEventLogger.dedent();
	}

	@Inject(method = "neighborUpdate", at = @At("HEAD"))
	private void logBlockUpdates(World world, BlockPos pos, Block updaterBlock, BlockPos updaterPos, boolean bool, CallbackInfo ci) {
		if (world.isClient) return;
		RedEventLogger.get("blockUpdates").log(new RedUpdateEvent((ServerWorld)world, (BlockState)(Object)this, pos, updaterBlock));
		RedEventLogger.indent();
	}

	@Inject(method = "neighborUpdate", at = @At("TAIL"))
	private void dedentAfterBlockUpdate(World world, BlockPos pos, Block updaterBlock, BlockPos updaterPos, boolean bool, CallbackInfo ci) {
		if (world.isClient) return;
		RedEventLogger.dedent();
	}

	@Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"))
	private void logStateUpdates(Direction dir, BlockState updaterBlock, IWorld world, BlockPos pos, BlockPos updaterPos, CallbackInfoReturnable ci) {
		if (!(world instanceof ServerWorld)) return;
		RedEventLogger.get("stateUpdates").log(new RedUpdateEvent((ServerWorld)world, (BlockState)(Object)this, pos, updaterBlock.getBlock()));
		RedEventLogger.indent();
	}

	@Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"))
	private void dedentAfterStateUpdate(Direction dir, BlockState updaterBlock, IWorld world, BlockPos pos, BlockPos updaterPos, CallbackInfoReturnable ci) {
		if (!(world instanceof ServerWorld)) return;
		RedEventLogger.dedent();
	}
}
