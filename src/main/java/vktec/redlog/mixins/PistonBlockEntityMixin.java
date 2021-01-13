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

	@Override
	public void setLocation(World world, BlockPos pos) {
		if (!world.isClient) {
			this.log(world, pos, true);
		}
		super.setLocation(world, pos);
		if (!world.isClient) {
			RedEventLogger.end();
		}
	}

	@Override
	public void markRemoved() {
		boolean logged = false;
		if (this.world != null && !this.world.isClient && !this.isRemoved()) {
			logged = true;
			this.log(this.world, this.pos, false);
		}
		super.markRemoved();
		if (logged) {
			RedEventLogger.end();
		}
	}

	private void log(World world, BlockPos pos, boolean start) {
		RedEventLogger.begin(new RedBlock36Event(world.getTime(), this.getHeadBlockState(), pos, start));
	}
}
