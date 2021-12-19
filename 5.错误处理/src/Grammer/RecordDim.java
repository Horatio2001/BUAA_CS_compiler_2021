package Grammer;

import java.util.ArrayList;

public class RecordDim {
    private ArrayList<String> res;
    private int retDim;

    public RecordDim(ArrayList<String> res, int retDim){
        this.res = res;
        this.retDim = retDim;
    }
    public ArrayList<String> getRes() {
        return res;
    }


    public void setRes(ArrayList<String> res) {
        this.res = res;
    }


    public int getRetDim() {
        return retDim;
    }

    public void setRetDim(int retDim) {
        this.retDim = retDim;
    }
}
