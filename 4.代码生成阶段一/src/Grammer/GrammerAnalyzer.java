package Grammer;
import IOTool.Interpreter;
import Lexical.*;
import MidCode.Code;
import MidCode.Label;
import SymbolTable.*;

import java.util.ArrayList;
import java.util.Scanner;

public class GrammerAnalyzer {
    private ArrayList<LexicalAnalyzerForm> GrammerAnalyzerOutput;
    private int index = 0;
    private int level = 1;
    private int flag = 0;
    Block curBlock = new Block("global",null,level);
    Func_symbol cur_func_symbol = null;
//    Block global = curBlock;
    private int address = 0;
    private boolean need_lods = true;
    private boolean ismainfunc = false;

    private ArrayList<Code> codelist = new ArrayList<>();

    public ArrayList<Code> getCodelist() {
        return codelist;
    }

    public GrammerAnalyzer(ArrayList<LexicalAnalyzerForm> LexicalAnalyzerOutput) {
        this.GrammerAnalyzerOutput = LexicalAnalyzerOutput;
    }




    public ArrayList<String> grammerAnalyze() {
        index = 0;
        return new ArrayList<>(CompUnit());
    }

    //编译单元
    //CompUnit → {Decl} {FuncDef} MainFuncDef
    public ArrayList<String> CompUnit() {
        ArrayList<String> res = new ArrayList<>();
        // 全局变量的声明
        flag = 1;
        while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("CONSTTK") ||
                (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK") &&
                        GrammerAnalyzerOutput.get(index + 1).getCategoryCode().equals("IDENFR") &&
                        !GrammerAnalyzerOutput.get(index + 2).getCategoryCode().equals("LPARENT"))) {
            res.addAll(Decl());

        }
        flag = 0;
        Label label1 = new Label();
        Code code1 = new Code("JTM", label1);
        codelist.add(code1);
        // 函数声明
        while ((GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK") &&
                GrammerAnalyzerOutput.get(index + 1).getCategoryCode().equals("IDENFR") &&
                GrammerAnalyzerOutput.get(index + 2).getCategoryCode().equals("LPARENT")) ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("VOIDTK")) {
            res.addAll(FuncDef());
        }
        //主函数
        ismainfunc = true;
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK")) {
            res.addAll(MainFuncDef());
            res.add("<CompUnit>");
        } else {
            System.out.println("compunit main func error");
        }
        label1.setPoint(cur_func_symbol.getStartCode());
//        curBlock.show();
//        System.out.println("\nCodelist is below: \n");
//        for(Code code:codelist){
//            code.show();
//        }


        return res;
    }

    //声明 Decl不输出
    //Decl → ConstDecl | VarDecl
    public ArrayList<String> Decl() {
        ArrayList<String> res = new ArrayList<>();
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("CONSTTK")) {
            res.addAll(ConstDecl());
        }
        else if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK") &&
                GrammerAnalyzerOutput.get(index + 1).getCategoryCode().equals("IDENFR") &&
                !GrammerAnalyzerOutput.get(index + 1).getCategoryCode().equals("LPARENT")) {
            res.addAll(VarDecl());
        }
        return res;
    }

    //常量声明
    //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
    public ArrayList<String> ConstDecl(){
        ArrayList<String> res = new ArrayList<>();
        //Const_symbol const_symbol;
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("CONSTTK")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.addAll(ConstDef());
                while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("COMMA")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    res.addAll(ConstDef());
                }
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("SEMICN")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    res.add("<ConstDecl>");
                }
                else {
                    //System.out.println(GrammerAnalyzerOutput.get(index).getCategoryCode());
                    System.out.println("constdecl error1");
                }
            }
            else {
                System.out.println("constdecl error2");
            }
        }
        else {
            System.out.println("constdecl error3");
        }
        return res;
    }

    //常数定义
    //ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
    public ArrayList<String> ConstDef(){
        ArrayList<String> res = new ArrayList<>();
        Const_symbol const_symbol = new Const_symbol("",0);
        curBlock.addSymbol(const_symbol);
        int dim1 = 0;
        int dim2 = 0;
        int dim = 0;
        int midvalue = 0;
        int value = 0;
        int cishu = 0;
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            const_symbol.setName(GrammerAnalyzerOutput.get(index).getValue());
            index++;

            while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACK")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                const_symbol.setDim(const_symbol.getDim() + 1);
                index++;
                cishu++;
//                res.addAll(ConstExp());
                Record record1 = ConstExpValue();
                res.addAll(record1.getRes());
                midvalue = record1.getRetValue();
                if(cishu == 1){
                    dim1 = midvalue;
                }else if(cishu == 2){
                    dim2 = dim1;
                    dim1 = midvalue;
                }
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RBRACK")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                }
                else {
                    //System.out.println(GrammerAnalyzerOutput.get(index).getCategoryCode());
                    System.out.println("constdef error1");
                }
            }
            const_symbol.setDim1(dim1);
            const_symbol.setDim2(dim2);
            const_symbol.setAddress(address);
            if (flag==1){const_symbol.setGlobal(true);}

            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("ASSIGN")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
