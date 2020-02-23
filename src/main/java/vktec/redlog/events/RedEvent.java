package vktec.redlog.events;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class RedEvent {
	public final ServerWorld world;
	public final BlockState block;
	public final BlockPos pos;

	public RedEvent(ServerWorld world, BlockPos pos) {
		this(world, world.getBlockState(pos), pos);
	}

	public RedEvent(ServerWorld world, BlockState block, BlockPos pos) {
		this.world = world;
		this.block = block;
		this.pos = pos;
	}

	public String getInfoString() {
		return null;
	}
}
