package SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class Const_symbol extends Symbol{
    private String type = "const";
    private List<Integer> values = new ArrayList<>();

    public Const_symbol(String name, int dim){
        super(name, dim);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public void show_value(){
        System.out.println(values);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setValues(List<Integer> values) {
        this.values = values;
    }

    public List<Integer> getValues() {
        return values;
    }
}
