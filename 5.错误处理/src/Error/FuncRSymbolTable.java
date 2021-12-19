package Error;

import SymbolTable.Symbol;

import java.util.ArrayList;

public class FuncRSymbolTable {
    private ArrayList<String> res;
    private int returnDim;

    public FuncRSymbolTable(ArrayList<String> res,int returnDim){
        this.res = res;
        this.returnDim = returnDim;
    }

    public void setRes(ArrayList<String> res) {
        this.res = res;
    }

    public ArrayList<String> getRes() {
        return res;
    }

    public int getReturnDim() {
        return returnDim;
    }

    public void setReturnDim(int returnDim) {
        this.returnDim = returnDim;
    }
}
