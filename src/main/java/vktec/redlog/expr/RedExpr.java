package vktec.redlog.expr;

import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

public class RedExpr {
	private final ArrayList<Action> code;
	public RedExpr(String expr) {
		Tokenizer tokens = new Tokenizer(expr);
		this.code = new ArrayList<>();
		Stack<Operator> opstack = new Stack<>();
		while (tokens.hasNext()) {
			// TODO: generate syntax errors
			Tokenizer.Token tok = tokens.next();
			switch (tok.type) {
			case LPAR:
				opstack.add(Operator.GROUP);
			case RPAR:
				for (;;) {
					Operator op = opstack.pop();
					if (op != Operator.GROUP) {
						this.code.add(new OpAction(op));
					} else {
						break;
					}
				}
				break;

			case PREFIX:
			case BINARY:
				Operator op = Operator.fromTok(tok);
				while (!opstack.empty() && opstack.peek().precedence > op.precedence) {
					this.code.add(new OpAction(opstack.pop()));
				}
				opstack.add(op);
				break;

			case IDENT:
				this.code.add(new VarAction(tok.text));
				break;
			case NUM:
				this.code.add(new ValAction(new Double(tok.text)));
				break;
			case STRING:
				this.code.add(new ValAction(tok.text));
				break;
			}
		}
		while (!opstack.empty()) {
			this.code.add(new OpAction(opstack.pop()));
		}
	}

	public Object eval(Map<String,Object> vars) {
		Stack<Object> stack = new Stack<>();
		for (Action action : this.code) {
			action.exec(vars, stack);
		}
		return stack.peek();
	}

	private static abstract class Action {
		public abstract void exec(Map<String,Object> vars, Stack<Object> stack);
	}
	private static class OpAction extends Action {
		private final Operator op;
		public OpAction(Operator op) {
			this.op = op;
		}
		Object a, b;
		public void exec(Map<String,Object> vars, Stack<Object> stack) {
			switch (this.op) {
			// TODO: short-circuiting
			case OR:
				b = stack.pop();
				a = stack.pop();
				stack.push(new Boolean(((Boolean)a).booleanValue() || ((Boolean)b).booleanValue()));
				break;

			case AND:
				b = stack.pop();
				a = stack.pop();
				stack.push(new Boolean(((Boolean)a).booleanValue() && ((Boolean)b).booleanValue()));
				break;

			case NOT:
				a = stack.pop();
				stack.push(new Boolean(!((Boolean)a).booleanValue()));
				break;

			case EQ:
				stack.push(new Boolean(compare(stack) == 0));
				break;
			case NE:
				stack.push(new Boolean(compare(stack) != 0));
				break;
			case LT:
				stack.push(new Boolean(compare(stack) < 0));
				break;
			case GT:
				stack.push(new Boolean(compare(stack) > 0));
				break;
			case LE:
				stack.push(new Boolean(compare(stack) <= 0));
				break;
			case GE:
				stack.push(new Boolean(compare(stack) >= 0));
				break;
			}
		}
		public static int compare(Stack<Object> stack) {
			Object b = stack.pop(), a = stack.pop();
			if (a instanceof Number && b instanceof Number) {
				return Double.compare(((Number)a).doubleValue(), ((Number)b).doubleValue());
			} else {
				return ((Comparable)a).compareTo(b);
			}
		}
	}
	private static class VarAction extends Action {
		private final String name;
		public VarAction(String name) {
			this.name = name;
		}
		public void exec(Map<String,Object> vars, Stack<Object> stack) {
			stack.add(vars.get(this.name));
		}
	}
	private static class ValAction extends Action {
		private final Object val;
		public ValAction(Object val) {
			this.val = val;
		}
		public void exec(Map<String,Object> vars, Stack<Object> stack) {
			stack.add(this.val);
		}
	}

	private static enum Operator {
		GROUP(-1),
		OR(0), AND(1), NOT(3),
		EQ(2), NE(2), LT(2), GT(2), LE(2), GE(2);

		public final int precedence;
		private Operator(int precedence) {
			this.precedence = precedence;
		}

		public static Operator fromTok(Tokenizer.Token tok) {
			switch (tok.type) {
			case PREFIX:
				switch (tok.text) {
				case "!":
					return NOT;
				}

			case BINARY:
				switch (tok.text) {
				case "||":
					return OR;
				case "&&":
					return AND;
				case "==":
				case "=":
					return EQ;
				case "!=":
					return NE;
				case "<":
					return LT;
				case ">":
					return GT;
				case "<=":
					return LE;
				case ">=":
					return GE;
				}
			}
			throw new RuntimeException("Invalid operator");
		}
	}
}
