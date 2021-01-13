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
						.executes(ctx -> this.passRule(ctx.getSource(), getString(ctx, "expr")))))
				.then(literal("block")
					.then(argument("expr", greedyString())
						.executes(ctx -> this.blockRule(ctx.getSource(), getString(ctx, "expr")))))
				.then(literal("del")
					.then(argument("index", integer())
						.executes(ctx -> this.delRule(ctx.getSource(), getInteger(ctx, "index")))))
				.then(literal("show")
					.executes(ctx -> this.showRules(ctx.getSource())))
				.then(literal("clear")
					.executes(ctx -> this.clearRules(ctx.getSource()))));
		});
	}

	private int passRule(ServerCommandSource source, String expr) throws CommandSyntaxException {
		RedEventLogger log = RedEventLogger.getLogger(source.getEntityOrThrow());
		log.addRule(new RedFilterRule(expr, RedFilterRule.Action.PASS));
		return 1;
	}

	private int blockRule(ServerCommandSource source, String expr) throws CommandSyntaxException {
		RedEventLogger log = RedEventLogger.getLogger(source.getEntityOrThrow());
		log.addRule(new RedFilterRule(expr, RedFilterRule.Action.BLOCK));
		return 1;
	}

	private int delRule(ServerCommandSource source, int index) throws CommandSyntaxException {
		RedEventLogger log = RedEventLogger.getLogger(source.getEntityOrThrow());
		log.delRule(index);
		return 1;
	}

	private int showRules(ServerCommandSource source) throws CommandSyntaxException {
		RedEventLogger log = RedEventLogger.getLogger(source.getEntityOrThrow());
		List<RedFilterRule> rules = log.getRules();
		source.sendFeedback(new LiteralText(String.format("%d rules:", rules.size())), true);
		for (int i = 0; i < rules.size(); i++) {
			MutableText text = new LiteralText(" ");
			text = text.append(new LiteralText(Integer.toString(i) + ": ").formatted(Formatting.GRAY));
			text = text.append(rules.get(i).toString());
			source.sendFeedback(text, false);
		}
		return 1;
	}

	private int clearRules(ServerCommandSource source) throws CommandSyntaxException {
		RedEventLogger log = RedEventLogger.getLogger(source.getEntityOrThrow());
		log.clearRules();
		return 1;
	}
}
