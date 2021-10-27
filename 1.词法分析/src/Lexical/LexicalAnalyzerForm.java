package Lexical;

public class LexicalAnalyzerForm {
    private String value;
    private String CategoryCode;

    public String getCategoryCode() { return CategoryCode; }

    public LexicalAnalyzerForm(String value, int kind){
        this.value = value;
        switch(kind){
            case 1:
                Lexicalform(value);
                break;
            case 2:
                this.CategoryCode = "INTCON";
                break;
            case 3:
                this.CategoryCode = "STRCON";
                break;
            case 4:
                LexicalSymbol(value);
                break;
        }
    }

    public String turnToFileFormat(){
        return this.CategoryCode + " " + this.value;
    }

    public void LexicalSymbol(String value){
        switch (value){
            case "!":
                this.CategoryCode = "NOT";
                break;
            case "&&":
                this.CategoryCode = "AND";
                break;
            case "||":
                this.CategoryCode = "OR";
                break;
            case "+":
                this.CategoryCode = "PLUS";
                break;
            case "-":
                this.CategoryCode = "MINU";
                break;
            case "*":
                this.CategoryCode = "MULT";
                break;
            case "/":
                this.CategoryCode = "DIV";
                break;
            case "%":
                this.CategoryCode = "MOD";
                break;
            case "<":
                this.CategoryCode = "LSS";
                break;
            case "<=":
                this.CategoryCode = "LEQ";
                break;
            case ">":
                this.CategoryCode = "GRE";
                break;
            case ">=":
                this.CategoryCode = "GEQ";
                break;
            case "==":
                this.CategoryCode = "EQL";
                break;
            case "!=":
                this.CategoryCode = "NEQ";
                break;
            case "=":
                this.CategoryCode = "ASSIGN";
                break;
            case ";":
                this.CategoryCode = "SEMICN";
                break;
            case ",":
                this.CategoryCode = "COMMA";
                break;
            case "(":
                this.CategoryCode = "LPARENT";
                break;
            case ")":
                this.CategoryCode = "RPARENT";
                break;
            case "[":
                this.CategoryCode = "LBRACK";
                break;
            case "]":
                this.CategoryCode = "RBRACK";
                break;
            case "{":
                this.CategoryCode = "LBRACE";
                break;
            case "}":
                this.CategoryCode = "RBRACE";
                break;
            default:
                System.out.println("Lexical symbol error!");
                break;
        }
    }

    public void Lexicalform(String value){
        switch(value){
            case "main":
                this.CategoryCode = "MAINTK";
                break;
            case "const":
                this.CategoryCode = "CONSTTK";
                break;
            case "int":
                this.CategoryCode = "INTTK";
                break;
            case "break":
                this.CategoryCode = "BREAKTK";
                break;
            case "continue":
                this.CategoryCode = "CONTINUETK";
                break;
            case "if":
                this.CategoryCode = "IFTK";
                break;
            case "else":
                this.CategoryCode = "ELSETK";
                break;
            case "while":
                this.CategoryCode = "WHILETK";
                break;
            case "getint":
                this.CategoryCode = "GETINTTK";
                break;
            case "printf":
                this.CategoryCode = "PRINTFTK";
                break;
            case "return":
                this.CategoryCode = "RETURNTK";
                break;
            case "void":
                this.CategoryCode = "VOIDTK";
                break;
            case "for":
                this.CategoryCode = "FORTK";
                break;
            default:
                this.CategoryCode = "IDENFR";
                break;
        }
    }
}
