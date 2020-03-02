package vktec.redlog.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vktec.redlog.RedEventLogger;
import vktec.redlog.events.RedBlock36Event;

@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin extends BlockEntity {
	@Shadow private native BlockState getHeadBlockState();
	@Shadow public abstract boolean isExtending();

	private PistonBlockEntityMixin() {
		super(BlockEntityType.PISTON);
	}

	public void setLocation(World world, BlockPos pos) {
		super.setLocation(world, pos);
		this.log(true);
	}

	public void markRemoved() {
		if (!this.isRemoved()) this.log(false);
		super.markRemoved();
	}

	private void log(boolean start) {
		if (this.world.isClient) return;
		RedEventLogger.get("block36").log(new RedBlock36Event((ServerWorld)this.world, this.getHeadBlockState(), this.pos, start));
	}
}
