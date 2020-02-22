package vktec.redlog;

import carpet.logging.Logger;
import carpet.logging.LoggerRegistry;
import carpet.utils.Messenger;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.block.BlockState;
import net.minecraft.text.BaseText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

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
	private Pattern filter;

	private RedEventLogger(String logName) {
		this.logger = LoggerRegistry.getLogger(logName);
		this.kind = name2kind.get(logName);
		try {
			this.setFilter((String)RedlogFilterSettings.class.getDeclaredField(logName).get(null));
		} catch (IllegalAccessException e) {
			System.out.println("Error: Unable to access field '" + this.logger.getLogName() + "'");
			((Object)null).toString(); // Crash the game
		} catch (NoSuchFieldException e) {
			System.out.println("Error: No such field '" + this.logger.getLogName() + "'");
			((Object)null).toString(); // Crash the game
		}
	}

	public void setFilter(String filter) {
		if (filter == null || filter == "") {
			this.filter = null;
		} else {
			this.filter = Pattern.compile(filter);
		}
	}

	public boolean enabled() {
		return this.logger.hasOnlineSubscribers();
	}

	public void log(World world, BlockPos pos) {
		this.log(world, pos, "");
	}

	public void log(World world, BlockPos pos, String info) {
		this.log(world, world.getBlockState(pos), pos, info);
	}

	public void log(World world, BlockState block, BlockPos pos, String info) {
		if (!this.enabled()) return;
		String blockType = Registry.BLOCK.getId(block.getBlock()).getPath();

		if (this.filter != null) {
			// TODO: improve filtering to allow more complex matching
			if (!this.filter.matcher(blockType).matches()) return;
		}

		this.logger.log(() -> {
			return new BaseText[]{Messenger.c(
				String.format("g [%d] ", world.getTime()),
				"w " + RedEventLogger.getIndentString(),
				"y " + this.kind + " ",
				Messenger.tp("c", pos),
				"p  " + info + (info == "" ? "" : " "),
				"g " + blockType
			)};
		});
	}
}
