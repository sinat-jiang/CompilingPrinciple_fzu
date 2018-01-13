package itemSets;

import grammar.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class ItemSets {
    public ItemSet set = new ItemSet();
    public PointItem pointItem;
    public ArrayList<ItemSet> setList = new ArrayList<ItemSet>();
    ArrayList<Item> itemList = new ArrayList<Item>();

    public void createItemSets() throws IOException {
        int id = 1;
        Grammar grammar = new Grammar();
        grammar.readSymbol();
        itemList = grammar.readGrammar();//读取文法规则集
        ItemSet itemSet = new ItemSet();
        PointItem pointItem = new PointItem(0, 0);
        String s = String.valueOf(0);
        s = s + String.valueOf(0);
        itemSet.id = 0;
        itemSet.itemSetTable.put(s, pointItem);
        closure(itemSet.itemSetTable);//求初始项集
        setList.add(itemSet);
        //求项集族
            for(int i = 0; i < setList.size(); i++){//每个项集
                Iterator iterator = grammar.symbols.keySet().iterator();
                String key;
                while (iterator.hasNext()) {//每个文法符号
                    key = (String) iterator.next();
                    Symbol symbol = (Symbol) grammar.symbols.get(key);
                    Hashtable gotoTable = new Hashtable();

                    Iterator iterator1 = setList.get(i).itemSetTable.keySet().iterator();
                    String key1;
                    while (iterator1.hasNext()){//查找对应文法符号的项
                        key1 = (String) iterator1.next();
                        PointItem pointItem1 = (PointItem) setList.get(i).itemSetTable.get(key1);
                        if(pointItem1.pointLocation < itemList.get(pointItem1.itemId).right.size()
                                &&itemList.get(pointItem1.itemId).right.get(pointItem1.pointLocation).id == symbol.id){
                            //找到一个对应文法符号的项
                            //System.out.println("name " + symbol.name);
                            PointItem pointItem2 = new PointItem();
                            pointItem2.pointLocation = pointItem1.pointLocation;
                            pointItem2.itemId = pointItem1.itemId;

                            s =String.valueOf(pointItem1.itemId);
                            s = s +String.valueOf(pointItem1.pointLocation);
                            gotoTable.put(s, pointItem2);
                        }
                    }
                    boolean newing = true;
                    if(!gotoTable.isEmpty()){

                        gotoTable = setGoto(gotoTable);
                        for(int j = 0; j < setList.size(); j++){
                            if(tableEqual(gotoTable, setList.get(j).itemSetTable) ){//项集存在
                                newing = false;
                                break;
                            }
                        }
                        if(newing){
                            ItemSet itemSet1 = new ItemSet();
                            itemSet1.id = id++;
                            //System.out.println(id-1);
                            Iterator iterator5 = gotoTable.keySet().iterator();
                            String key5;
                            while (iterator5.hasNext()) {
                                key5 = (String) iterator5.next();
                                PointItem pointItem7 = (PointItem) gotoTable.get(key5);
                                //System.out.println(pointItem7.itemId+" "+pointItem7.pointLocation);
                            }
                            itemSet1.itemSetTable = gotoTable;
                            setList.add(itemSet1);
                        }
                        else newing = true;
                    }
                }
            }

        FileWriter fw = new FileWriter("E:\\itemSets.txt");
        BufferedWriter out = new BufferedWriter(fw);
        for(int i=0; i<setList.size();i++) {
            Iterator iterator = setList.get(i).itemSetTable.keySet().iterator();
            PointItem pointItem1;
            String key1;
            out.write("I" + i);
            out.newLine();
            while (iterator.hasNext()) {
                key1 = (String) iterator.next();
                pointItem1 = (PointItem) setList.get(i).itemSetTable.get(key1);
                out.write(itemList.get(pointItem1.itemId).left.name + " ");
                out.write("→ ");
                for (int j = 0; j < itemList.get(pointItem1.itemId).right.size(); j++) {
                    if (j == pointItem1.pointLocation) out.write("? ");
                    out.write(itemList.get(pointItem1.itemId).right.get(j).name + " ");
                }
                if(pointItem1.pointLocation == itemList.get(pointItem1.itemId).right.size())
                {
                    out.write("?");
                }
                out.newLine();
                out.newLine();
            }
        }

        out.close();
        fw.close();
         /*   PointItem pointItem9;
            pointItem9 = (PointItem)setList.get(37).itemSetTable.get("230");
        System.out.println(pointItem9.itemId+" "+pointItem9.pointLocation);*/
    }

    //求项集的闭包
    void closure(Hashtable itemSetTable) {
        Hashtable additionTable = new Hashtable();//存放新增的项
        do {//没有新的项加入时，退出循环
            Iterator iterator = itemSetTable.keySet().iterator();
            String key;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                pointItem = (PointItem) itemSetTable.get(key);
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
                    pointItem = (PointItem) additionTable.get(key1);
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
            pointItem = (PointItem) gotoTable.get(key);
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
