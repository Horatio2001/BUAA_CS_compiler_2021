package Lexical;

import java.io.IOException;
import java.util.ArrayList;


public class LexicalAnalyzer {
    public String content;

    public LexicalAnalyzer(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public ArrayList<LexicalAnalyzerForm> LexicalAnalyze() throws IOException {
        LexicalJudge lexicalJudge = new LexicalJudge();
        ArrayList<LexicalAnalyzerForm> res = new ArrayList<>();
        int row = 1;
        for (int i = 0; i < content.length(); i++) {
            char chara = content.charAt(i);

            //judge if is number
            if (lexicalJudge.judgeNum(chara)) {
                StringBuilder buf = new StringBuilder();
                for (int j = i; j < content.length(); j++) {
                    char tmp = content.charAt(j);
                    if (lexicalJudge.judgeNum(tmp)) {
                        buf.append(tmp);
                    } else {
                        i = j - 1;
                        break;
                    }
                }
                res.add(new LexicalAnalyzerForm(buf.toString(), 2, row));
            }

            //judge if is alphabet
            else if (lexicalJudge.judgeWordFirst(chara)) {
                StringBuilder buf = new StringBuilder();
                for (int j = i; j < content.length(); j++) {
                    char tmp = content.charAt(j);
                    if (lexicalJudge.judgeWordThen(tmp)) {
                        buf.append(tmp);
                    } else {
                        i = j - 1;
                        break;
                    }
                }
                res.add(new LexicalAnalyzerForm(buf.toString(), 1, row));
            }

            //judge if is sym
            else if (lexicalJudge.judgeSingleSym(chara)) {
                StringBuilder buf = new StringBuilder();
                for (int j = i; j < content.length(); j++) {
                    char tmp = content.charAt(j);
                    if (lexicalJudge.judgeSingleSym(tmp)) {
                        if (lexicalJudge.judgeDoubleSym(tmp)) {
                            if (tmp == '&') {
                                // &&
                                if (content.charAt(j + 1) == '&' && j + 1 < content.length()) {
                                    res.add(new LexicalAnalyzerForm("&&", 4, row));
                                    j++;
                                }
                                // &
                                else {
                                    System.out.println("sym && error");
                                }
                            } else if (tmp == '|') {
                                // ||
                                if (content.charAt(j + 1) == '|' && j + 1 < content.length()) {
                                    res.add(new LexicalAnalyzerForm("||", 4, row));
                                    j++;
                                }
                                // |
                                else {
                                    System.out.println("sym || error");
                                }
                            } else if (tmp == '<') {
                                // <=
                                if (content.charAt(j + 1) == '=' && j + 1 < content.length()) {
                                    res.add(new LexicalAnalyzerForm("<=", 4, row));
                                    j++;
                                }
                                // <
                                else {
                                    res.add(new LexicalAnalyzerForm("<", 4, row));
                                }
                            } else if (tmp == '>') {
                                // >=
                                if (content.charAt(j + 1) == '=' && j + 1 < content.length()) {
                                    res.add(new LexicalAnalyzerForm(">=", 4, row));
                                    j++;
                                }
                                // >
                                else {
                                    res.add(new LexicalAnalyzerForm(">", 4, row));
                                }
                            } else if (tmp == '=') {
                                // ==
                                if (content.charAt(j + 1) == '=' && j + 1 < content.length()) {
                                    res.add(new LexicalAnalyzerForm("==", 4, row));
                                    j++;
                                }
                                // =
                                else {
                                    res.add(new LexicalAnalyzerForm("=", 4, row));
                                }
                            } else if (tmp == '!') {
                                // !=
                                if (content.charAt(j + 1) == '=' && j + 1 < content.length()) {
                                    res.add(new LexicalAnalyzerForm("!=", 4, row));
                                    j++;
                                }
                                // =
                                else {
                                    res.add(new LexicalAnalyzerForm("!", 4, row));
                                }
                            }
                        } else {
                            res.add(new LexicalAnalyzerForm(tmp + "", 4, row));
                        }
                    } else {
                        i = j - 1;
                        break;
                    }
                }
            }

            //judge format
            else if (chara == '\"') {
                StringBuilder buf = new StringBuilder();
                buf.append(chara);
                int r = row;
                for (int j = i + 1; j < content.length(); j++) {
                    char tmp = content.charAt(j);
                    if (lexicalJudge.judgeFormatChar(tmp) && tmp != '\"') {
                        // format \n
                        if (tmp == '\\') {
                            if (content.charAt(j + 1) == 'n' && j + 1 < content.length()) {
                                buf.append(tmp);
                            } else {
                                System.out.println("error1");
                            }
                        }
                        // %d
                        else if (tmp == '%') {
                            if (content.charAt(j + 1) == 'd' && j + 1 < content.length()) {
                                buf.append(tmp);
                            } else {
                                System.out.println("error2");
                            }
                        }
                        // buffer \n
                        else if (tmp == '\n') {
                            row++;
                        } else {
                            buf.append(tmp);
                        }
                    } else if (tmp == '\"') {
                        buf.append(tmp);
                        i = j;
                        break;
                    } else {
                        System.out.println("error3");
                    }
                }
                res.add(new LexicalAnalyzerForm(buf.toString(), 3, row));
            }

            //judge \n
            else if (chara == '\n') {
                row++;
                //continue;
            }

            //judge comment
            else if (chara == '/') {
                if (i + 1 < content.length()) {
                    i++;
                    if (content.charAt(i) == '/') {
                        for (i = i + 1; i < content.length(); i++) {
                            if (content.charAt(i) == '\n') {
                                row++;
                                break;
                            }
                        }
                    } else if (content.charAt(i) == '*') {
                        for (i = i + 1; i < content.length() - 1; i++) {
                            if (content.charAt(i) == '*' && content.charAt(i + 1) == '/') {
                                i++;
                                break;
                            }
                            if (content.charAt(i) == '\n') {
                                row++;
                                //continue;
                            }
                        }
                    } else {
                        res.add(new LexicalAnalyzerForm(chara + "", 4, row));
                        i--;
                    }
                } else {
                    res.add(new LexicalAnalyzerForm(chara + "", 4, row));
                    System.out.println("error4");
                }
            } else {
                if (chara != ' ' && chara != '\t') {
                    System.out.println("error5");
                }
            }
        }
        return res;
    }
}
