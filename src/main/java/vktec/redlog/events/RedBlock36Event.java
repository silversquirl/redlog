package vktec.redlog.events;

import net.minecraft.block.BlockState;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;

public class RedBlock36Event extends RedEvent {
	public final boolean start;
	public RedBlock36Event(long time, BlockState block, BlockPos pos, boolean start) {
		super(time, block, pos);
		this.start = start;
	}

	@Override
	public String event() {
		return "B36";
	}

	@Override
	public MutableText info() {
		return new LiteralText(this.start ? "start" : "finish");
	}
}
