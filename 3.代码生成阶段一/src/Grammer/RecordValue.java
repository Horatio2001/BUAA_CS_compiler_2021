package Grammer;

import java.util.List;

public class RecordValue {
    private List<String> res;
    private List<Integer> values;

    public RecordValue(List<String> res, List<Integer> values){
        this.res = res;
        this.values = values;
    }
    public void setRes(List<String> res) {
        this.res = res;
    }

    public void setValues(List<Integer> values) {
        this.values = values;
    }

    public List<Integer> getValues() {
        return values;
    }

    public List<String> getRes() {
        return res;
    }
}
