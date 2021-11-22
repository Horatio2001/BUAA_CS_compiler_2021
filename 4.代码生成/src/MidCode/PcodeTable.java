package MidCode;

public class PcodeTable {
    private String actions;
    private int op1;
    private int op2;

    public void code(String name, int op1, int op2){
        this.actions = name;
        this.op1 = op1;
        this.op2 = op2;
    }

    public void code(String name, int op1){
        this.actions = name;
        this.op1 = op1;
    }

    public void code(String name){
        this.actions = name;
    }

    public int getOp1() {
        return op1;
    }

    public void setOp1(int op1) {
        this.op1 = op1;
    }

    public int getOp2() {
        return op2;
    }

    public void setOp2(int op2) {
        this.op2 = op2;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public String getActions() {
        return this.actions;
    }


}
