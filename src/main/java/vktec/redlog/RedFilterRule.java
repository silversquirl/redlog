package vktec.redlog;

import vktec.redlog.events.RedEvent;
import vktec.redlog.expr.RedExpr;

public class RedFilterRule {
	private final String source;
	private final RedExpr expr;
	public final Action action;
	public RedFilterRule(String expr, Action action) {
		this.source = expr;
		this.expr = new RedExpr(expr);
		this.action = action;
	}

	public boolean match(RedEvent ev) {
		Object result = this.expr.eval(ev.props());
		return ((Boolean)result).booleanValue();
	}

	public String toString() {
		// TODO: convert actions into string rather than storing source code
		return this.action.name() + " " + this.source;
	}

	public static enum Action {
		BLOCK, PASS
	}
}
