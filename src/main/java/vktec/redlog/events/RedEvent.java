package vktec.redlog.events;

import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;

public abstract class RedEvent {
	public final long time;
	public final BlockState block;
	public final BlockPos pos;

	public RedEvent(long time, BlockState block, BlockPos pos) {
		this.time = time;
		this.block = block;
		this.pos = pos;
	}

	public abstract String event();
	public MutableText info() {
		return null;
	}

	public Map<String,Object> props() {
		return Map.of(
			"time", this.time,
			"block", this.block,
			"x", this.pos.getX(),
			"y", this.pos.getY(),
			"z", this.pos.getZ(),
			this.event().toLowerCase(), true
		);
	}
}
