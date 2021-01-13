package vktec.redlog.events;

import java.util.Map;
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ChunkPos;

public class RedChunkTicketEvent extends RedEvent {
	private ChunkPos cpos;
	private ChunkTicket ticket;
	private int currentLevel;
	public RedChunkTicketEvent(long time, ChunkPos pos, ChunkTicket ticket, int currentLevel) {
		super(time, pos.getStartPos());
		this.cpos = pos;
		this.ticket = ticket;
		this.currentLevel = currentLevel;
	}

	@Override
	public String event() {
		return "TKT";
	}
	@Override
	public MutableText info() {
		return new LiteralText(String.format(
			"%s %d (%d -> %d)",
			this.ticket.getType().toString(),
			this.ticket.getLevel(),
			this.currentLevel,
			this.newLevel()
		)).formatted(Formatting.DARK_PURPLE);
	}

	@Override
	public Map<String,Object> props() {
		Map<String,Object> props = super.props();
		props.put("cx", this.cpos.x);
		props.put("cz", this.cpos.z);
		props.put("type", this.ticket.getType().toString());
		props.put("level", this.ticket.getLevel());
		props.put("old", this.currentLevel);
		props.put("new", this.newLevel());
		return props;
	}

	private int newLevel() {
		if (this.ticket.getLevel() > this.currentLevel) {
			return this.ticket.getLevel();
		} else {
			return this.currentLevel;
		}
	}
}
