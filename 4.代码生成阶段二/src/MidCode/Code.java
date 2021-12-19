package MidCode;

public class Code{
    private String name;
    private int level;
    private int addr;
    private String print;
    private Label label;

    private int type = 0;

    //1，只有name
    //2，有name，addr
    //3，有name，level，addr
    //4，prf，有string
    //5，有name，label

    public Code(String name, int level, int addr){
        this.name = name;
        this.level = level;
        this.addr = addr;
        type = 1;
    }

    public Code(String name, int addr){
        this.name = name;
        this.addr = addr;
        type = 2;
    }

    public Code(String name){
        this.name = name;
        type = 3;
    }

    public Code(String name, String print){
        this.name = name;
        this.print = print;
        type = 4;
    }

    public Code(String name, Label label){
        this.name = name;
        this.label = label;
        type = 5;
    }

    public void show(){
        if(type == 1){
            System.out.println(name + "  " +level + "  " + addr);
        }else if(type == 2){
            System.out.println(name + "  "+ addr);
        }else if(type == 3){
            System.out.println(name);
        }else if(type == 4){
            System.out.println(name + "  " + print);
        }else if(type == 5){
            System.out.println(name + "  " + label.getPoint());
        }
    }

    public String getName(){
        return name;
    }

    public Label getLabel(){
        return label;
    }

    public int getLevel(){
        return level;
    }

    public int getAddr(){
        return addr;
    }

    public String getPrint(){
        return print;
    }


}
