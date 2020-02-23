package vktec.redlog.events;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.server.world.ServerWorld;

public class RedUpdateEvent extends RedEvent {
	public final Block updater;
	public RedUpdateEvent(ServerWorld world, BlockState block, BlockPos pos, Block updater) {
		super(world, block, pos);
		this.updater = updater;
	}

	@Override
	public String getInfoString() {
		return String.format("from %s", Registry.BLOCK.getId(this.updater).getPath());
	}
}
