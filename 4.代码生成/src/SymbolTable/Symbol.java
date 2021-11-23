package SymbolTable;

public class Symbol {
    private String name;
    private int dim;//0,1,2,-1
    private int dim1 = 0;
    private int dim2 = 0;
    private int address = 0;
    private boolean isConst = false;
    private boolean isGlobal = false;

    public Symbol(String name,int dim){
        this.name = name;
        this.dim = dim;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Symbol)) {
            return false;
        }
        Symbol sym_obj = (Symbol) obj;
        return sym_obj.getName().equals(this.name);
    }

    public int getAddress() {
        return address;
    }

    public void show_value(){}

    public void setAddress(int address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public int getDim1() {
        return dim1;
    }

    public void setDim1(int dim1) {
        this.dim1 = dim1;
    }

    public int getDim2() {
        return dim2;
    }

    public void setDim2(int dim2) {
        this.dim2 = dim2;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }
}
