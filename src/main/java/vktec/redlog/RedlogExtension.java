package vktec.redlog;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.settings.SettingsManager;
import net.minecraft.server.MinecraftServer;

public class RedlogExtension implements CarpetExtension {
	public static void noop() {}
	private static SettingsManager filterSettingsManager;
	static {
		filterSettingsManager = new SettingsManager("0.1.0", "redlog", "RedLog");
		CarpetServer.manageExtension(new RedlogExtension());
	}

	public static String getFilter(String name) {
		return (String)filterSettingsManager.getRule(name).get();
	}

	@Override
	public void onGameStarted() {
		filterSettingsManager.parseSettingsClass(RedlogFilterSettings.class);

		CarpetServer.settingsManager.addRuleObserver((commandSource, rule, value) -> {
			if (rule.categories.contains("redlog")) {
				RedEventLogger.get(rule.name).setFilter(commandSource, value);
			}
		});
	}

	@Override
	public SettingsManager customSettingsManager() {
		return filterSettingsManager;
	}
}
