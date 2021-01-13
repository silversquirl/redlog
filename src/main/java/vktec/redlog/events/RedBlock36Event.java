package vktec.redlog.events;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class RedBlock36Event extends RedEvent.AtBlock {
	public final boolean start;
	public RedBlock36Event(long time, BlockState block, BlockPos pos, boolean start) {
		super(time, pos, block);
		this.start = start;
	}

	@Override
	public String event() {
		return "B36";
	}

	@Override
	public String extraInfo() {
		return this.start ? "start" : "finish";
	}
}
