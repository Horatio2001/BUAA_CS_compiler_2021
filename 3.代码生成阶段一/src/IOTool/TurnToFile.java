package IOTool;

import Lexical.LexicalAnalyzerForm;
import Grammer.GrammerAnalyzer;

import java.io.*;
import java.util.ArrayList;

public class TurnToFile {
    public static String readFile(File file) {
        BufferedReader reader = null;
        StringBuilder sbd = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbd.append(tempStr).append("\n");
            }
            reader.close();
            return sbd.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    ;
                }
            }
        }
        return sbd.toString();
    }

    public static void LexicalToFile(boolean flag, ArrayList<LexicalAnalyzerForm> res, String outFileName) throws IOException {
        if (flag) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < res.size(); i++) {
                if (i != res.size() - 1) {
                    buf.append(res.get(i).turnToFileFormat()).append("\n");
                } else {
                    buf.append(res.get(i).turnToFileFormat());
                }
            }
            File file = new File(outFileName);
            FileWriter fileWritter = new FileWriter(file.getName(), false);
            fileWritter.write(buf.toString());
            fileWritter.close();
        }
    }

    public static void GrammerToFile(boolean flag, ArrayList<String> res, String outFileName) throws IOException {
        if (flag) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < res.size(); i++) {
                if (i != res.size() - 1) {
                    buf.append(res.get(i)).append("\n");
                } else {
                    buf.append(res.get(i));
                }
            }
            File file = new File(outFileName);
            FileWriter fileWritter = new FileWriter(file.getName(), false);
            fileWritter.write(buf.toString());
            fileWritter.close();
        }
    }

    public static void PcodeToFile(boolean flag, ArrayList<String> res, String outFileName) throws IOException {
        if (flag) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < res.size(); i++) {
                if (i != res.size() - 1) {
                    buf.append(res.get(i)).append("\n");
                } else {
                    buf.append(res.get(i));
                }
            }
            File file = new File(outFileName);
            FileWriter fileWritter = new FileWriter(file.getName(), false);
            fileWritter.write(buf.toString());
            fileWritter.close();
        }
    }

}
