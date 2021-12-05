package Error;

public class ErrorTable {
    private String type;
    private int lineNum;

    public ErrorTable(int lineNum,String type){
        this.type = type;
        this.lineNum = lineNum;
    }

    public String turnToFileFormat() {return this.lineNum + " " + this.type;}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }
}
