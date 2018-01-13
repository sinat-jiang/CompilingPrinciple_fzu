package parser;

import lexer.Lexer;
import lexer.Token;
import  table.*;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
    public ArrayList<Number> stateStack;
    public ArrayList<String> symbolStack;
    public ArrayList<String> codeStack;
    public ArrayList<String> code;
    public ArrayList<AddrPosition> gotoAddr;
    public ArrayList<Number> gotoPosition;
    public ArrayList<Number> beginAddr;
    public AddrPosition addrPosition;
    public Table table;
    int addr = 100;
    int addr1 = 100;
    int c = 1;
    public void parserGo()throws IOException
    {
        Lexer lexer = new Lexer();
        Token token;
        lexer.in = new FileInputStream("./test/programInput.txt");
        FileWriter fw = new FileWriter("./test/outPut2.txt");
        BufferedWriter out = new BufferedWriter(fw);

        int state;
        int thenPosition = 0;
        gotoAddr = new ArrayList<>();
        gotoPosition = new ArrayList<>();
        stateStack = new ArrayList<>();
        symbolStack = new ArrayList<>();
        codeStack = new ArrayList<>();
        code = new ArrayList<>();
        beginAddr = new ArrayList<>();
        stateStack.add(0);
        table = new Table();
        table.createTable();//生成预测分析表
        String a;

        token = readProgram(lexer);
        String st = token.symbol;
        if(!table.termin.contains(token.symbol)&&!table.untermin.contains(token.symbol)&&token.tag == 264)
        {
            a = "id";
        }

        else a = token.symbol;//System.out.println("a: "+a);
        if(token.tag == 270) a = "num";
        printStack(out);
        while(true) {

            state = stateStack.get(stateStack.size()-1).intValue();//栈顶
            String action = table.Action[state][table.termin.indexOf(a)];
            if(action.startsWith("s")){
                int id = 0;char sta;
                for(int i = 1; i < action.length(); i++ ){
                    sta = action.charAt(i);
                    id = id * 10 + (sta - '0');
                }
                System.out.println("\t移入： "+a);
                out.write("\t移入： " + a);//写入文件
                out.newLine();
                if(a.equals("while"))
                {
                    beginAddr.add(addr);
                    System.out.println("----------"+(addr));
                }
                if(a.equals("for"))
                {
                    beginAddr.add(addr+1);
                    System.out.println("----------"+(addr+1));
                }
                if(a.equals("downto")||a.equals("to"))
                {
                    code.add(codeStack.get(codeStack.size()-3)+"="+codeStack.get(codeStack.size()-1));
                    addr++;
                }
                if(st.equals("downto"))
                {
                    //--------------------------------------------------------for1
                    code.add("if("+codeStack.get(codeStack.size()-4)+">="+token.symbol+")goto ");
                    gotoPosition.add(addr++);
                    addrPosition = new AddrPosition();
                    addrPosition.position = addr -1;
                    addrPosition.addr = addr + 1;
                    gotoAddr.add(addrPosition);//gotoAddr.add(addr+1);
                    //------------------------------------------------for2
                    code.add("goto ");
                    gotoPosition.add(addr++);
                    addrPosition = new AddrPosition();
                    addrPosition.position = addr - 1;
                    gotoAddr.add(addrPosition);
                }
                if(st.equals("to"))
                {
                    //------------------------------------------------------for1
                    code.add("if("+codeStack.get(codeStack.size()-4)+"<="+token.symbol+")goto ");
                    gotoPosition.add(addr++);
                    addrPosition = new AddrPosition();
                    addrPosition.position = addr -1;
                    addrPosition.addr = addr + 1;
                    gotoAddr.add(addrPosition);//gotoAddr.add(addr+1);
                    //--------------------------------------------------------for2
                    code.add("goto ");
                    gotoPosition.add(addr++);
                    addrPosition = new AddrPosition();
                    addrPosition.position = addr - 1;
                    gotoAddr.add(addrPosition);
                }
                if(a.equals("else")){
                    //-------------------------------------------if3
                    code.add("goto ");
                    gotoPosition.add(addr++);
                    addrPosition = new AddrPosition();
                    addrPosition.position = addr - 1;
                    gotoAddr.add(addrPosition);
                }
                stateStack.add(id);
                symbolStack.add(a);
                //移入code栈
                if(a.equals("num")||a.equals("id")){
                    codeStack.add(token.symbol);
                }
                else {
                    codeStack.add(a);
                }
                printStack(out);
                st = token.symbol;
                token = readProgram(lexer);//读取下一个符号
                if (lexer.flag == -1) break;
                else if(!table.termin.contains(token.symbol)&&!table.untermin.contains(token.symbol)&&token.tag == 264) {
                    a = "id";
                    //System.out.println("ERROR");
                } else a = token.symbol;//System.out.println("a: "+a);
                if(token.tag == 270) a = "num";


            }else if(action.startsWith("r")) {//归约
                int id = 0;char sta;
                for(int i = 1; i < action.length(); i++ ){
                    sta = action.charAt(i);
                    id = id * 10 + (sta - '0');
                }
                System.out.println("\t规约: ");
                out.write("\t规约: ");
                printItem(table, id, out);
                //out.newLine();
                // -------------------------------------对code进行规约，并执行语义动作
                gen(id);
                for(int i = 0; i < table.itemList.get(id).right.size(); i++)//栈中弹出符号
                {
                    symbolStack.remove(symbolStack.size() - 1);
                   // System.out.println("Delete: "+symbolStack.get(symbolStack.size() - 1));
                    stateStack.remove(stateStack.size() - 1);
                    //codeStack.remove(codeStack.size() - 1);
                    //printStack();
                }
                //codeStack.add(table.itemList.get(id).left.name);
                stateStack.add(table.Goto[stateStack.get(stateStack.size()-1).intValue()][table.untermin.indexOf(table.itemList.get(id).left.name)]);
                symbolStack.add(table.itemList.get(id).left.name);
                if(symbolStack.get(symbolStack.size()-1).equals("stmt")&&symbolStack.get(symbolStack.size()-2).equals("do"))
                {//----------------------------------------do-addr
                    //gotoAddr.add(addr);
                    for(int i = gotoAddr.size() - 1;i >= 0; i--)
                    {
                        if(gotoAddr.get(i).addr == 0){
                            if(symbolStack.get(symbolStack.size()-3).equals("expr"))
                            {
                                gotoAddr.get(i).addr = addr+3;
                            }
                            else {
                                if(symbolStack.get(symbolStack.size()-3).equals("bool")) gotoAddr.get(i).addr = addr+1;
                                else gotoAddr.get(i).addr = addr;
                                //System.out.println("----------------------------------" + gotoAddr.get(i).position + " " + gotoAddr.get(i).addr);
                            }
                            break;
                        }
                    }
                }
                if(symbolStack.get(symbolStack.size()-1).equals("stmt")&&symbolStack.get(symbolStack.size()-2).equals("then"))
                 {
                    for(int i = gotoAddr.size() - 1;i >= 0; i--)
                    {
                        if(gotoAddr.get(i).addr == 0){
                            gotoAddr.get(i).addr = addr;
                            thenPosition = i;
                            //System.out.println("--------------then------"+gotoAddr.get(i).position+" "+gotoAddr.get(i).addr);
                            break;
                        }
                    }
                }
                if(symbolStack.get(symbolStack.size()-1).equals("stmt")&&symbolStack.get(symbolStack.size()-2).equals("else"))
                {//-----------------------------------------else-stmt
                    //System.out.println("else"+addr);
                    //gotoAddr.add(addr);
                    for(int i = gotoAddr.size() - 1;i >= 0; i--)
                    {
                        if(gotoAddr.get(i).addr == 0){
                            gotoAddr.get(i).addr = addr;
                            gotoAddr.get(thenPosition).addr+=1;
                            //System.out.println("--------------else------"+gotoAddr.get(i).position+" "+gotoAddr.get(i).addr);
                            break;
                        }
                    }
                }
                //System.out.println("Add: "+table.itemList.get(id).left.name);
                printStack(out);
            }else if(action.equals("acc")) {
                System.out.println("\t接受");
                out.write("\t接受");
                out.newLine();
                break;
            }
            else {
                out.newLine();
                System.out.println();
                System.out.println("ERROR: line"+lexer.line);
                out.write("ERROR: line"+lexer.line);
                out.newLine();
                out.newLine();break;
            }
        }
        //输出中间代码
        FileWriter fw2 = new FileWriter("./test/outPut3.txt");
        BufferedWriter out2 = new BufferedWriter(fw2);
        for(int i = 0; i < code.size(); i++ )
        {
            System.out.print(addr1++ + ": " + code.get(i));
            out2.write(String.valueOf(addr1-1) + ": " + code.get(i));
            for(int j = 0;j<gotoAddr.size();j++)
            {
                if(addr1-1 == gotoAddr.get(j).position)
                {
                    System.out.print(gotoAddr.get(j).addr);
                    out2.write(String.valueOf(gotoAddr.get(j).addr));
                }
            }

            System.out.println();
            out2.newLine();
        }
        out2.close();
        fw2.close();


        for(int i = 0; i < gotoPosition.size(); i++ )
        {
            System.out.print(gotoPosition.get(i)+" ");
        }
        System.out.println();
        for(int i = 0; i < gotoAddr.size(); i++ )
        {
            System.out.println(gotoAddr.get(i).position+" "+gotoAddr.get(i).addr);
        }
        System.out.println();
        out.close();
        fw.close();
    }


    public Token readProgram(Lexer lexer)throws IOException {

        for (; ; lexer.readch()) {
            if (lexer.peek == ' ' || lexer.peek == '\t' || lexer.peek == '\r') continue;
            else if (lexer.peek == '\n') lexer.line = lexer.line + 1;
            else break;
        }
        Token token = new Token(0, "0");
        //System.out.println("flag:"+lexer.flag);
        if (lexer.flag == -1) return token;//读取结束
        token = lexer.scan();
        return token;
    }

    //--------------------------------------------
    void gen(int id)
    {
        switch (id){
            case 0:CodeStackG(id);break;
            case 1:CodeStackG(id);break;
            case 2:CodeStackG(id);break;
            case 3:CodeStackG(id);break;
            case 4:CodeStackG(id);break;
            case 5:CodeStackG(id);break;
            case 6:code.add(codeStack.get(codeStack.size()-3) + "=" + codeStack.get(codeStack.size()-1));
                    addr++;
                   CodeStackG(id);break;
            case 7:CodeStackG(id);break;
            case 8:CodeStackG(id);break;
            case 9:CodeStackG(id);break;
            case 10:code.add("goto ");//---------------------------------while-begin
                    addrPosition = new AddrPosition();
                    addrPosition.addr = beginAddr.get(beginAddr.size()-1).intValue();
                    //gotoAddr.add(beginAddr.get(beginAddr.size()-1));
                    //System.out.println("----------while: "+beginAddr.get(beginAddr.size()-1));
                    beginAddr.remove(beginAddr.size()-1);
                    gotoPosition.add(addr++);
                    addrPosition.position = addr - 1;
                    gotoAddr.add(addrPosition);
                    CodeStackG(id);break;
            case 11:CodeStackG(id);break;
            case 12:CodeStackG(id);break;
            case 13:CodeStackG(id);break;
            case 14:code.add("t"+c+"=i+1");addr++;//------------------------------------------for-begin
                    code.add("i=t"+c);addr++;
                    code.add("goto ");
                    //gotoAddr.add(beginAddr.get(beginAddr.size()-1));
                    addrPosition = new AddrPosition();
                    addrPosition.addr = beginAddr.get(beginAddr.size()-1).intValue();
                    //System.out.println("----------for: "+beginAddr.get(beginAddr.size()-1));
                    beginAddr.remove(beginAddr.size()-1);
                    gotoPosition.add(addr++);
                    addrPosition.position = addr -1 ;
                    gotoAddr.add(addrPosition);
                    CodeStackG(id);break;
            case 15:code.add("t"+c+"=i-1");addr++;//---------------------------------------------for-begin
                    code.add("i=t"+c);addr++;
                    code.add("goto ");
                    addrPosition = new AddrPosition();
                    addrPosition.addr = beginAddr.get(beginAddr.size()-1).intValue();
                    //gotoAddr.add(beginAddr.get(beginAddr.size()-1));
                    //System.out.println("----------for: "+beginAddr.get(beginAddr.size()-1));
                    beginAddr.remove(beginAddr.size()-1);
                    gotoPosition.add(addr++);
                    addrPosition.position = addr -1;
                    gotoAddr.add(addrPosition);
                    CodeStackG(id);break;
            case 16:code.add("if("+codeStack.get(codeStack.size()-3) + codeStack.get(codeStack.size()-2)+codeStack.get(codeStack.size()-1)+")"+"goto ");
                    //-----------------------------------------if1
                    gotoPosition.add(addr++);
                    addrPosition = new AddrPosition();
                    addrPosition.position = addr -1;
                    addrPosition.addr = addr +1;
                    System.out.println("-----------------"+addrPosition.position+" "+addrPosition.addr);
                    gotoAddr.add(addrPosition);
                    //gotoAddr.add(addr+1);---------------------------------------------------if2
                    code.add("goto ");gotoPosition.add(addr++);
                    addrPosition = new AddrPosition();
                    addrPosition.position = addr -1;
                    System.out.println("-----------------"+addrPosition.position+" "+addrPosition.addr);
                    gotoAddr.add(addrPosition);
                    CodeStackG(id);break;
            case 17:code.add("if("+codeStack.get(codeStack.size()-3) + codeStack.get(codeStack.size()-2)+codeStack.get(codeStack.size()-1)+")"+"goto ");
                    //-----------------------------------------------if1
                    gotoPosition.add(addr++);
                    addrPosition = new AddrPosition();
                    addrPosition.position = addr -1;
                    addrPosition.addr = addr+1;
                    System.out.println("-----------------"+addrPosition.position+" "+addrPosition.addr);
                    gotoAddr.add(addrPosition);
                    //gotoAddr.add(addr+1);-------------------------------------------------------if2
                    code.add("goto ");gotoPosition.add(addr++);
                    addrPosition = new AddrPosition();
                    addrPosition.position = addr -1;
                System.out.println("-----------------"+addrPosition.position+" "+addrPosition.addr);
                    gotoAddr.add(addrPosition);
                    CodeStackG(id);break;
            case 18:code.add("t"+c+"="+codeStack.get(codeStack.size()-3)+codeStack.get(codeStack.size()-2)+codeStack.get(codeStack.size()-1));
                    addr++;
                    CodeStackG2(id);break;
            case 19:code.add("t"+c+"="+codeStack.get(codeStack.size()-3)+codeStack.get(codeStack.size()-2)+codeStack.get(codeStack.size()-1));
                    addr++;
                    CodeStackG2(id);break;
            case 20:code.add("t"+c+"="+codeStack.get(codeStack.size()-3)+codeStack.get(codeStack.size()-2)+codeStack.get(codeStack.size()-1));
                    addr++;
                    CodeStackG2(id);break;
            case 21:code.add("t"+c+"="+codeStack.get(codeStack.size()-3)+codeStack.get(codeStack.size()-2)+codeStack.get(codeStack.size()-1));
                    addr++;
                    CodeStackG2(id);break;
            case 22:code.add("t"+c+"="+codeStack.get(codeStack.size()-3)+codeStack.get(codeStack.size()-2)+codeStack.get(codeStack.size()-1));
                    addr++;
                    CodeStackG2(id);break;
            case 23:break;
            case 24:break;
            case 25:break;
            case 26:String s = codeStack.get(codeStack.size()-2);
                    for(int i = 0; i < table.itemList.get(id).right.size(); i++)//栈中弹出符号
                        {
                            codeStack.remove(codeStack.size() - 1);
                            //printStack();
                        }
                    codeStack.add(s);break;
            default:break;
        }
    }

    void CodeStackG(int id){
        for(int i = 0; i < table.itemList.get(id).right.size(); i++)//栈中弹出符号
        {
            codeStack.remove(codeStack.size() - 1);
            //printStack();
        }
        codeStack.add(table.itemList.get(id).left.name);
    }

    void CodeStackG2(int id){
        for(int i = 0; i < table.itemList.get(id).right.size(); i++)//栈中弹出符号
        {
            codeStack.remove(codeStack.size() - 1);
            //printStack();
        }
        codeStack.add("t"+String.valueOf(c));c++;
    }
    void printStack(BufferedWriter out)throws IOException
    {
       /* for(int i = 0;i < stateStack.size();i++){
            System.out.print(stateStack.get(i).intValue()+" ");
            out.write(stateStack.get(i).intValue()+" ");
        }
        System.out.print("\t");
        out.write("\t");*/
        for(int j = 0; j<symbolStack.size();j++)
        {
            System.out.print(symbolStack.get(j)+" ");
            out.write(symbolStack.get(j)+" ");
        }
        //System.out.println();
    }

    void printItem(Table table, int id, BufferedWriter out)throws IOException
    {
       // System.out.print(table.itemList.get(id).left.name + " ");
        System.out.print("→");
        out.write(table.itemList.get(id).left.name + " ");
        out.write("→");
        for(int i = 0; i < table.itemList.get(id).right.size(); i++ )
        {
            out.write(" " + table.itemList.get(id).right.get(i).name);
            System.out.print(" " + table.itemList.get(id).right.get(i).name);
        }
        out.newLine();
        System.out.println();
    }
}