//                res.addAll(ConstInitVal());
                RecordValue recordValue = ConstInitValValue();
                res.addAll(recordValue.getRes());
                const_symbol.setValues(recordValue.getValues());
                res.add("<ConstDef>");
            }
            else {
                System.out.println("constdef error2");
            }
        }
        else {
            System.out.println("constdef error3");
        }
        return res;
    }

    //常量初值
    //ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
    public ArrayList<String> ConstInitVal(){
        ArrayList<String> res = new ArrayList<>();
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
            res.addAll(ConstExp());
            res.add("<ConstInitVal>");
        }
        else if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACE")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACE")) {
                res.addAll(ConstInitVal());
                while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("COMMA")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    res.addAll(ConstInitVal());
                }
            }
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RBRACE")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.add("<ConstInitVal>");
            }
            else {
                System.out.println("constinitval error1");
            }
        }
        else {
            System.out.println("constinitval error2");
        }
        return res;
    }
    //常量初值
    //ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
    public RecordValue ConstInitValValue(){
        ArrayList<String> res = new ArrayList<>();
        ArrayList<Integer> values = new ArrayList<>();
        RecordValue recordValue = new RecordValue(res, values);

        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
//            res.addAll(ConstExp());
            Record record = ConstExpValue();
            res.addAll(record.getRes());
            values.add(record.getRetValue());
            int value = record.getRetValue();
            //定义四个指令，INT 1，LDA， LDC， STOS
            Code code1 = new Code("INT", 1);
            Code code2 = new Code("LDA", 0,address);
            address++;
            Code code3 = new Code("LDC", value);
            Code code4 = new Code("STOS");
            codelist.add(code1);
            codelist.add(code2);
            codelist.add(code3);
            codelist.add(code4);

            res.add("<ConstInitVal>");
        }
        else if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACE")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACE")) {
//                res.addAll(ConstInitVal());
                RecordValue recordValue1 = ConstInitValValue();
                res.addAll(recordValue1.getRes());
                values.addAll(recordValue1.getValues());

                while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("COMMA")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
//                    res.addAll(ConstInitVal());
                    RecordValue recordValue2 = ConstInitValValue();
                    res.addAll(recordValue2.getRes());
                    values.addAll(recordValue2.getValues());
                }
            }
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RBRACE")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.add("<ConstInitVal>");
            }
            else {
                System.out.println("constinitval error1");
            }
        }
        else {
            System.out.println("constinitval error2");
        }
        recordValue.setValues(values);
        recordValue.setRes(res);
        return recordValue;
    }


    //变量声明
    //VarDecl → BType VarDef { ',' VarDef } ';'
    public ArrayList<String> VarDecl() {
        ArrayList<String> res = new ArrayList<>();
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            res.addAll(VarDef());
            while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("COMMA")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.addAll(VarDef());
            }
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("SEMICN")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.add("<VarDecl>");
            }
            else {
                System.out.println("Vardecl error1");
            }
        }
        else {
            System.out.println("Vardecl error2");
        }
        return res;
    }

    //变量定义
    //VarDef → Ident { '[' ConstExp ']' } |  Ident { '[' ConstExp ']' } '=' InitVal
    public ArrayList<String> VarDef() {
        ArrayList<String> res = new ArrayList<>();
        Record record = new Record(res,0);
        int cishu = 0;
        int dim1 = 0;
        int dim2 = 0;
        int value = 0;
        int midvalue = 0;
        Var_symbol var_symbol = new Var_symbol("",0);
        curBlock.addSymbol(var_symbol);

        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            var_symbol.setName(GrammerAnalyzerOutput.get(index).getValue());
            index++;
            while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACK")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                cishu++;
                var_symbol.setDim(var_symbol.getDim() + 1);
//                res.addAll(ConstExp());
                Record record1 = ConstExpValue();
                res.addAll(record1.getRes());
                midvalue = record1.getRetValue();
                if(cishu == 1){
                    dim1 = midvalue;
                }else if(cishu ==2){
                    dim2 = dim1;
                    dim1 = midvalue;
                }
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RBRACK")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                }
                else {
                    System.out.println("vardef error1");
                }
            }
            var_symbol.setDim1(dim1);
            var_symbol.setDim2(dim2);
            var_symbol.setAddress(address);
            int total = 0;
            if(var_symbol.getDim() == 0){
                total =1;
            }else if(var_symbol.getDim() == 1){
                total = dim1;
            }else if(var_symbol.getDim() == 2){
                total = dim1 * dim2;
            }
            if (flag==1){var_symbol.setGlobal(true);}
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("ASSIGN")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.addAll(InitVal());
            }else {
                //INT, LDA, LDC 0, STOS
                for(int i = 0;i < total;i++){
                    Code code1 = new Code("INT", 1);
                    Code code2 = new Code("LDA",0,address);
                    address++;
                    Code code3 = new Code("LDC",0);
                    Code code4 = new Code("STOS");
                    codelist.add(code1);
                    codelist.add(code2);
                    codelist.add(code3);
                    codelist.add(code4);
                }

            }
            res.add("<VarDef>");
        }
        else {
            System.out.println("vardef error2");
        }
        return res;
    }

    //变量初值
    //InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
    public ArrayList<String> InitVal() {
        ArrayList<String> res = new ArrayList<>();
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACE")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACE") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
                res.addAll(InitVal());
                while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("COMMA")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    res.addAll(InitVal());
                }
            }
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RBRACE")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.add("<InitVal>");
            }
        }
        else if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
            //INT 1, LDA , Exp, STOS
            Code code1 = new Code("INT", 1);
            Code code2 = new Code("LDA", 0,address);
            codelist.add(code1);
            codelist.add(code2);
            address++;
            res.addAll(Exp());
            Code code3 = new Code("STOS");
            codelist.add(code3);


            res.add("<InitVal>");
        }
        else {
            System.out.println("initval error1");
        }
        return res;
    }

    //函数定义
    //FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
    public ArrayList<String> FuncDef() {
        ArrayList<String> res = new ArrayList<>();
        Func_symbol func_symbol = new Func_symbol("",0);
        address = 3;
        func_symbol.setStartCode(codelist.size());
        Label label = new Label();
        Code code = new Code("INT_L", label);
        codelist.add(code);

        String type;
        String name;
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("VOIDTK")) {

            type = GrammerAnalyzerOutput.get(index).getValue();
            if(type.equals("int")){
                func_symbol.setDim(0);
            }else if(type.equals("void")){
                func_symbol.setDim(-1);
            }

            res.addAll(FuncType());
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                name = GrammerAnalyzerOutput.get(index).getValue();
                func_symbol.setName(name);
                index++;

                curBlock.addSymbol(func_symbol);
                Block block1 = new Block(type, curBlock, curBlock.getLevel()+1);
                curBlock.addCBlock(block1);
                curBlock = block1;
                cur_func_symbol = func_symbol;

                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK")) {
                        res.addAll(FuncFParams());
                    }
                    if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RPARENT")) {
                        res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                        index++;
                        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACE")) {
                            res.addAll(Block());
                            res.add("<FuncDef>");
                            if(!codelist.get(codelist.size()-1).getName().equals("RET")){
                                Code code1 = new Code("RET");
                                codelist.add(code1);
                            }
                            label.setPoint(address);
                            curBlock = curBlock.getFBlock();
                        }
                        else {
                            System.out.println("funcdef error1");
                        }
                    }
                    else {
                        System.out.println("funcdef error2");
                    }
                }
                else {
                    System.out.println("funcdef error3");
                }
            }
            else {
                System.out.println("funcdef error4");
            }
        }
        else {
            System.out.println("funcdef error5");
        }
        return res;
    }

    //主函数定义
    //MainFuncDef → 'int' 'main' '(' ')' Block
    public ArrayList<String> MainFuncDef() {
        ArrayList<String> res = new ArrayList<>();

        Func_symbol func_symbol = new Func_symbol("",0);
        String type;
        String name;
        address = 0;
        func_symbol.setStartCode(codelist.size());
        Label label = new Label();
        Code code = new Code("INT_L", label);
        codelist.add(code);


        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MAINTK")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                func_symbol.setName("main");

                index++;

                curBlock.addSymbol(func_symbol);
                Block block1 = new Block("int", curBlock, curBlock.getLevel()+1);
                curBlock.addCBlock(block1);
                curBlock = block1;
                cur_func_symbol = func_symbol;

                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RPARENT")) {
                        res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                        index++;
                        if(GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACE")){
                            res.addAll(Block());
                            label.setPoint(address);
                            curBlock = curBlock.getFBlock();
                            res.add("<MainFuncDef>");
                        } else{
                            System.out.println("mainfunc error 0");
                        }
                    } else {
                        System.out.println("mainfunc error 1");
                    }
                } else {
                    System.out.println("mainfunc error 2");
                }
            } else {
                System.out.println("mainfunc error 3");
            }
        } else {
            System.out.println("mainfunc error 4");
        }
        return res;
    }

    //函数类型
    //FuncType → 'void' | 'int'
    public ArrayList<String> FuncType() {
        ArrayList<String> res = new ArrayList<>();
        //System.out.println("functype");
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("VOIDTK")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            res.add("<FuncType>");
        } else {
            System.out.println("Functype error");
        }
        return res;
    }

    //函数形参表
    //FuncFParams → FuncFParam { ',' FuncFParam }
    public ArrayList<String> FuncFParams() {
        ArrayList<String> res = new ArrayList<>();
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK")) {
            res.addAll(FuncFParam());
            while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("COMMA")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK")) {
                    res.addAll(FuncFParam());
                }
                else {
                    System.out.println("funfcparams error1");
                }
            }
            res.add("<FuncFParams>");
        }
        else {
            System.out.println("funfcparams error2");
        }
        return res;
    }

    //函数形参
    //FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
    public ArrayList<String> FuncFParam() {
        ArrayList<String> res = new ArrayList<>();
        Param_symbol param_symbol = new Param_symbol("",0);
        String type;
        String name;
        int dim1 = 0;
        int dim2 = 0;
        int midvalue = 0;

        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());

                name = GrammerAnalyzerOutput.get(index).getValue();
                param_symbol.setName(name);

                index++;
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACK")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    param_symbol.setDim(1);
                    param_symbol.setDim1(0);
                    index++;
                    if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RBRACK")) {
                        res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                        index++;
                        while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACK")) {
                            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());

                            param_symbol.setDim(2);
                            index++;
