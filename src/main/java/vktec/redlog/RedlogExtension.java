package vktec.redlog;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.logging.Logger;
import carpet.logging.LoggerRegistry;

public class RedlogExtension implements CarpetExtension {
	public static void noop() {}

	static {
		CarpetServer.manageExtension(new RedlogExtension());
	}

	public static boolean __block36;
	public static boolean __blockEvents;
	public static boolean __blockUpdates;
	public static boolean __stateUpdates;
	public static boolean __tileTicks;

	private static void registerLogger(String name) {
		try {
			LoggerRegistry.registerLogger(name, new Logger(RedlogExtension.class.getField("__"+name), name, null, null));
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Could not create logger "+name);
		}
	}

	public void registerLoggers() {
		registerLogger("block36");
		registerLogger("blockEvents");
		registerLogger("blockUpdates");
		registerLogger("stateUpdates");
		registerLogger("tileTicks");
	}
}
