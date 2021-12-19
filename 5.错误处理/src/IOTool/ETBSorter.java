package IOTool;

import java.util.ArrayList;
import Error.ErrorTable;

public class ETBSorter {
    public static void ETBSort(ArrayList<ErrorTable> ETB){
        for (int i=0;i<ETB.size()-1;i++){
            for (int j=0;j<ETB.size()-1-i;j++){
                if (ETB.get(j).getLineNum()>ETB.get(j+1).getLineNum()){
                    ErrorTable tmp = new ErrorTable(0,"");
                    tmp.changeTable(ETB.get(j));
                    ETB.get(j).changeTable(ETB.get(j+1));
                    ETB.get(j+1).changeTable(tmp);
                }
            }
        }
    }
}
