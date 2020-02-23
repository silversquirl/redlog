package vktec.redlog;

import carpet.CarpetServer;
import carpet.logging.Logger;
import carpet.logging.LoggerRegistry;
import carpet.script.CarpetExpression;
import carpet.script.CarpetContext;
import carpet.script.Context;
import carpet.script.RedlogHaxxor;
import carpet.script.exception.ExpressionException;
import carpet.script.value.BlockValue;
import carpet.script.value.NumericValue;
import carpet.script.value.StringValue;
import carpet.utils.Messenger;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.reflect.Field;
import java.util.Map;
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

	private static CarpetContext scriptContext = new CarpetContext(CarpetServer.scriptServer.globalHost, null, new BlockPos(0, 0, 0));

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
	private static final Object2ObjectOpenHashMap<String,RedEventLogger> cache = new Object2ObjectOpenHashMap(4);

	private static final Map<String,String> name2kind = Map.of(
		"block36", "B36",
		"blockEvents", "BEV",
		"blockUpdates", "BUP",
		"stateUpdates", "SUP",
		"tileTicks", "TIC"
	);

	public static RedEventLogger get(String logName) {
		return cache.computeIfAbsent(logName, (name) -> new RedEventLogger(name));
	}

	private final Logger logger;
	private final String kind;
	private CarpetExpression filter;

	private RedEventLogger(String logName) {
		this.logger = LoggerRegistry.getLogger(logName);
		this.kind = name2kind.get(logName);
		this.setFilter(null, RedlogExtension.getFilter(logName));
	}

	public void setFilter(ServerCommandSource commandSource, String filter) {
		if (filter == null || filter == "") {
			this.filter = null;
		} else {
			this.scriptContext.s = commandSource;
			this.filter = new CarpetExpression(RedEventLogger.scriptContext.host.main, filter, null, null);
		}
	}

	private boolean runFilter(RedEvent ev) {
		if (this.filter == null) return true;
		if (CarpetServer.scriptServer.stopAll) return false;
		try {
			// TODO: add event-specific extra info variables here
			RedEventLogger.scriptContext
				.with("_", (c, t) -> new BlockValue(ev.block, ev.world, ev.pos))
				.with("x", (c, t) -> new NumericValue(ev.pos.getX()).bindTo("x"))
				.with("y", (c, t) -> new NumericValue(ev.pos.getY()).bindTo("y"))
				.with("z", (c, t) -> new NumericValue(ev.pos.getZ()).bindTo("z"))
				.with("info", (c, t) -> new StringValue(ev.getInfoString()));
			// XXX: gnembon/fabric-carpet#157
			//return this.filter.getExpr().eval(RedEventLogger.scriptContext).getBoolean();
			return RedlogHaxxor.evalCarpetExpression(this.filter, RedEventLogger.scriptContext).getBoolean();
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

		if (this.filter != null) {
			if (!this.runFilter(ev)) return;
		}

		String info = ev.getInfoString();
		if (info != null) info += " ";
		final String finalInfo = info; // 'cause Java complains at me otherwise

		final String blockName = Registry.BLOCK.getId(ev.block.getBlock()).getPath();

		this.logger.log(() -> {
			return new BaseText[]{Messenger.c(
				String.format("g [%d] ", ev.world.getTime()),
				"w " + RedEventLogger.getIndentString(),
				"y " + this.kind + " ",
				Messenger.tp("c", ev.pos),
				"p  " + finalInfo,
				"g " + blockName
			)};
		});
	}
}
