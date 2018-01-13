//package main;
import grammar.*;
import itemSets.*;
import lexer.Lexer;
import parser.Parser;
import table.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Main{

    public static void main(String[] args)throws IOException {
       /* System.out.println("symbol");
        Grammar grammar = new Grammar();

        grammar.readSymbol();
        Iterator iterator =grammar.symbols.keySet().iterator();
        Symbol symbol ;

        String key1;
        while(iterator.hasNext()) {
            key1 = (String)iterator.next();
            symbol = (Symbol)grammar.symbols.get(key1);
            System.out.print(key1 + "\t\t");
            System.out.println(symbol.id);
        }
        ArrayList<Item> grammarList = grammar.readGrammar();
        for(int i=0; i<grammarList.size(); i++) {
            System.out.print(grammarList.get(i).id +" "+ grammarList.get(i).left.type+" →");
            for(int j=0;j<grammarList.get(i).right.size();j++)
            {
                System.out.print(" "+grammarList.get(i).right.get(j).name);
            }
            System.out.println();
        }*/

        //ItemSets itemSets = new ItemSets();
        //itemSets.createItemSets();
        //Table table = new Table();
        //table.createTable();
        Parser parser = new Parser();
        parser.parserGo();

    }

}