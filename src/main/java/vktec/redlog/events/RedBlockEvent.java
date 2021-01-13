package vktec.redlog.events;

import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class RedBlockEvent extends RedEvent.AtBlock {
	public final int type, data;

	public RedBlockEvent(long time, BlockState block, BlockPos pos, int type, int data) {
		super(time, pos, block);
		this.type = type;
		this.data = data;
	}

	@Override
	public String event() {
		return "BEV";
	}
	@Override
	public String extraInfo() {
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
			return String.format("%s(%d) %s(%d)", typeStr, type, dataStr, data);
		} else {
			return String.format("%d %d", type, data);
		}
	}
}
