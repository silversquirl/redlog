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

	@Override
	public void onGameStarted() {
		filterSettingsManager.parseSettingsClass(RedlogFilterSettings.class);

		CarpetServer.settingsManager.addRuleObserver((commandSource, rule, value) -> {
			System.out.printf("%s = %s\n", rule.name, value); // debugging
			if (rule.categories.contains("redlog")) {
				RedEventLogger.get(rule.name).setFilter(value);
			}
		});
	}

	@Override
	public SettingsManager customSettingsManager() {
		return filterSettingsManager;
	}
}
