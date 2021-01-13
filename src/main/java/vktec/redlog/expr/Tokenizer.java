package vktec.redlog.expr;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Tokenizer implements Iterator<Tokenizer.Token> {
	private static final Pattern pattern = Pattern.compile("(\\()|(\\))|(&&|\\|\\||[<>=]=?|!=)|(!)|\"([^\"]*)\"|'([^']*)'|([+-]?(?:(?:\\d*\\.\\d+|\\d+\\.)(?:e[+-]?\\d+)?|\\d+e[+-]\\d+))|([+-]?\\d+)|([a-zA-Z_]\\w*)");
	private static final List<TokenType> groups = List.of(
		TokenType.LPAR, TokenType.RPAR,
		TokenType.PREFIX, TokenType.BINARY,
		TokenType.STRING, TokenType.STRING,
		TokenType.NUM, TokenType.NUM,
		TokenType.IDENT
	);

	private final Matcher matcher;
	private Token nextTok;

	public Tokenizer(String expr) {
		this.matcher = pattern.matcher(expr);
		this.nextTok = this.getNext();
	}

	private Token getNext() {
		if (this.matcher.find()) {
			for (int i = 0; i < groups.size(); i++) {
				String text = this.matcher.group(i+1);
				if (text != null) {
					return new Token(groups.get(i), text);
				}
			}
		}
		return null;
	}

	public boolean hasNext() {
		return this.nextTok != null;
	}
	public Token next() {
		Token t = this.nextTok;
		if (t == null) throw new NoSuchElementException();
		this.nextTok = this.getNext();
		return t;
	}

	public static class Token {
		public final TokenType type;
		public final String text;
		public Token(TokenType type, String text) {
			this.type = type;
			this.text = text;
		}
		public String toString() {
			return this.type.name() + " " + this.text;
		}
	}
	public static enum TokenType {
		LPAR, RPAR,
		PREFIX, BINARY,
		IDENT, NUM, STRING
	}
}
