package vktec.redlog.events;

import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.server.world.ServerWorld;

public class RedBlockEvent extends RedEvent {
	public final int type, data;

	public RedBlockEvent(ServerWorld world, BlockState block, BlockPos pos, int type, int data) {
		super(world, block, pos);
		this.type = type;
		this.data = data;
	}

	@Override
	public String getInfoString() {
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
