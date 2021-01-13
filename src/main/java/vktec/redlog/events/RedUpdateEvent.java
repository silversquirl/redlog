package vktec.redlog.events;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class RedUpdateEvent extends RedEvent.AtBlock {
	public final UpdateType type;
	public final Block from;
	public RedUpdateEvent(UpdateType type, long time, BlockState block, BlockPos pos, Block from) {
		super(time, pos, block);
		this.type = type;
		this.from = from;
	}

	@Override
	public String event() {
		return this.type.event;
	}
	@Override
	public String extraInfo() {
		return String.format("from %s", Registry.BLOCK.getId(this.from).getPath());
	}

	public static enum UpdateType {
		BLOCK("BUP"),
		STATE("SUP");

		public final String event;
		private UpdateType(String event) {
			this.event = event;
		}
	}
}
