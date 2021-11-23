import Grammer.GrammerAnalyzer;
import Lexical.LexicalAnalyzer;
import Lexical.LexicalAnalyzerForm;
import IOTool.TurnToFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Compiler {
    public static void main(String[] args) throws IOException {
        File file = new File("testfile.txt");
        if (!file.exists()) {
            System.out.println("Filename error");
            System.exit(0);
        }
        String content = TurnToFile.readFile(file);
        //符号表建立

        //词法
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(content);
        ArrayList<LexicalAnalyzerForm> lexicalAnalyzerForms = lexicalAnalyzer.LexicalAnalyze();
        TurnToFile.LexicalToFile(true, lexicalAnalyzerForms, "output.txt");
        //语法
        GrammerAnalyzer grammerAnalyzer = new GrammerAnalyzer(lexicalAnalyzerForms);
        TurnToFile.GrammerToFile(false,grammerAnalyzer.grammerAnalyze(),"output.txt" );
        //错误处理
        //代码生成
    }
}
