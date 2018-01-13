package table;

import grammar.Grammar;
import grammar.Item;
import grammar.Symbol;
import itemSets.*;

import java.io.*;
import java.util.*;

public class Table {

    public String[][] Action = new String[200][200];
    public int[][] Goto = new int[200][200];
    public ArrayList<String> termin;
    public ArrayList<String> untermin;
    public ArrayList<Item> itemList;

    public void createTable() throws IOException {
        FileWriter  fw = new FileWriter("..\\table.txt");
        BufferedWriter out = new BufferedWriter(fw);
        Grammar grammar = new Grammar();
        grammar.readSymbol();
        itemList = grammar.readGrammar();

        ItemSets itemSets = new ItemSets();
        Follow follow = new Follow();

        itemSets.createItemSets();//求状态集
        follow.followFirst();//求follow集

        for(int t = 0; t < 200; t++){
            for(int m = 0; m < 200; m++)
                Action[t][m] = "n";
        }
        termin = new ArrayList<>();
        untermin = new ArrayList<>();
        Iterator iterator = grammar.symbols.keySet().iterator();
        Symbol symbol;
        String key;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            symbol = (Symbol) grammar.symbols.get(key);
            if (symbol.type == false) {
                if (!symbol.name.equals("s")) untermin.add(symbol.name);
            } else {
                if (!symbol.name.equals("empty")) termin.add(symbol.name);
            }
        }
        termin.add("$");

        Hashtable itemTable = new Hashtable();
        for (int i = 0; i < itemSets.setList.size(); i++) {
            Iterator iterator2 = itemSets.setList.get(i).itemSetTable.keySet().iterator();
            PointItem pointItem;
            String key2;
            while (iterator2.hasNext()) {
                key2 = (String) iterator2.next();
                pointItem = (PointItem) itemSets.setList.get(i).itemSetTable.get(key2);
                if (pointItem.pointLocation < itemList.get(pointItem.itemId).right.size())
                {
                    if (itemList.get(pointItem.itemId).right.get(pointItem.pointLocation).type == true
                            && !itemList.get(pointItem.itemId).right.get(pointItem.pointLocation).name.equals("empty")) {
                        String s = String.valueOf(pointItem.itemId);
                        s = s + String.valueOf(pointItem.pointLocation);
                        //System.out.println("remove " + s1);
                        PointItem pointItem1 = new PointItem();
                        pointItem1.pointLocation = pointItem.pointLocation;
                        pointItem1.itemId = pointItem.itemId;
                        itemTable.put(s, pointItem1);
                        //搜索
                        Iterator iterator3 = itemSets.setList.get(i).itemSetTable.keySet().iterator();
                        PointItem pointItem2;
                        String key3;
                        while (iterator3.hasNext()) {
                            key3 = (String) iterator3.next();
                            pointItem2 = (PointItem) itemSets.setList.get(i).itemSetTable.get(key3);
                            if (pointItem2.pointLocation < itemList.get(pointItem2.itemId).right.size()
                                    && itemList.get(pointItem.itemId).right.get(pointItem.pointLocation).id ==
                                    itemList.get(pointItem2.itemId).right.get(pointItem2.pointLocation).id) {
                                s = String.valueOf(pointItem2.itemId);
                                s = s + String.valueOf(pointItem2.pointLocation);
                                if (itemTable.get(s) == null) {
                                    pointItem1 = new PointItem();
                                    pointItem1.itemId = pointItem2.itemId;
                                    pointItem1.pointLocation = pointItem2.pointLocation;
                                    itemTable.put(s, pointItem1);
                                }
                            }
                        }

                        Hashtable table = setGoto(itemTable);
                        itemTable.clear();

                        for (int j = 0; j < itemSets.setList.size(); j++) {

                            if (tableEqual(table, itemSets.setList.get(j).itemSetTable)) {
                                String str = "s" + String.valueOf(j);
                                int r = termin.indexOf(itemList.get(pointItem.itemId).right.get(pointItem.pointLocation).name);
                                //System.out.println("num: "+i+" "+r+itemList.get(pointItem.itemId).right.get(pointItem.pointLocation).name);
                                //if(!Action[i][r].startsWith("r"))
                                    Action[i][r] = str;
                                //System.out.println("move: " + i + " " + r + " " + str);
                            }
                        }
                    }
                    if (itemList.get(pointItem.itemId).right.get(pointItem.pointLocation).type == false)//goto表
                    {
                        String s = String.valueOf(pointItem.itemId);
                        s = s + String.valueOf(pointItem.pointLocation);
                        //System.out.println("remove " + s1);
                        PointItem pointItem1 = new PointItem();
                        pointItem1.pointLocation = pointItem.pointLocation;
                        pointItem1.itemId = pointItem.itemId;
                        itemTable.put(s, pointItem1);
                        //搜索
                        Iterator iterator3 = itemSets.setList.get(i).itemSetTable.keySet().iterator();
                        PointItem pointItem2;
                        String key3;
                        while (iterator3.hasNext()) {
                            key3 = (String) iterator3.next();
                            pointItem2 = (PointItem) itemSets.setList.get(i).itemSetTable.get(key3);
                            if (pointItem2.pointLocation < itemList.get(pointItem2.itemId).right.size()
                                    && itemList.get(pointItem.itemId).right.get(pointItem.pointLocation).id ==
                                    itemList.get(pointItem2.itemId).right.get(pointItem2.pointLocation).id) {
                                s = String.valueOf(pointItem2.itemId);
                                s = s + String.valueOf(pointItem2.pointLocation);
                                if (itemTable.get(s) == null) {
                                    pointItem1 = new PointItem();
                                    pointItem1.itemId = pointItem2.itemId;
                                    pointItem1.pointLocation = pointItem2.pointLocation;
                                    itemTable.put(s, pointItem1);
                                }
                            }
                        }

                        Hashtable table = setGoto(itemTable);
                        itemTable.clear();

                        for (int j = 0; j < itemSets.setList.size(); j++) {

                            if (tableEqual(table, itemSets.setList.get(j).itemSetTable)) {
                                //String str = String.valueOf(j);
                                int r = untermin.indexOf(itemList.get(pointItem.itemId).right.get(pointItem.pointLocation).name);
                                //System.out.println("num: "+i+" "+r+itemList.get(pointItem.itemId).right.get(pointItem.pointLocation).name);
                                Goto[i][r] = j;
                                //System.out.println("move: " + i + " " + r + " " + str);
                            }
                        }
                    }
                }
                else
                {
                    //System.out.println(itemList.get(pointItem.itemId).left.name);
                    if(itemList.get(pointItem.itemId).left.name.equals("s"))
                    {
                        int r = termin.indexOf("$");
                        Action[i][r] = "acc";
                    }
                    ArrayList<String> symbolFollow = (ArrayList<String>)follow.follows.get(itemList.get(pointItem.itemId).left. name);
                    for(int k = 0; k < symbolFollow.size(); k++)
                    {
                        //System.out.println(symbolFollow.get(k));
                        int r = termin.indexOf(symbolFollow.get(k));
                        if(!Action[i][r].startsWith("s")||itemList.get(pointItem.itemId).right.size()>1&&itemList.get(pointItem.itemId).right.get(1).name.contains("*"))
                        {
                                Action[i][r] = "r"+String.valueOf(pointItem.itemId);
                        }

                        //System.out.println(i+" "+r+" "+Action[i][r]);
                    }

                }
            }



        }

