package vktec.redlog.events;

import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class RedBlockEvent extends RedEvent {
	public final int type, data;

	public RedBlockEvent(long time, BlockState block, BlockPos pos, int type, int data) {
		super(time, block, pos);
		this.type = type;
		this.data = data;
	}

	@Override
	public String event() {
		return "BEV";
	}
	@Override
	public MutableText info() {
		String str;
		if (this.block.getBlock() instanceof PistonBlock) {
			String typeStr;
			switch (type) {
			case 0:
				typeStr = "extend";
				break;
			case 1:
				typeStr = "retract";
				break;
			case 2:
				typeStr = "drop";
				break;
			default:
				typeStr = "unknown";
				break;
			}
			String dataStr = Direction.byId(data).getName();
			str = String.format("%s(%d) %s(%d)", typeStr, type, dataStr, data);
		} else {
			str = String.format("%d %d", type, data);
		}
		return new LiteralText(str);
	}
}
