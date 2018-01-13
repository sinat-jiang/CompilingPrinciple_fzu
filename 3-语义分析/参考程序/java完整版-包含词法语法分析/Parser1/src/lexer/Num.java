package lexer;

public class Num extends Token {

	public final int value;
	public Num(int v) { super(Tag.NUM, String.valueOf(v)); value = v; }
	public String toString() { return "" + value; }
}
