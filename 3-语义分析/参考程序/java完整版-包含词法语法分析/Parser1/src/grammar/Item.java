package grammar;

import java.util.*;
public class Item {
    public int id;
    public Symbol left;
    public ArrayList<Symbol> right ;//= new ArrayList<Symbol>();
    public Item(int id, Symbol left, ArrayList<Symbol> right){
        this.id = id;
        this.left = left;
        this.right = right;
    }
    public Item(){
        left = new Symbol();
    }
}
