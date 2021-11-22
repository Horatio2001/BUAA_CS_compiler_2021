package Lexical;

public class LexicalJudge {
    public boolean judgeNum(char chara) {
        return Character.isDigit(chara);
    }

    //1a is not word, a1 is word
    public boolean judgeWordThen(char chara) {
        return ((int) chara >= 65 && (int) chara <= 90) || (int) chara == 95 || ((int) chara >= 97 && (int) chara <= 122) || Character.isDigit(chara);
    }

    public boolean judgeWordFirst(char chara) {
        return ((int) chara >= 65 && (int) chara <= 90) || (int) chara == 95 || ((int) chara >= 97 && (int) chara <= 122);
    }

    public boolean judgeSingleSym(char chara) {
        return chara == '!' || chara == '&' || chara == '|' || chara == '+' || chara == '-' || chara == '*' || chara == '%'
                || chara == '<' || chara == '>' || chara == '=' || chara == ';' || chara == ',' || chara == '(' || chara == ')'
                || chara == '[' || chara == ']' || chara == '{' || chara == '}';
    }

    public boolean judgeDoubleSym(char chara) {
        return chara == '!' || chara == '&' || chara == '|' || chara == '>' || chara == '<' || chara == '=';
    }

    public boolean judgeFormatChar(char chara) {
        return ((int) chara == 32) || ((int) chara == 33) || chara == '%' || ((int) chara >= 40 && (int) chara <= 126) || chara == '\n';
    }
}
