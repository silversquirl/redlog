package vktec.redlog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vktec.redlog.events.RedEvent;

public class RedEventLogger {
	private static final Logger LOGGER = LogManager.getLogger();

	private static final HashMap<Entity,RedEventLogger> loggers = new HashMap<>();
	public static RedEventLogger getLogger(Entity entity) {
		return loggers.computeIfAbsent(entity, (output) -> new RedEventLogger(output));
	}
	public static void begin(RedEvent ev) {
		for (Map.Entry<Entity,RedEventLogger> pair : loggers.entrySet()) {
			if (pair.getKey().removed) {
				loggers.remove(pair.getKey());
			} else {
				pair.getValue().beginEvent(ev);
			}
		}
	}
	public static void end() {
		for (RedEventLogger logger : loggers.values()) {
			logger.endEvent();
		}
	}

	private int indentLevel = 0;
	private long indentMask = 0;
	private StringBuffer indentBuffer = new StringBuffer();
	private String indentString;
	private String getIndentString() {
		if (indentLevel == 0) return "";
		if (indentLevel != indentBuffer.length()) {
			if (indentBuffer.length() > indentLevel) {
				indentBuffer.setLength(indentLevel);
			} else {
				while (indentBuffer.length() < indentLevel) {
					indentBuffer.append(' ');
				}
			}
			this.indentString = indentBuffer.toString();
		}

		return this.indentString;
	}

	protected final CommandOutput output;
	protected final ArrayList<RedFilterRule> ruleset = new ArrayList<>();
	public RedEventLogger(CommandOutput output) {
		this.output = output;
	}

	public void addRule(RedFilterRule rule) {
		this.ruleset.add(rule);
	}
	public void delRule(int index) {
		this.ruleset.remove(index);
	}
	public List<RedFilterRule> getRules() {
		return this.ruleset;
	}
	public void clearRules() {
		this.ruleset.clear();
	}

	private boolean shouldPass(RedEvent ev) {
		RedFilterRule.Action act = RedFilterRule.Action.BLOCK;
		for (RedFilterRule rule : this.ruleset) {
			try {
				if (rule.match(ev)) {
					act = rule.action;
				}
			} catch (Exception e) {
				// Ignore this rule
			}
		}
		return act == RedFilterRule.Action.PASS;
	}

	protected void beginEvent(RedEvent ev) {
		this.indentMask <<= 1;
		if (this.shouldPass(ev)) {
			MutableText msg = new LiteralText("[" + Long.toString(ev.time) + "] ").formatted(Formatting.GRAY);
			msg = msg.append(new LiteralText(this.getIndentString()));
			msg = msg.append(new LiteralText(ev.event()).formatted(Formatting.YELLOW));

			String coord = String.format("(%d, %d, %d)", ev.pos.getX(), ev.pos.getY(), ev.pos.getZ());
			String tpCommand = String.format("/tp %d %d %d", ev.pos.getX(), ev.pos.getY(), ev.pos.getZ());
			msg = msg.append(new LiteralText(coord).setStyle(Style.EMPTY
				.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, tpCommand))
				.withFormatting(Formatting.AQUA)));

			MutableText info = ev.info();
			if (info != null) {
				msg = msg.append(" ");
				msg = msg.append(info);
			}

			this.output.sendSystemMessage(msg, Util.NIL_UUID);
			this.indentLevel++;
			this.indentMask |= 1;
		}
	}
	protected void endEvent() {
		if ((this.indentMask & 1) != 0) {
			this.indentLevel--;
		}
		this.indentMask >>= 1;
	}
}
