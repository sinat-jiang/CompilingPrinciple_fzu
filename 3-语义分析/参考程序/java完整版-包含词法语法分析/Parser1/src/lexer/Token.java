package lexer;

public class Token {

	public final int tag;
	public String symbol;
	public Token(int t,String s) { symbol = s;tag = t; }
	public String toString() {return "" + (char)tag;}
}