        for(int i = 0;i < termin.size(); i++){
            System.out.print(termin.get(i)+"\t\t");
            out.write(termin.get(i)+"\t\t");
        }

        for (int i = 0;i < untermin.size();i++)
        {
            System.out.print(untermin.get(i)+"\t\t");
            out.write(untermin.get(i)+"\t\t");
        }
        System.out.println();
        out.newLine();
        for(int i = 0;i <itemSets.setList.size();i++)
        {
            for (int j =0;j<termin.size();j++) {
                if (Action[i][j] == null) Action[i][j] = "n";
                    System.out.print(Action[i][j] + "\t\t");
                    out.write(Action[i][j] + "\t\t");
            }
            for (int k = 0;k<untermin.size();k++){
                //if (Goto[i][k] == null) Goto[i][k] = -1;
                System.out.print(Goto[i][k]+"\t\t");
                out.write(Goto[i][k]+"\t\t");
            }

            System.out.println();
            out.newLine();
        }
        out.close();
        fw.close();


    }

    void closure(Hashtable itemSetTable) {
        Hashtable additionTable = new Hashtable();//存放新增的项
        do {//没有新的项加入时，退出循环
            Iterator iterator = itemSetTable.keySet().iterator();
            String key;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                PointItem pointItem = (PointItem) itemSetTable.get(key);
                //点的左端为非终结符
                if ( pointItem.pointLocation < itemList.get(pointItem.itemId).right.size()
                        && itemList.get(pointItem.itemId).right.get(pointItem.pointLocation).type == false) {

                    int leftId = itemList.get(pointItem.itemId).right.get(pointItem.pointLocation).id;
                    for (int i = 0; i < itemList.size(); i++) {//查找以该非终结符为头部的文法规则
                        if (itemList.get(i).left.id == leftId) {
                            String s = String.valueOf(itemList.get(i).id);
                            s = s + String.valueOf(0);
                            if (itemSetTable.get(s) == null) {
                                PointItem pointItemTmp = new PointItem();
                                pointItemTmp.pointLocation = 0;
                                pointItemTmp.itemId = itemList.get(i).id;
                                additionTable.put(s, pointItemTmp);
                            }
                        }
                    }
                }
            }

            if (!additionTable.isEmpty()) {//新增了项集
                iterator = additionTable.keySet().iterator();//合并itemSetTable
                String key1;
                while (iterator.hasNext()) {
                    key1 = (String) iterator.next();
                    PointItem pointItem = (PointItem) additionTable.get(key1);
                    //System.out.println(pointItem.itemId + " " + pointItem.pointLocation);
                    itemSetTable.put(key1, pointItem);
                }
                additionTable.clear();//清空additionTable
            } else break;//未新增项，退出
        } while (true);
    }

    Hashtable setGoto(Hashtable gotoTable) {
        Hashtable gotoTable2 = new Hashtable();
        Iterator iterator = gotoTable.keySet().iterator();
        String key;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            PointItem pointItem = (PointItem) gotoTable.get(key);
            int location = pointItem.pointLocation++;
            String s = String.valueOf(pointItem.itemId);
            s = s + String.valueOf(pointItem.pointLocation);
            //System.out.println("disremove "+pointItem.itemId + " " + pointItem.pointLocation);
            gotoTable2.put(s, pointItem);
            String s1 = String.valueOf(pointItem.itemId);
            s1 = s1 + String.valueOf(location);
            //System.out.println("remove " + s1);
            gotoTable2.remove(s1);
        }
        closure(gotoTable2);
        return gotoTable2;
    }

    boolean tableEqual(Hashtable gotoTable, Hashtable setTable) {
        Iterator iterator = gotoTable.keySet().iterator();
        String key;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            if (setTable.get(key) == null) {
                return false;
            }
        }
        return true;
    }
}
