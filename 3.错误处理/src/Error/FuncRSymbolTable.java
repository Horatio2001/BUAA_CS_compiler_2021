package Error;

import SymbolTable.Symbol;

import java.util.ArrayList;

public class FuncRSymbolTable {
    private ArrayList<String> res;
    private Symbol symbol;

    public FuncRSymbolTable(ArrayList<String> res,Symbol symbol){
        this.res = res;
        this.symbol = symbol;
    }

    public void setRes(ArrayList<String> res) {
        this.res = res;
    }

    public ArrayList<String> getRes() {
        return res;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }
}
