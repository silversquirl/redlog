package vktec.redlog.events;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class RedTickEvent extends RedEvent {
	// TODO: priority?
	public RedTickEvent(long time, BlockState block, BlockPos pos) {
		super(time, block, pos);
	}

	@Override
	public String event() {
		return "TIC";
	}
}