//                            res.addAll(ConstExp());
                            Record record1 = ConstExpValue();
                            res.addAll(record1.getRes());
                            midvalue = record1.getRetValue();
                            param_symbol.setDim2(0);
                            param_symbol.setDim1(midvalue);

                            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RBRACK")) {
                                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                                index++;
                            }
                            else {
                                System.out.println("funcfparam error1");
                            }
                        }
                    }
                    else {
                        System.out.println("funcfparam error2");
                    }
                }
                cur_func_symbol.addParam(param_symbol);
                curBlock.addSymbol(param_symbol);
                param_symbol.setAddress(address);
                address++;
                res.add("<FuncFParam>");
            }
            else {
                System.out.println("funcfparam error3");
            }
        }
        else {
            System.out.println("funcfparam error4");
        }
        return res;
    }

    //语法块
    //Block → '{' { BlockItem } '}'
    public ArrayList<String> Block(){
        ArrayList<String> res = new ArrayList<>();
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACE")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("CONSTTK") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACE") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IFTK") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("WHILETK") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("BREAKTK") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("CONTINUETK") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RETURNTK") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PRINTFTK") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("SEMICN")) {
                res.addAll(BlockItem());
            }
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RBRACE")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.add("<Block>");
            }
            else {
                System.out.println("block error1");
            }
        }
        else {
            System.out.println("block error2");
        }
        return res;
    }

    //语句块项
    //BlockItem → Decl | Stmt
    public ArrayList<String> BlockItem() {
        ArrayList<String> res = new ArrayList<>();
        //System.out.println("blockitem");
        switch (GrammerAnalyzerOutput.get(index).getCategoryCode()) {
            case "CONSTTK":
                res.addAll(ConstDecl());
                break;
            case "INTTK":
                res.addAll(VarDecl());
                break;
            case "IDENFR":
            case "LBRACE":
            case "IFTK":
            case "WHILETK":
            case "BREAKTK":
            case "CONTINUETK":
            case "RETURNTK":
            case "PRINTFTK":
            case "LPARENT":
            case "INTCON":
            case "NOT":
            case "PLUS":
            case "MINU":
            case "SEMICN":
                res.addAll(Stmt());
                break;
            default:
                System.out.println("blockitem error");
                break;
        }
        return res;
    }

    //语句
    /*
    Stmt → LVal '=' Exp ';' // 每种类型的语句都要覆盖
     | [Exp] ';' //有⽆Exp两种情况
     | Block
     | 'if' '( Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.⽆else
     | 'while' '(' Cond ')' Stmt
     | 'break' ';'
     | 'continue' ';'
     | 'return' [Exp] ';' // 1.有Exp 2.⽆Exp
     | LVal = 'getint''('')'';'
     | 'printf''('FormatString{,Exp}')'';'
     */
    public ArrayList<String> Stmt() {
        ArrayList<String> res = new ArrayList<>();
        switch (GrammerAnalyzerOutput.get(index).getCategoryCode()) {
            //| Block
            case "LBRACE":
                Block block = new Block("block",curBlock,curBlock.getLevel()+1);
                block.setFBlock(curBlock);
                curBlock.addCBlock(block);
                curBlock = block;

                res.addAll(Block());

                curBlock = curBlock.getFBlock();
                res.add("<Stmt>");
                break;
            //| 'if' '( Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.⽆else
            case "IFTK":
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    res.addAll(Cond());
                    if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RPARENT")) {
                        res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                        index++;
                        res.addAll(Stmt());
                        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("ELSETK")) {
                            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                            index++;
                            res.addAll(Stmt());
                        }
                        res.add("<Stmt>");
                    } else {
                        System.out.println("----------stmt IFTK error1");
                    }
                } else {
                    System.out.println("----------stmt IFTK error2");
                }
                break;
            //| 'while' '(' Cond ')' Stmt
            case "WHILETK":
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    res.addAll(Cond());
                    if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RPARENT")) {
                        res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                        index++;
                        res.addAll(Stmt());
                        res.add("<Stmt>");
                    } else {
                        System.out.println("----------stmt whiletk error1");
                    }
                } else {
                    System.out.println("----------stmt whiletk error2");
                }
                break;
            //| 'break' ';'
            case "BREAKTK":
            //| 'continue' ';'
            case "CONTINUETK":
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("SEMICN")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    res.add("<Stmt>");
                } else {
                    System.out.println("----------stmt continuetk error1");
                }
                break;
            //| 'return' [Exp] ';' // 1.有Exp 2.⽆Exp
            case "RETURNTK":
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                Code code4 = new Code("LDA", 0, 0);
                codelist.add(code4);
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
                    res.addAll(Exp());
                }
                Code code5 = new Code("STOS");
                codelist.add(code5);
                Code code6 = null;
                if(ismainfunc){
                    code6 = new Code("RET_TO_END");
                }else {
                    code6 = new Code("RET");
                }
                codelist.add(code6);
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("SEMICN")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    res.add("<Stmt>");
                } else {
                    System.out.println("----------stmt returntk error1");
                }
                break;
            //| 'printf''('FormatString{,Exp}')'';'
            case "PRINTFTK":
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                String strcon = "";
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("STRCON")) {
                        res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                        strcon = GrammerAnalyzerOutput.get(index).getValue();
                        index++;
                        while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("COMMA")) {
                            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                            index++;
                            res.addAll(Exp());
                        }
                        Code code1 = new Code("PRF",strcon);
                        codelist.add(code1);
                        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RPARENT")) {
                            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                            index++;
                            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("SEMICN")) {
                                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                                index++;
                                res.add("<Stmt>");
                            } else {
                                System.out.println("----------stmt printtk error1");
                            }
                        } else {
                            System.out.println("----------stmt printtk error2");
                        }
                    } else {
                        System.out.println("----------stmt printtk error3");
                    }
                } else {
                    System.out.println("----------stmt printtk error4");
                }
                break;
            // |;
            case "SEMICN":
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.add("<Stmt>");
                break;
            //| LVal '=' Exp ';'
            //| LVal = 'getint''('')'';'
            case "IDENFR":
                if (GrammerAnalyzerOutput.get(index + 1).getCategoryCode().equals("LPARENT")) {
                    res.addAll(Exp());
                    if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("SEMICN")) {
                        res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                        index++;
                        res.add("<Stmt>");
                    } else {
                        System.out.println("----------stmt IDENFR error1");
                    }
                } else {
                    int temp = index;
                    ArrayList<String> t = LVal();
                    if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("ASSIGN")) {
                        res.addAll(t);
                        res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                        index++;
                        //| LVal = 'getint''('')'';'
                        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("GETINTTK")) {
                            Code code2 = new Code("GET");
                            codelist.add(code2);
                            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                            index++;
                            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT")) {
                                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                                index++;
                                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RPARENT")) {
                                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                                    index++;
                                } else {
                                    System.out.println("----------stmt IDENFR error2");
                                }
                            } else {
                                System.out.println("----------stmt IDENFR error3");
                            }
                            //| LVal '=' Exp ';'
                        } else {
                            res.addAll(Exp());
                        }
                        Code code1 = new Code("STOS");
                        codelist.add(code1);
                        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("SEMICN")) {
                            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                            index++;
                            res.add("<Stmt>");
                        } else {
                            System.out.println("----------stmt IDENFR error4");
                        }
                    } else {
                        index = temp;
                        res.addAll(Exp());
                        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("SEMICN")) {
                            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                            index++;
                            res.add("<Stmt>");
                        } else {
                            System.out.println("----------stmt IDENFR error5");
                        }
                    }
                }
                break;
            //[Exp] ';'
            case "LPARENT":
            case "INTCON":
            case "NOT":
            case "PLUS":
            case "MINU":
                res.addAll(Exp());
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("SEMICN")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    res.add("<Stmt>");
                } else {
                    System.out.println("----------stmt minu error1");
                }
                break;
            default:
                System.out.println("----------stmt default error1");
                break;
        }
        return res;
    }

    //表达式
    //Exp → AddExp
    public ArrayList<String> Exp() {
        ArrayList<String> res = new ArrayList<>();
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
            res.addAll(AddExp());
            res.add("<Exp>");
        }
        else {
            System.out.println("exp error1");
        }
        return res;
    }
    //表达式
    //Exp → AddExp
    public Record ExpValue() {
        ArrayList<String> res = new ArrayList<>();
        Record record = new Record(res,0);
        int value;
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
//            res.addAll(AddExp());
            Record record1 = AddExpValue();
            value = record1.getRetValue();
            res.addAll(record1.getRes());
            res.add("<Exp>");
            record.setRetValue(value);
        }
        else {
            System.out.println("exp error1");
        }
        return record;
    }

    //条件表达式
    //Cond → LOrExp
    public ArrayList<String> Cond() {
        ArrayList<String> res = new ArrayList<>();
        //System.out.println("cond");
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
            res.addAll(LOrExp());
            res.add("<Cond>");
        } else {
            System.out.println("cond error");
        }
        return res;
    }

    //左值表达式
    //LVal → Ident {'[' Exp ']'}
    public ArrayList<String> LVal() {
        ArrayList<String> res = new ArrayList<>();
        int dim = 0;
        int cishu = 0;
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            String lval_name = GrammerAnalyzerOutput.get(index).getValue();
            index++;
            Symbol symbol = curBlock.search(lval_name);
            dim = symbol.getDim();
            while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACK")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                cishu++;
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
                    res.addAll(Exp());
                    if(cishu == 1 && dim == 2){
                        Code code1 = new Code("LDC", symbol.getDim1());
                        Code code2 = new Code("MUL");
                        codelist.add(code1);
                        codelist.add(code2);
                    }
                    if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RBRACK")) {
                        res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                        index++;
                    }
                    else {
                        System.out.println("lval error1");
                    }
                }
                else {
                    System.out.println("lval error2");
                }
            }
            if(symbol != null){
                Code code1;
                Code code2;
                Code code3;
                Code code4;
                int isglobal = symbol.isGlobal()?1:0;
                if(symbol.getDim() == 0){
                    code1 = new Code("LDA", isglobal, symbol.getAddress());
                    codelist.add(code1);
                }else if(symbol.getDim() == 1 && cishu == 1){
                    if(symbol.getType().equals("param")){
                        code1 = new Code("LOD", isglobal, symbol.getAddress());
                    }else {
                        code1 = new Code("LDA", isglobal, symbol.getAddress());
                    }
                    code2 = new Code("ADD");
                    codelist.add(code1);
                    codelist.add(code2);
                }else if(symbol.getDim() == 2 && cishu == 2){
                    if(symbol.getType().equals("param")){
                        code1 = new Code("LOD", isglobal, symbol.getAddress());
                    }else {
                        code1 = new Code("LDA", isglobal, symbol.getAddress());
                    }
                    code2 = new Code("ADD");
                    code3 = new Code("ADD");
                    codelist.add(code1);
                    codelist.add(code2);
                    codelist.add(code3);
                }else if(cishu == 0 && (dim == 1 || dim == 2)){
                    if(symbol.getType().equals("param")){
                        code1 = new Code("LOD", isglobal, symbol.getAddress());
                    }else {
                        code1 = new Code("LDA", isglobal, symbol.getAddress());
                    }
                    codelist.add(code1);
                    need_lods = false;
                }else if(cishu == 1 && dim == 2){
                    if(symbol.getType().equals("param")){
                        code1 = new Code("LOD", isglobal, symbol.getAddress());
                    }else {
                        code1 = new Code("LDA", isglobal, symbol.getAddress());
                    }
                    code2 = new Code("ADD");
                    codelist.add(code1);
                    codelist.add(code2);
                    need_lods = false;
                }
            }
            res.add("<LVal>");
        }
        else {
            System.out.println("lval error3");
        }
        return res;
    }
    //左值表达式
    //LVal → Ident {'[' Exp ']'}
    public Record LValValue() {
        ArrayList<String> res = new ArrayList<>();
        Record record = new Record(res,0);
        int dim1 = 0;
        int dim2 = 0;
        int midvalue = 0;
        int cishu = 0;
        int dim;
        int value = 0;
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            String lval_name = GrammerAnalyzerOutput.get(index).getValue();
            Symbol symbol = curBlock.search(lval_name);
            dim = symbol.getDim();
            index++;
            while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACK")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                cishu++;
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
//                    res.addAll(Exp);
                    Record record1 = ExpValue();
                    res.addAll(record1.getRes());
                    midvalue = record1.getRetValue();
                    if(cishu == 1 && dim == 1){
                        dim1 = midvalue;
                    }else if(cishu == 1 && dim == 2){
                        dim2 = midvalue;
                    }else if(cishu == 2 && dim == 2){
                        dim1 = midvalue;
                    }
                    if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RBRACK")) {
                        res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                        index++;
                    }
                    else {
                        System.out.println("lval error1");
                    }
                }
                else {
                    System.out.println("lval error2");
                }
            }
            int off=0;
            if(dim == 0){
                off = 0;
            }else if(dim == 1){
                off = dim1;
            }else if(dim == 2){
                off = dim2 * symbol.getDim1() + dim1;
            }
            value = ((Const_symbol)symbol).getValues().get(off);
            record.setRetValue(value);
            res.add("<LVal>");
        }
        else {
            System.out.println("lval error3");
        }
        return record;
    }

    //基本表达式
    //PrimaryExp → '(' Exp ')' | LVal | Number
    public ArrayList<String> PrimaryExp() {
        ArrayList<String> res = new ArrayList<>();
        //System.out.println("primaryexp");
        switch (GrammerAnalyzerOutput.get(index).getCategoryCode()) {
            case "LPARENT":
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.addAll(Exp());
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RPARENT")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    res.add("<PrimaryExp>");
                }
                break;
            case "IDENFR":
                need_lods = true;
                res.addAll(LVal());
                if(need_lods){
                    Code code1 = new Code("LODS");
                    codelist.add(code1);
                }
                res.add("<PrimaryExp>");
                break;
            case "INTCON":
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                int value = Integer.valueOf(GrammerAnalyzerOutput.get(index).getValue());
                Code code2 = new Code("LDC", value);
                codelist.add(code2);
                index++;
                res.add("<Number>");
                res.add("<PrimaryExp>");
                break;
            default:
                System.out.println("primaryexp error");
                break;
        }
        return res;
    }

    //基本表达式
    //PrimaryExp → '(' Exp ')' | LVal | Number
    public Record PrimaryExpValue() {
        ArrayList<String> res = new ArrayList<>();
        Record record = new Record(res,0);
        //System.out.println("primaryexp");
        int value;
        int midvalue;
        switch (GrammerAnalyzerOutput.get(index).getCategoryCode()) {
            case "LPARENT":
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
//                res.addAll(Exp());
                Record record1 = ExpValue();
                res.addAll(record1.getRes());
                value = record1.getRetValue();
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RPARENT")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    res.add("<PrimaryExp>");
                }
                record.setRetValue(value);
                break;
            case "IDENFR":
                Record record2 = LValValue();
                res.addAll(record2.getRes());
                res.add("<PrimaryExp>");
                value = record2.getRetValue();
                record.setRetValue(value);
                break;
            case "INTCON":
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                value = Integer.valueOf(GrammerAnalyzerOutput.get(index).getValue());
                index++;
                res.add("<Number>");
                res.add("<PrimaryExp>");
                record.setRetValue(value);
                break;
            default:
                System.out.println("primaryexp error");
                break;
        }
        return record;
    }

    //数值
    //Number → IntConst
    public ArrayList<String> Number() {
        ArrayList<String> res = new ArrayList<>();
        //System.out.println("number");
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INCON")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            res.add("<Number>");
        }
        return res;
    }

    //一元表达式
    //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
    //注意先识别调用函数的Ident '(' [FuncRParams] ')'，再识别基本表达式PrimaryExp
    public ArrayList<String> UnaryExp() {
        ArrayList<String> res = new ArrayList<>();
        String op;
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            op = GrammerAnalyzerOutput.get(index).getValue();
            index++;
            res.add("<UnaryOp>");
            res.addAll(UnaryExp());
            if(op.equals("-")){
                Code code1 = new Code("MINU");
                codelist.add(code1);
            }
            res.add("<UnaryExp>");
        }
        else if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") &&
                GrammerAnalyzerOutput.get(index + 1).getCategoryCode().equals("LPARENT")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            String fun_name = GrammerAnalyzerOutput.get(index).getValue();
            Symbol symbol = curBlock.search(fun_name);
            Code code1 = new Code("INT", 3);
            codelist.add(code1);
            index++;
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
                    res.addAll(FuncRParams());
                }
                Code code2 = new Code("DOWN", 3+((Func_symbol)symbol).getParams().size());
                codelist.add(code2);
                Code code3 = new Code("CAL", ((Func_symbol)symbol).getStartCode());
                codelist.add(code3);
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RPARENT")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    res.add("<UnaryExp>");
                }
                else {
                    System.out.println("unaryexp error1");
                }
            }
            else {
                System.out.println("unaryexp error2");
            }
        }
        else if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON")) {
            res.addAll(PrimaryExp());
            res.add("<UnaryExp>");
        }
        else {
            System.out.println("unaryexp error3");
        }
        return res;
    }
    //一元表达式
    //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
    //注意先识别调用函数的Ident '(' [FuncRParams] ')'，再识别基本表达式PrimaryExp
    public Record UnaryExpValue() {
        ArrayList<String> res = new ArrayList<>();
        String op;
        int midvalue;
        int value = 0;
        Record record = new Record(res,0);

        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
            op = GrammerAnalyzerOutput.get(index).getValue();
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            res.add("<UnaryOp>");
            Record record1 = UnaryExpValue();
            midvalue = record1.getRetValue();
            res.addAll(record1.getRes());
            if(op.equals("+")){
                value = midvalue;
            }else if(op.equals("-")){
                value = -1 * midvalue;
            }
            record.setRetValue(value);
//            res.addAll(UnaryExp());
            res.add("<UnaryExp>");
        }
        else if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON")) {
            Record record1 = PrimaryExpValue();
            res.addAll(record1.getRes());
            value = record1.getRetValue();
