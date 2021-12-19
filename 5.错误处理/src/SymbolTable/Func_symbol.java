package SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class Func_symbol extends Symbol{
    private String type = "func";
    private List<Param_symbol> params = new ArrayList<>();
    private int startCode;

    public Func_symbol(String name, int dim){
        super(name,dim);

    }


    public void addParam(Param_symbol param_symbol){
        this.params.add(param_symbol);
    }
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public void show(){
        for (Symbol param:params){
            System.out.println("param: "+param.getName());
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStartCode() {
        return startCode;
    }

    public List<Param_symbol> getParams() {
        return params;
    }

    public void setParams(List<Param_symbol> params) {
        this.params = params;
    }

    public void setStartCode(int startCode) {
        this.startCode = startCode;
    }
}
