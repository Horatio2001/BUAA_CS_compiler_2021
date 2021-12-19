package SymbolTable;

public class Var_symbol extends Symbol{
    private String type = "var";
    public Var_symbol(String name, int dim){
        super(name, dim);
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
