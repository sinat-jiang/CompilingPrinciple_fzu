package grammar;

public class Symbol {
    public  int id;
    public String name;
    public boolean type;//
    public  Symbol()
    {
    }
    public  Symbol(int id, String name, boolean type)
    {
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
