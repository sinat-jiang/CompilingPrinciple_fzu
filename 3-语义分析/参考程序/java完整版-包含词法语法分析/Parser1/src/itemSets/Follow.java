package itemSets;

import grammar.*;
import java.io.IOException;
import java.util.*;

public class Follow {
    Hashtable firsts = new Hashtable();
    public Hashtable follows = new Hashtable();
    ArrayList<String> first;
    ArrayList<String> follow;

    public void followFirst()throws IOException
    {
        Grammar grammar = new Grammar();
        grammar.readSymbol();
        ArrayList<Item> itemList = grammar.readGrammar();//读取文法规则集
        Iterator iterator =grammar.symbols.keySet().iterator();
        Symbol symbol ;
        String key;
        while(iterator.hasNext()) {
            key = (String) iterator.next();
            symbol = (Symbol) grammar.symbols.get(key);
            if(symbol.type == false) {//添加非终结符
                first = new ArrayList<String>();
                firsts.put(symbol.name, first);
                follow = new ArrayList<String>();
                follows.put(symbol.name, follow);
            }
            else {
                first = new ArrayList<String>();
                first.add(symbol.name);
                firsts.put(symbol.name, first);
            }
        }
        countFirst(grammar, itemList);//求first集合
        countFollow(grammar, itemList);//求follow集合

        System.out.println("FIRST:");
        Iterator iterator2 =firsts.keySet().iterator();
        String key1;
        while(iterator2.hasNext()) {
            key1 = (String) iterator2.next();
            ArrayList<String> link = (ArrayList<String>) firsts.get(key1);
            System.out.print(key1+": ");
            for (int l = 0; l < link.size(); l++) {
                System.out.print(link.get(l)+" ");

            }System.out.println();
        }

        System.out.println("FOLLOW:");
        Iterator iterator3 =follows.keySet().iterator();
        String key3;
        while(iterator3.hasNext()) {
            key3 = (String) iterator3.next();
            ArrayList<String> link1 = (ArrayList<String>) follows.get(key3);
            System.out.print(key3+": ");
            for (int l = 0; l < link1.size(); l++) {
                System.out.print(link1.get(l)+" ");

            }System.out.println();
        }

    }
    void countFirst(Grammar grammar, ArrayList<Item> itemList)
    {
        Symbol symbol88;
        //symbol88 = (Symbol)grammar.symbols.get("e");
        //System.out.println(symbol88.name+"hello"+itemList.get(5).left.name);
        for(int i = 0; i < itemList.size(); i++ ){

            if(itemList.get(i).right.get(0).name.equals("empty")){//规则推导出空串，添加空串
                first = (ArrayList<String>)firsts.get(itemList.get(i).left.name);
                first.add("empty");
            }
        }

        for(int i = 0; i < itemList.size(); i++){//规则右端第一个字符串为终结符
            if(itemList.get(i).right.get(0).type == true && !itemList.get(i).right.get(0).name.equals("empty")){
                first = (ArrayList<String>)firsts.get(itemList.get(i).left.name);
                if(!first.contains(itemList.get(i).right.get(0).name))
                    first.add(itemList.get(i).right.get(0).name);
            }
        }
        for(int k = 0; k < 1000; k++) {

            for (int i = 0; i < itemList.size(); i++) {
                int j = 0;
                if (itemList.get(i).right.get(j).type == false) {
                    ArrayList<String> firstTmp = new ArrayList<String>();
                    first = (ArrayList<String>) firsts.get(itemList.get(i).left.name);
                    firstTmp = (ArrayList<String>) firsts.get(itemList.get(i).right.get(j).name);
                    addFirst(first, firstTmp);
                    while (true) {
                        if (firstTmp.contains("empty")) {
                            j++;
                            if (itemList.get(i).right.size() == j) {
                                if(!first.contains("empty"))
                                    first.add("empty");
                                break;
                            } else {
                                firstTmp = (ArrayList<String>) firsts.get(itemList.get(i).right.get(j).name);
                                addFirst(first, firstTmp);
                            }
                        } else break;
                    }
                }
            }
        }
    }
    void countFollow(Grammar grammar, ArrayList<Item> itemList)
    {

        follow = (ArrayList<String>)follows.get("S");
        follow.add("$");//开始符号的follow加入$
        ArrayList<String> followTmp = new ArrayList<String>();
        ArrayList<String> firstTmp = new ArrayList<String>();

        for(int k = 0; k < 1000; k++)
        {
            for (int i = 0; i < itemList.size(); i++) {
                for (int j = 0; j < itemList.get(i).right.size(); j++) {
                    if (j + 1 < itemList.get(i).right.size()) {
                        if (itemList.get(i).right.get(j).type == false) {
                            String empty = "empty";
                            int m = j + 1;
                            do {
                                follow = (ArrayList<String>) follows.get(itemList.get(i).right.get(j).name);
                                firstTmp = (ArrayList<String>) firsts.get(itemList.get(i).right.get(m).name);
                                addFollow(follow, firstTmp);
                                m ++;
                            }while(((ArrayList<String>)firsts.get(itemList.get(i).right.get(m - 1).name)).contains(empty)
                                    && m < itemList.get(i).right.size());
                        }
                    }

                }
            }

            for (int i = 0; i < itemList.size(); i++) {
                for (int j = 0; j < itemList.get(i).right.size(); j++) {
                    if (itemList.get(i).right.get(j).type == false) {
                        if (j + 1 == itemList.get(i).right.size()) {
                            follow = (ArrayList<String>) follows.get(itemList.get(i).right.get(j).name);
                            followTmp = (ArrayList<String>) follows.get(itemList.get(i).left.name);

                            addFollow(follow, followTmp);
                        } else {
                            first = (ArrayList<String>) firsts.get(itemList.get(i).right.get(j + 1).name);
                            if (first.contains("empty")) {
                                followTmp = (ArrayList<String>) follows.get(itemList.get(i).left.name);
                                follow = (ArrayList<String>) follows.get(itemList.get(i).right.get(j));
                                addFollow(follow, followTmp);
                            }
                        }
                    }
                }
            }
        }

    }

    void addFirst(ArrayList<String> first, ArrayList<String> firstTmp)
    {
        for(int i = 0;i < firstTmp.size(); i++)
        {
            if (!firstTmp.get(i).equals("empty")){
                if(!first.contains(firstTmp.get(i))){
                    first.add(firstTmp.get(i));
                }
            }
        }
    }

    void addFollow(ArrayList<String> follow, ArrayList<String> tmp)
    {
            for (int i = 0; i < tmp.size(); i++) {
                if (!tmp.get(i).equals("empty")) {
                    if (!follow.contains(tmp.get(i))) {
                        follow.add(tmp.get(i));
                    }
                }
            }
    }

}

