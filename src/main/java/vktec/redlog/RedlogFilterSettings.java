package vktec.redlog;

import carpet.settings.Rule;

public class RedlogFilterSettings {
	@Rule(desc = "Filter for block 36 logging", category = "redlog", options = {}, strict = false)
	public static String block36 = "";

	@Rule(desc = "Filter for block event logging", category = "redlog", options = {}, strict = false)
	public static String blockEvents = "";

	@Rule(desc = "Filter for block update logging", category = "redlog", options = {}, strict = false)
	public static String blockUpdates = "";

	@Rule(desc = "Filter for state update logging", category = "redlog", options = {}, strict = false)
	public static String stateUpdates = "";

	@Rule(desc = "Filter for tile tick logging", category = "redlog", options = {}, strict = false)
	public static String tileTicks = "";
}
