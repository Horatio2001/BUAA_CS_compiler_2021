package SymbolTable;

import java.util.ArrayList;

public class Block {
    private String type;//global,int,void,block
    private ArrayList<Block> CBlock;
    private Block FBlock;
    private ArrayList<Symbol> SymbolTable;
    private int level;
    private boolean returnTk;

    public Block(String type, Block FBlock, int level){
        this.type = type;
        this.FBlock = FBlock;
        this.level = level;
        CBlock = new ArrayList<>();
        SymbolTable = new ArrayList<>();
    }

    public void addBlock(Block block){
        this.CBlock.add(block);
    }

    public boolean addSymbol(Symbol symbol) {
        if(!SymbolTable.contains(symbol)){
            SymbolTable.add(symbol);
        }else {
            return false;
        }
        return true;
    }

    public boolean containSymbol(Symbol symbol) {
        return SymbolTable.contains(symbol);
    }



    public Symbol search(String str){
        Block searchBlock = this;
        Symbol target = null;
        int flag = 0;
        while(searchBlock != null && flag == 0 ){
            for (Symbol symbol : searchBlock.getSymbolTable()) {
                if (symbol.getName().equals(str)) {
                    flag = 1;
                    target = symbol;
                    break;
                }
            }
            searchBlock = searchBlock.getFBlock();
        }
        return target;
    }

    public ArrayList<Block> getCBlock() {
        return CBlock;
    }

    public Block getFBlock() {
        return FBlock;
    }

    public ArrayList<Symbol> getSymbolTable() {
        return SymbolTable;
    }

    public boolean isReturnTk() {
        return returnTk;
    }

    public int getLevel() {
        return level;
    }

    public String getType() {
        return type;
    }

    public void setBlockItems(ArrayList<Symbol> blockItems) {
        this.SymbolTable = blockItems;
    }

    public void setCBlock(ArrayList<Block> CBlock) {
        this.CBlock = CBlock;
    }

    public void setFBlock(Block FBlock) {
        this.FBlock = FBlock;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setReturnTk(boolean returnTk) {
        this.returnTk = returnTk;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void show() {
        System.out.println("------------------" + type + "----------------------");
        System.out.println("level: " + level);
        for (Symbol symbol : SymbolTable) {
            System.out.println(symbol.getName() + "   dim: " + symbol.getDim() + "  type: " + symbol.getName()
                    +"  address: " + symbol.getAddress() + "  global: " + symbol.isGlobal());
            if (symbol.isConst()) {
                symbol.show_value();
            }else if(symbol.getName().equals("func")){
                ((Func_symbol)symbol).show();
            }
        }
        for (Block cblock : CBlock) {
            cblock.show();
        }
    }

}
