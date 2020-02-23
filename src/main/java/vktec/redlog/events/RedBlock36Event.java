package vktec.redlog.events;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;

public class RedBlock36Event extends RedEvent {
	public final boolean start;
	public RedBlock36Event(ServerWorld world, BlockState block, BlockPos pos, boolean start) {
		super(world, block, pos);
		this.start = start;
	}

	@Override
	public String getInfoString() {
		return this.start ? "start" : "finish";
	}
}
