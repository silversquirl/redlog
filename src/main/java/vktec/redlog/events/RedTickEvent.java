package vktec.redlog.events;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;

public class RedTickEvent extends RedEvent.AtBlock {
	public final TickPriority priority;

	public RedTickEvent(long time, BlockState block, BlockPos pos, TickPriority priority) {
		super(time, pos, block);
		this.priority = priority;
	}

	@Override
	public String extraInfo() {
		return String.format("%s(%d)", this.priority, this.priority.getIndex());
	}

	@Override
	public String event() {
		return "TIC";
	}
}
