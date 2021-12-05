package IOTool;

import MidCode.Code;

import java.util.ArrayList;
import java.util.Scanner;

public class Interpreter {
    private int[] dstack = new int[100000];
    private int BAddr = 0;
    private int at = 0;
    private int sp = -1;
    private ArrayList<Code> codelist = new ArrayList<>();

    public Interpreter(ArrayList<Code> codelist){
        this.codelist = codelist;
    }

    public ArrayList<String> interpret(){
        int addr;
        ArrayList<String> res = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        while (at < codelist.size()) {
            Code curCode = codelist.get(at);
            switch (curCode.getName()) {
                case "INT":
                    sp += curCode.getAddr();
                    at++;
                    break;
                case "DOWN":
                    sp -= curCode.getAddr();
                    at++;
                    break;
                case "LOD":
                    sp++;
                    if (curCode.getLevel() == 0) {
                        addr = BAddr + curCode.getAddr();
                    } else {
                        addr = curCode.getAddr();
                    }
                    dstack[sp] = dstack[addr];
                    at++;
                    break;
                case "LODS":
//                    System.out.println("当前指令:"+at+"当前栈顶"+sp);
//                    System.out.println("stack[sp]: "+dstack[sp]);
//                    System.out.println("stack[stack[sp]]: "+dstack[dstack[sp]]);
                    dstack[sp] = dstack[dstack[sp]];
                    //System.out.println("value: " + dstack[dstack[sp]] + "  put into sp: " + sp);
                    at++;
                    break;
                case "LDA":
                    sp++;
                    if (curCode.getLevel() == 0) {
                        addr = BAddr + curCode.getAddr();
                    } else {
                        addr = curCode.getAddr();
                    }
                    dstack[sp] = addr;
                    at++;
                    break;
                case "LDC":
                    sp++;
                    dstack[sp] = curCode.getAddr();
                    at++;
                    break;
                case "STOS":
                    sp--;
                    dstack[dstack[sp]] = dstack[sp + 1];
                    sp--;
                    at++;
                    break;
                case "ADD":
                    sp--;
                    dstack[sp] = dstack[sp] + dstack[sp + 1];
                    at++;
                    break;
                case "SUB":
                    sp--;
                    dstack[sp] = dstack[sp] - dstack[sp + 1];
                    at++;
                    break;
                case "MUL":
                    sp--;
                    dstack[sp] = dstack[sp] * dstack[sp + 1];
                    at++;
                    break;
                case "DIV":
                    sp--;
                    dstack[sp] = dstack[sp] / dstack[sp + 1];
                    at++;
                    break;
                case "MOD":
                    sp--;
                    dstack[sp] = dstack[sp] % dstack[sp + 1];
                    at++;
                    break;
                case "MINU":
                    dstack[sp] = -dstack[sp];
                    at++;
                    break;
                case "GET":
                    int value = scanner.nextInt();
                    //System.out.println(sp+" " + at);
                    sp++;
                    dstack[sp] = value;
                    at++;
                    break;
                case "PRF":
                    String s = curCode.getPrint();
                    s = s.replace("\"", "");
                    int ci = 0;
                    for (int i = 0; i < s.length(); i++) {
                        int t = s.indexOf("%d", i);
                        if (i == t) {
                            ci++;
                        }
                    }
                    sp = sp - ci;
                    String num;
                    for (int i = 0; i < ci; i++) {
                        num = String.valueOf(dstack[sp + i + 1]);
                        s = s.replaceFirst("%d", num);
                    }
                    at++;
                    s = s.replace("\\n", "\n");
                    res.add(s);
                    break;
                case "JTM":
                    BAddr = sp + 1;
                    at = curCode.getLabel().getPoint();
                    break;
                case "CAL":
                    dstack[sp + 1] = 0;
                    dstack[sp + 2] = BAddr;
                    dstack[sp + 3] = at + 1;
                    BAddr = sp + 1;
                    sp = sp + 3;
                    at = curCode.getAddr();
                    break;
                case "RET":
                    at = dstack[BAddr + 2];
                    sp = BAddr;
                    BAddr = dstack[BAddr + 1];
                    break;
                case "RET_TO_END":
                    at = codelist.size();
                    break;
                case "INT_L":
                    sp += curCode.getLabel().getPoint();
                    at++;
                    break;
                case "BGT"://>
                    sp--;
                    dstack[sp] = (dstack[sp] > dstack[sp + 1])? 1 : 0;
                    at++;
                    break;
                case "BGE": //>=
                    sp--;
                    dstack[sp] = (dstack[sp] >= dstack[sp + 1])? 1 : 0;
                    at++;
                    break;
                case "BLT"://<
                    sp--;
                    dstack[sp] = (dstack[sp] < dstack[sp + 1])? 1 : 0;
                    at++;
                    break;
                case "BLE"://<=
                    sp--;
                    dstack[sp] = (dstack[sp] <= dstack[sp + 1])? 1 : 0;
                    at++;
                    break;
                case "BEQ"://==
                    sp--;
                    dstack[sp] = (dstack[sp] == dstack[sp + 1])? 1 : 0;
                    at++;
                    break;
                case "BNE"://!=
                    sp--;
                    dstack[sp] = (dstack[sp] != dstack[sp + 1])? 1 : 0;
                    at++;
                    break;
                case "BZT"://if 0 jump
                    //System.out.println(dstack[sp]);
                    if (dstack[sp] == 0) {
                        at = curCode.getLabel().getPoint();
                        //System.out.println(at);
                    } else {
                        at++;
                    }
                    sp--;
                    break;
                case "J":// jump
                    at = curCode.getLabel().getPoint();
                    //System.out.println(at);
                    break;
                case "JP0"://jump when 0
                    if (dstack[sp] == 0) {
                        at = curCode.getLabel().getPoint();
                    } else {
                        at++;
                    }
                    break;
                case "JP1"://jump when 1
                    if (dstack[sp] == 1) {
                        at = curCode.getLabel().getPoint();
                    } else {
                        at++;
                    }
                    break;
                case "NOT"://!a
                    if (dstack[sp] == 0) {
                        dstack[sp] = 1;
                    } else {
                        dstack[sp] = 0;
                    }
                    at++;
//                    System.out.println(code_point + "  " + "NOT" + "  栈顶指针: " + stack_point);
                    break;
                default:
                    at++;
            }
        }
        return res;
    }
}
