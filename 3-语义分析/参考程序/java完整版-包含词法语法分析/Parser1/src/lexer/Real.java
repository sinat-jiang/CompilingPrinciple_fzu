package lexer;
public class Real extends Token {

	public final float value;
	public Real(float v) { super(Tag.REAL,String.valueOf(v)); value = v; }
	public String toString() { return "" + value; }
}