//            res.addAll(PrimaryExp());
            res.add("<UnaryExp>");
            record.setRetValue(value);
        }
        else {
            System.out.println("unaryexp error3");
        }
        return record;
    }

    //单目运算符
    //UnaryOp → '+' | '−' | '!'
    public ArrayList<String> UnaryOp() {
        ArrayList<String> res = new ArrayList<>();
        //System.out.println("unaryop");
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            res.add("<UnaryOp>");
        }
        return res;
    }

    //函数实参表
    //FuncRParams → Exp { ',' Exp }
    public ArrayList<String> FuncRParams() {
        ArrayList<String> res = new ArrayList<>();
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
            res.addAll(Exp());
            while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("COMMA")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
                    res.addAll(Exp());
                }
                else {
                    System.out.println("funcrparams error1");
                }
            }
            res.add("<FuncRParams>");
        }
        else {
            System.out.println("funcrparams error2");
        }
        return res;
    }

    //乘除模表达式
    //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    //MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
    public ArrayList<String> MulExp(){
        ArrayList<String> res = new ArrayList<>(UnaryExp());
        res.add("<MulExp>");
        String op;
        while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MULT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("DIV") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MOD")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            op = GrammerAnalyzerOutput.get(index).getValue();
            index++;
            res.addAll(UnaryExp());
            if(op.equals("*")){
                Code code1 = new Code("MUL");
                codelist.add(code1);
            } else if (op.equals("/")) {
                Code code2 = new Code("DIV");
                codelist.add(code2);
            }else if(op.equals("%")){
                Code code3 = new Code("MOD");
                codelist.add(code3);
            }
            res.add("<MulExp>");
        }
        return res;
    }

    //乘除模表达式
    //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    //MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
    public Record MulExpValue(){
        ArrayList<String> res = new ArrayList<>();
        Record record = new Record(res, 0);
        int value;
        int midvalue;
        String op;
        Record record1 = UnaryExpValue();
        res.addAll(record1.getRes());
        value = record1.getRetValue();
        res.add("<MulExp>");
        while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MULT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("DIV") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MOD")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            op = GrammerAnalyzerOutput.get(index).getValue();
            index++;
//            res.addAll(UnaryExp());
            Record record2 = UnaryExpValue();
            res.addAll(record2.getRes());
            midvalue = record2.getRetValue();
            if (op.equals("*")){
                value = value * midvalue;
            }else if(op.equals("/")){
                value = value / midvalue;
            }else if (op.equals("%")){
                value = value % midvalue;
            }
            res.add("<MulExp>");
        }
        record.setRetValue(value);
        return record;
    }

    //加减表达式
    //AddExp → MulExp | AddExp ('+' | '−') MulExp
    //AddExp → MulExp { ('+' | '−') MulExp }
    public ArrayList<String> AddExp(){
        ArrayList<String> res = new ArrayList<>(MulExp());
        res.add("<AddExp>");
        String op;
        while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            op = GrammerAnalyzerOutput.get(index).getValue();
            index++;
            res.addAll(MulExp());
            if(op.equals("+")){
                Code code1 =new Code("ADD");
                codelist.add(code1);
            }else if(op.equals("-")){
                Code code2 = new Code("SUB");
                codelist.add(code2);
            }

            res.add("<AddExp>");
        }
        return res;
    }
    //加减表达式
    //AddExp → MulExp | AddExp ('+' | '−') MulExp
    //AddExp → MulExp { ('+' | '−') MulExp }
    public Record AddExpValue(){
        ArrayList<String> res = new ArrayList<>();
        Record record = new Record(res, 0);
        String op;
        int value;
        int midValue;
        Record record1 = MulExpValue();
        value = record1.getRetValue();
        res.addAll(record1.getRes());
        res.add("<AddExp>");
        while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            op = GrammerAnalyzerOutput.get(index).getValue();
            index++;
//            res.addAll(MulExp());
            Record record2 = MulExpValue();
            midValue = record2.getRetValue();
            res.addAll(record2.getRes());
            if(op.equals("+")){
                value = value + midValue;
            }else if(op.equals("-")){
                value = value - midValue;
            }
            res.add("<AddExp>");
        }
        record.setRetValue(value);
        return record;
    }

    //关系表达式
    //RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    //RelExp → AddExp { ('<' | '>' | '<=' | '>=') AddExp }
    public ArrayList<String> RelExp() {
        ArrayList<String> res = new ArrayList<>();
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
            res.addAll(AddExp());
            res.add("<RelExp>");
            while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LSS") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LEQ") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("GRE") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("GEQ")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.addAll(AddExp());
                res.add("<RelExp>");
            }
        }
        else {
            System.out.println("RelExp error");
        }
        return res;
    }

    //相等性表达式
    //EqExp → RelExp | EqExp ('==' | '!=') RelExp
    //EqExp → RelExp { ('==' | '!=') RelExp }
    public ArrayList<String> EqExp() {
        ArrayList<String> res = new ArrayList<>();
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
            res.addAll(RelExp());
            res.add("<EqExp>");
            while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("EQL") ||
                    GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NEQ")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.addAll(RelExp());
                res.add("<EqExp>");
            }
        }
        else {
            System.out.println("EqExp error");
        }
        return res;
    }

    //逻辑与表达式
    //LAndExp → EqExp | LAndExp '&&' EqExp
    //LAndExp → EqExp { '&&' EqExp }
    public ArrayList<String> LAndExp() {
        ArrayList<String> res = new ArrayList<>();
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
            res.addAll(EqExp());
            res.add("<LAndExp>");
            while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("AND")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.addAll(EqExp());
                res.add("<LAndExp>");
            }
        }
        else {
            System.out.println("LAndExp error");
        }
        return res;
    }

    //逻辑或表达式
    //LOrExp → LAndExp | LOrExp '||' LAndExp
    //LOrExp → LAndExp { '||' LAndExp }
    public ArrayList<String> LOrExp() {
        ArrayList<String> res = new ArrayList<>();
        //System.out.println("lorexp");
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
            res.addAll(LAndExp());
            res.add("<LOrExp>");
            while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("OR")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.addAll(LAndExp());
                res.add("<LOrExp>");
            }
        } else {
            System.out.println("LOrExp error");
        }
        return res;
    }

    //常量表达式
    //ConstExp → AddExp
    public ArrayList<String> ConstExp(){
        ArrayList<String> res = new ArrayList<>();
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
            res.addAll(AddExp());
            res.add("<ConstExp>");
        }
        else {
            System.out.println("constexp error");
        }
        return res;
    }
    //常量表达式
    //ConstExp → AddExp
    public Record ConstExpValue(){
        ArrayList<String> res = new ArrayList<>();
        Record record = new Record(res, 0);
        int value = 0;
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
//            res.addAll(AddExp());
            Record record1 = AddExpValue();
            value = record1.getRetValue();
            res.addAll(record1.getRes());
            res.add("<ConstExp>");

            record.setRetValue(value);
        }
        else {
            System.out.println("constexp error");
        }
        return record;
    }

}

