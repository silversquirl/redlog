package vktec.redlog;

import carpet.CarpetServer;
import carpet.logging.Logger;
import carpet.logging.LoggerRegistry;
import carpet.script.CarpetExpression;
import carpet.script.CarpetContext;
import carpet.script.Context;
import carpet.script.exception.ExpressionException;
import carpet.script.value.BlockValue;
import carpet.script.value.NumericValue;
import carpet.script.value.StringValue;
import carpet.utils.Messenger;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.BaseText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import vktec.redlog.events.RedEvent;

public class RedEventLogger {
	private static int indentLevel;
	private static String indentString = " ";
	private static StringBuffer indentBuffer = new StringBuffer(indentString);

	public static void indent() {
		indentLevel++;
	}
	public static void dedent() {
		indentLevel--;
		if (indentLevel < 0) indentLevel = 0;
	}
	// Called from ServerWorldMixin at the end of each tick just to make sure nothing's left over (eg. if logger got deactivated during an event)
	public static void clearIndent() {
		if (indentLevel != 0) System.out.println("Cleared all indents");
		indentLevel = 0;
	}

	private static String getIndentString() {
		if (indentLevel == 0) return "";
		if (indentLevel == indentBuffer.length()) return indentString;

		if (indentBuffer.length() > indentLevel) {
			indentBuffer.setLength(indentLevel);
		} else while (indentBuffer.length() < indentLevel) {
			indentBuffer.append(' ');
		}

		return indentString = indentBuffer.toString();
	}

	private static final String TEMPLATE = "[%d] %s (%d, %d, %d) %s %s";
	private static final Map<String,RedEventLogger> loggerCache = new Object2ObjectOpenHashMap<>(5);
	private static final Map<String, CarpetExpression> filterCache = new WeakHashMap<>();
	private static final CarpetContext scriptContext = new CarpetContext(CarpetServer.scriptServer.globalHost, null, new BlockPos(0, 0, 0));

	private static String name2kind(String name) {
		switch (name) {
			case "block36": return "B36";
			case "blockEvents": return "BEV";
			case "blockUpdates": return "BUP";
			case "stateUpdates": return "SUP";
			case "tileTicks": return "TIC";
		}
		return null;
	}

	public static RedEventLogger get(String logName) {
		return loggerCache.computeIfAbsent(logName, (name) -> new RedEventLogger(name));
	}

	private final Logger logger;
	private final String kind;

	private RedEventLogger(String logName) {
		this.logger = LoggerRegistry.getLogger(logName);
		this.kind = RedEventLogger.name2kind(logName);
	}

	private static CarpetExpression computeOrGetFilter(String filterString) {
		CarpetExpression filter = RedEventLogger.filterCache.get(filterString);
		if (filter == null) {
			filter = new CarpetExpression(RedEventLogger.scriptContext.host.main, filterString, null, null);
			RedEventLogger.filterCache.put(filterString, filter);
		}
		return filter;
	}

	private boolean runFilter(String filterString, RedEvent ev) {
		if (filterString == null || filterString == "") return true;
		if (CarpetServer.scriptServer.stopAll) return false;

		final CarpetExpression filter = RedEventLogger.computeOrGetFilter(filterString);

		try {
			// TODO: add event-specific extra info variables here
			RedEventLogger.scriptContext
				.with("_", (c, t) -> new BlockValue(ev.block, ev.world, ev.pos))
				.with("x", (c, t) -> new NumericValue(ev.pos.getX()).bindTo("x"))
				.with("y", (c, t) -> new NumericValue(ev.pos.getY()).bindTo("y"))
				.with("z", (c, t) -> new NumericValue(ev.pos.getZ()).bindTo("z"))
				.with("info", (c, t) -> new StringValue(ev.getInfoString()));
			return filter.getExpr().eval(RedEventLogger.scriptContext).getBoolean();
		} catch (ExpressionException e) {
			// TODO: Probably want better reporting than this
			System.out.println(e);
			return false;
		} finally {
			RedEventLogger.scriptContext.variables.clear(); // Reset the context for next time
		}
	}

	public boolean enabled() {
		return this.logger.hasOnlineSubscribers();
	}

	public void log(World world, BlockPos pos) {
		this.log(new RedEvent((ServerWorld)world, pos));
	}

	public void log(World world, BlockState block, BlockPos pos) {
		this.log(new RedEvent((ServerWorld)world, block, pos));
	}

	public void log(RedEvent ev) {
		if (!this.enabled()) return;

		this.logger.log((filterString) -> {
			if (!this.runFilter(filterString, ev)) return null;

			String info = ev.getInfoString();
			if (info == null) info = "";
			else info += " ";

			final String blockName = Registry.BLOCK.getId(ev.block.getBlock()).getPath();

			return new BaseText[]{Messenger.c(
				String.format("g [%d] ", ev.world.getTime()),
				"w " + RedEventLogger.getIndentString(),
				"y " + this.kind + " ",
				Messenger.tp("c", ev.pos),
				"p  " + info,
				"g " + blockName
			)};
		});
	}
}
