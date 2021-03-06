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

        //词法
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(content);
        ArrayList<LexicalAnalyzerForm> lexicalAnalyzerForms = lexicalAnalyzer.LexicalAnalyze();
        TurnToFile.LexicalToFile(false, lexicalAnalyzerForms, "output.txt");
        //语法
        GrammerAnalyzer grammerAnalyzer = new GrammerAnalyzer(lexicalAnalyzerForms);
        TurnToFile.GrammerToFile(true,grammerAnalyzer.grammerAnalyze(),"output.txt" );
    }
}
