package Grammer;

import java.util.ArrayList;

public class Record {
    private ArrayList<String> res;
    private int retValue;
    private int retDim;

    public Record(ArrayList<String> res, int retValue, int retDim){
        this.res = res;
        this.retValue =retValue;
        this.retDim = retDim;
    }

    public Record(ArrayList<String> res){
        this.res = res;
    }

    public ArrayList<String> getRes() {
        return res;
    }

    public int getRetValue() {
        return retValue;
    }

    public void setRes(ArrayList<String> res) {
        this.res = res;
    }

    public void setRetValue(int retValue) {
        this.retValue = retValue;
    }

    public int getRetDim() {
        return retDim;
    }

    public void setRetDim(int retDim) {
        this.retDim = retDim;
    }
}
