package SymbolTable;

public class Param_symbol extends Symbol{
    private String type = "param";
    public Param_symbol(String name,int dim){
        super(name,dim);
    }
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
