package vktec.redlog.events;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public abstract class RedEvent {
	public final long time;
	public final BlockPos pos;

	public RedEvent(long time, BlockPos pos) {
		this.time = time;
		this.pos = pos;
	}

	public abstract String event();
	public MutableText info() {
		return null;
	}

	public Map<String,Object> props() {
		Map<String,Object> props = new HashMap<>();
		props.put("time", this.time);
		props.put("x", this.pos.getX());
		props.put("y", this.pos.getY());
		props.put("z", this.pos.getZ());
		props.put(this.event().toLowerCase(), true);
		return props;
	}

	public static abstract class AtBlock extends RedEvent {
		public final BlockState block;
		public AtBlock(long time, BlockPos pos, BlockState block) {
			super(time, pos);
			this.block = block;
		}

		public String extraInfo() {
			return null;
		}

		@Override
		public MutableText info() {
			String extra = this.extraInfo();
			MutableText text = new LiteralText(extra).formatted(Formatting.DARK_PURPLE);
			if (extra != null) {
				text = text.append(" ");
			}

			String blockId = Registry.BLOCK.getId(this.block.getBlock()).getPath();
			text = text.append(new LiteralText(blockId).formatted(Formatting.GRAY));
			return text;
		}

		@Override
		public Map<String,Object> props() {
			Map<String,Object> props = super.props();
			props.put("block", this.block);
			return props;
		}
	}
}
