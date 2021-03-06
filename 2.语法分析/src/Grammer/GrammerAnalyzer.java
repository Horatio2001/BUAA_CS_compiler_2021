package Grammer;
import Lexical.*;

import java.util.ArrayList;

public class GrammerAnalyzer {
    private ArrayList<LexicalAnalyzerForm> GrammerAnalyzerOutput;
    private int index = 0;

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
        while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("CONSTTK") ||
                (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK") &&
                        GrammerAnalyzerOutput.get(index + 1).getCategoryCode().equals("IDENFR") &&
                        !GrammerAnalyzerOutput.get(index + 2).getCategoryCode().equals("LPARENT"))) {
            res.addAll(Decl());
        }
        // 函数声明
        while ((GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK") &&
                GrammerAnalyzerOutput.get(index + 1).getCategoryCode().equals("IDENFR") &&
                GrammerAnalyzerOutput.get(index + 2).getCategoryCode().equals("LPARENT")) ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("VOIDTK")) {
            res.addAll(FuncDef());
        }
        //主函数
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK")) {
            res.addAll(MainFuncDef());
            res.add("<CompUnit>");
        } else {
            System.out.println("compunit main func error");
        }
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
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACK")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.addAll(ConstExp());
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RBRACK")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                }
                else {
                    //System.out.println(GrammerAnalyzerOutput.get(index).getCategoryCode());
                    System.out.println("constdef error1");
                }
            }
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("ASSIGN")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.addAll(ConstInitVal());
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
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACK")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.addAll(ConstExp());
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RBRACK")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                }
                else {
                    System.out.println("vardef error1");
                }
            }
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("ASSIGN")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                res.addAll(InitVal());
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
            res.addAll(Exp());
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
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("VOIDTK")) {
            res.addAll(FuncType());
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
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
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MAINTK")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RPARENT")) {
                        res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                        index++;
                        if(GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACE")){
                            res.addAll(Block());
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
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTTK")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACK")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("RBRACK")) {
                        res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                        index++;
                        while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACK")) {
                            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                            index++;
                            res.addAll(ConstExp());
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
                res.addAll(Block());
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
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
                    res.addAll(Exp());
                }
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
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT")) {
                    res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                    index++;
                    if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("STRCON")) {
                        res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                        index++;
                        while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("COMMA")) {
                            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                            index++;
                            res.addAll(Exp());
                        }
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
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LBRACK")) {
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
                index++;
                if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("LPARENT") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("INTCON") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                        GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
                    res.addAll(Exp());
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
            res.add("<LVal>");
        }
        else {
            System.out.println("lval error3");
        }
        return res;
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
                res.addAll(LVal());
                res.add("<PrimaryExp>");
                break;
            case "INTCON":
                res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
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
        if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("NOT")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            res.add("<UnaryOp>");
            res.addAll(UnaryExp());
            res.add("<UnaryExp>");
        }
        else if (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("IDENFR") &&
                GrammerAnalyzerOutput.get(index + 1).getCategoryCode().equals("LPARENT")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
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
        while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MULT") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("DIV") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MOD")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            res.addAll(UnaryExp());
            res.add("<MulExp>");
        }
        return res;
    }

    //加减表达式
    //AddExp → MulExp | AddExp ('+' | '−') MulExp
    //AddExp → MulExp { ('+' | '−') MulExp }
    public ArrayList<String> AddExp(){
        ArrayList<String> res = new ArrayList<>(MulExp());
        res.add("<AddExp>");
        while (GrammerAnalyzerOutput.get(index).getCategoryCode().equals("PLUS") ||
                GrammerAnalyzerOutput.get(index).getCategoryCode().equals("MINU")) {
            res.add(GrammerAnalyzerOutput.get(index).turnToFileFormat());
            index++;
            res.addAll(MulExp());
            res.add("<AddExp>");
        }
        return res;
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

}

