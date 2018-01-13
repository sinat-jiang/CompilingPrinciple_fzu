package grammar;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;

public class Grammar {
    char peek;
    int ch;
    InputStream in;
    public Hashtable symbols = new Hashtable();

    //读取文法规则集
    public ArrayList<Item> readGrammar()throws IOException {
        ArrayList<Item> grammar = new ArrayList<Item>();
        ArrayList<Symbol> grammarSymbol = new ArrayList<Symbol>();
        boolean left = true;//left为true时表示读取到一条文法规则的头部
        Symbol symbol;
        int id = 0;
        Item item = new Item();
        readSymbol();//读取文法符号
        in = new FileInputStream("./test/grammar.txt");

        do {
            while (true) {
                readch();
                if (peek == ' ' || peek == '\t' || peek == '\r') continue;
                else if (peek == '→') {//读取到 '->'，表示将要读取文法后部
                    left = false;
                    continue;
                } else if (peek == '\n') {//一条文法结束
                    item.id = id++;
                    //System.out.println("id:"+id);
                    item.right = grammarSymbol;
                    grammarSymbol = new ArrayList<Symbol>();//创建一个新的数组
                    grammar.add(item);
                    item = new Item();//创建新的对象
                    left = true;//下一个读取的是文法规则头部
                    continue;
                } else break;
            }

            //读取一个symbol
            if(peek >= 33 && peek <= 126 ){
                StringBuffer b = new StringBuffer();
                do {
                    b.append(peek);//System.out.println(peek);
                    readch();
                } while (peek >= 33 && peek <= 126);
                String s = b.toString();//System.out.println(s+line);

                symbol = (Symbol) symbols.get(s);//获取对应symbol类

                if (left) {//为文法规则头部
                    item.left.name = symbol.name;
                    item.left.id = symbol.id;
                    item.left.type = symbol.type;
                }
                else {//文法规则的后部
                    grammarSymbol.add(symbol);
                }
            }
        }while (ch != -1) ;
        in.close();
        return grammar;

    }

    //读取文法符号
    public void readSymbol()throws IOException {
        int id = 0;
        boolean line = false;//文档中第一行为非终结符，第二行为终结符
        in = new FileInputStream("./test/grammarSymbol.txt");
        do {
            while(true) {
                readch();
                if (peek == ' ' || peek == '\t' ) continue;
                else if (peek == '\n') line = true;
                else break;
            }
            if(peek >= 33 && peek <= 126 ) {
                StringBuffer b = new StringBuffer();
                do {
                    b.append(peek);//System.out.println(peek);
                    readch();
                } while (peek >= 33 && peek <= 126 );
                String s = b.toString();//System.out.println(s+line);
                Symbol symbol = new Symbol(id++, s, line);
                symbols.put(s, symbol);
            }
        }while(ch != -1);
        in.close();
    }
    void readch() throws IOException
    {
        ch = in.read();
        peek = (char)ch;
        if(peek == 250){//处理箭头的编码问题
            peek = 8594;
        }
    }
}
