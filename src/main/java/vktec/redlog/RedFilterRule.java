package vktec.redlog;

import vktec.redlog.events.RedEvent;
import vktec.redlog.expr.RedExpr;

public class RedFilterRule {
	private final RedExpr expr;
	public final Action action;
	public RedFilterRule(String expr, Action action) {
		this(new RedExpr(expr), action);
	}
	public RedFilterRule(RedExpr expr, Action action) {
		this.expr = expr;
		this.action = action;
	}

	public boolean match(RedEvent ev) {
		Object result = this.expr.eval(ev.props());
		return ((Boolean)result).booleanValue();
	}

	public String toString() {
		return String.format("%s <TODO>", this.action.name());
	}

	public static enum Action {
		BLOCK, PASS
	}
}
