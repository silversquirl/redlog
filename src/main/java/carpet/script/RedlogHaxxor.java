package carpet.script;

import carpet.script.CarpetExpression;
import carpet.script.Context;
import carpet.script.value.Value;

public class RedlogHaxxor {
	public static Value evalCarpetExpression(CarpetExpression expr, Context ctx) {
		return expr.getExpr().eval(ctx);
	}
}
