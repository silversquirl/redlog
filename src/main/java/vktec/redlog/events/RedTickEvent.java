package vktec.redlog.events;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class RedTickEvent extends RedEvent.AtBlock {
	// TODO: priority?
	public RedTickEvent(long time, BlockState block, BlockPos pos) {
		super(time, pos, block);
	}

	@Override
	public String event() {
		return "TIC";
	}
}
