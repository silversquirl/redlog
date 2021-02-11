package vktec.redlog;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RedlogMod implements ModInitializer {
	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(literal("rlog")
				.requires(source -> source.hasPermissionLevel(2))
				.then(literal("pass")
					.then(argument("expr", greedyString())
						.executes(ctx -> this.addRule(ctx.getSource(), getString(ctx, "expr"), RedFilterRule.Action.PASS))))
				.then(literal("block")
					.then(argument("expr", greedyString())
						.executes(ctx -> this.addRule(ctx.getSource(), getString(ctx, "expr"), RedFilterRule.Action.BLOCK))))
				.then(literal("del")
					.then(argument("index", integer())
						.executes(ctx -> this.delRule(ctx.getSource(), getInteger(ctx, "index")))))
				.then(literal("show")
					.executes(ctx -> this.showRules(ctx.getSource())))
				.then(literal("clear")
					.executes(ctx -> this.clearRules(ctx.getSource()))));
		});
	}

	private int addRule(ServerCommandSource source, String expr, RedFilterRule.Action action) throws CommandSyntaxException {
		RedEventLogger log = RedEventLogger.getLogger(source.getEntityOrThrow());
		RedFilterRule rule = new RedFilterRule(expr, action);
		int index = log.addRule(rule);

		String msg = String.format("Added rule %d: %s", index, rule);
		source.sendFeedback(new LiteralText(msg), true);
		return 1;
	}

	private int delRule(ServerCommandSource source, int index) throws CommandSyntaxException {
		RedEventLogger log = RedEventLogger.getLogger(source.getEntityOrThrow());
		RedFilterRule rule = log.getRules().get(index);
		log.delRule(index);

		String msg = String.format("Removed rule %d: %s", index, rule);
		source.sendFeedback(new LiteralText(msg), true);
		return 1;
	}

	private int showRules(ServerCommandSource source) throws CommandSyntaxException {
		RedEventLogger log = RedEventLogger.getLogger(source.getEntityOrThrow());
		List<RedFilterRule> rules = log.getRules();
		source.sendFeedback(new LiteralText(String.format("%d rules:", rules.size())), true);
		for (int i = 0; i < rules.size(); i++) {
			RedFilterRule rule = rules.get(i);

			MutableText text = new LiteralText(" ");
			text = text.append(new LiteralText(Integer.toString(i) + ": ").formatted(Formatting.GRAY));
			text = text.append(rule.toString() + " ");
			text = text.append(new LiteralText(Integer.toString(rule.getMatchCount()) + " matches").formatted(Formatting.DARK_GRAY));
			source.sendFeedback(text, false);
		}
		return 1;
	}

	private int clearRules(ServerCommandSource source) throws CommandSyntaxException {
		RedEventLogger log = RedEventLogger.getLogger(source.getEntityOrThrow());
		log.clearRules();
		source.sendFeedback(new LiteralText("Cleared all rules"), true);
		return 1;
	}
}
