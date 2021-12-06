import Grammer.GrammerAnalyzer;
import IOTool.Interpreter;
import Lexical.LexicalAnalyzer;
import Lexical.LexicalAnalyzerForm;
import IOTool.TurnToFile;
import Error.ErrorTable;
import  IOTool.ETBSorter;

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
        //错误表建立
        ArrayList<ErrorTable> ETB = new ArrayList<>();

        //词法
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(content);
        ArrayList<LexicalAnalyzerForm> lexicalAnalyzerForms = lexicalAnalyzer.LexicalAnalyze();
        TurnToFile.LexicalToFile(false, lexicalAnalyzerForms, "output.txt");
        ETB.addAll(lexicalAnalyzer.getErrorTables());

        //语法
        GrammerAnalyzer grammerAnalyzer = new GrammerAnalyzer(lexicalAnalyzerForms);
        TurnToFile.GrammerToFile(false,grammerAnalyzer.grammerAnalyze(),"output.txt" );
        ETB.addAll(grammerAnalyzer.getErrorTables());

        //错误处理
        //System.out.println(ETB.get(0).turnToFileFormat());
        ETBSorter.ETBSort(ETB);
        TurnToFile.ErrorToFile(true,ETB,"error.txt");

        //代码生成
//        Interpreter interpreter = new Interpreter(grammerAnalyzer.getCodelist());
//        System.out.println(interpreter.interpret());
//        TurnToFile.PcodeToFile(true, interpreter.interpret(),"pcoderesult.txt");
    }
}
