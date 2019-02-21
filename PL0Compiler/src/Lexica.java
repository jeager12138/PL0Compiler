import java.io.*;
import java.util.HashSet;

public class Lexica {
    private char ch,rech;
    private File file;
    private InputStream reader;
    private String byId;

    private String id;  //存放标识符的值
    private int NUM;  //存放数字
    private SymType SYM;  //存放单词的类别 对应下方enum

    private int line = 1,row = 0; //记录当前读入的行列，方便error调用

    private int needBack = 0; //回读
    private String[] key = {
            "begin","call", "const", "do","end","if", "ood","procedure", "then", "var","while","read","write","else","repeat","until"
    };
    Lexica(File f) { file = f; }
    public int getNUM() {
        return NUM;
    }
    public void setNUM(int _NUM) {
        NUM = _NUM;
    }

    public SymType getType() {
        return SYM;
    }
    public String getId() {
        return id;
    }
    public String getById() {
        return byId;
    }
    public int getLine() {
        return line;
    }
    public int getRow() { return row; }

    private int getch() {
        try{
            if(needBack == 1) {
                ch = rech;
                needBack = 0;
                return 1;
            }
            row++;
            if(reader == null)
                reader = new FileInputStream(file);
            int by;
            if((by = reader.read()) == -1) {  //EOF
                return 0;
            } else {
                ch = (char)by;
                rech = ch;
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    private int IsKey(String token) {
        int i;
        for(i=0;i<key.length;i++) {
            if(token.equals(key[i])) {
                return i;
            }
        }
        return -1;
    }

    private boolean isdigit(char a) {
        if(a>=48&&a<=57)
            return true;
        return false;
    }
    private boolean isalpha(char a) {
        if(a>=65&&a<=90)
            return true;
        if(a>=97&&a<=122)
            return true;
        return false;
    }

    public int getSYM() {
        char[] token = new char[20];
        byId = "\0";
        NUM = 0;
        int by;
        by=getch();
        while(ch==' '||ch =='\n'||ch == '\t'||ch == '\r') {
            if(ch=='\n') {
                line++;
                row = 0;
            }
            by=getch(); //过滤掉单词间的空格
        }
        if(by == 0) return 0;
        if(isalpha(ch)) {
            int start = 0;
            do {
                token[start++] = ch;
                if(getch() == 0) break;
            } while(isalpha(ch)||isdigit(ch));
            char byChar[] = new char[start];
            for(int i=0;i<start;i++) byChar[i]=token[i];
            byId = String.valueOf(byChar);
            needBack = 1;

            int tt = IsKey(byId);
            if(tt != -1) {
                switch (tt) {
                    case 0:SYM = SymType.BEGIN;break;
                    case 1:SYM = SymType.CALL;break;
                    case 2:SYM = SymType.CONST;break;
                    case 3:SYM = SymType.DO;break;
                    case 4:SYM = SymType.END;break;
                    case 5:SYM = SymType.IF;break;
                    case 6:SYM = SymType.OOD;break;
                    case 7:SYM = SymType.PROCEDURE;break;
                    case 8:SYM = SymType.THEN;break;
                    case 9:SYM = SymType.VAR;break;
                    case 10:SYM = SymType.WHILE;break;
                    case 11:SYM = SymType.READ;break;
                    case 12:SYM = SymType.WRITE;break;
                    case 13:SYM = SymType.ELSE;break;
                    case 14:SYM = SymType.REPEAT;break;
                    case 15:SYM = SymType.UNTIL;break;
                }
                return 2;
            } else {
                id = byId;
                SYM = SymType.IDENTIFIER;
                return 1;
            }
        } else if(isdigit(ch)) {
            int n = 0;
            do{
                n=n*10+(ch-'0');
                getch();
            }while(isdigit(ch));
            SYM = SymType.NUMBER;
            NUM = n;
            needBack = 1;
            return 3;
        } else if(ch == ':') {
            getch();
            if(ch == '=') {
                SYM = SymType.BECOMES;
                byId = ":=";
                return 4;
            } else {
                //
            }
        }

        else if (ch == '=') {
            SYM = SymType.EQU;
            byId= "=";
            return 4;
        }

        else if (ch == '<') {
            getch();
            if(ch == '=') {
                SYM = SymType.LEQ;
                byId= "<=";
            } else if(ch == '>'){
                SYM = SymType.NEQ;
                byId = "<>";
            } else {
                needBack = 1;
                SYM = SymType.LES;
                byId = "<";
            }
            return 4;
        }

        else if(ch == '>') {
            getch();
            if(ch != '=') {
                needBack = 1;
                SYM= SymType.GTR;
                byId = ">";
            } else {
                SYM= SymType.GEQ;
                byId = ">=";
            }
            return 4;
        }

        else if (ch == '+') {
            SYM= SymType.PLUS;
            byId= "+";
            return 4;
        }

        else if (ch == '-') {
            SYM= SymType.MINUS;
            byId= "-";
            return 4;
        }

        else if (ch == '*') {
            SYM= SymType.TIMES;
            byId= "*";
            return 4;
        }

        else if (ch == '/') {
            SYM= SymType.SLASH;
            byId = "/";
            return 4;
        }

        else if (ch == ';') {
            SYM= SymType.SEMICOLON;
            byId = ";";
            return 5;
        }

        else if (ch == ',') {
            SYM= SymType.COMMA;
            byId = ",";
            return 5;
        }

        else if (ch == '.') {
            SYM= SymType.PERIOD;
            byId = ".";
            return 5;
        }

        else if (ch == '(') {
            SYM= SymType.LPAREN;
            byId = "(";
            return 4;
        }

        else if (ch == ')') {
            SYM= SymType.RPAREN;
            byId = ")";
            return 4;
        }

        else {
            //
        }
        return -1;
    } //读取下一个token 相关属性存在SYM NUM ID属性中

}


enum SymType {
    BEGIN,CALL,CONST,DO,ELSE,END,IF,OOD,PROCEDURE,REPEAT,UNTIL,THEN,VAR,WHILE,READ,WRITE,
    IDENTIFIER,
    NUMBER,
    PLUS,MINUS,TIMES,SLASH,EQU,NEQ,LES,LEQ,GTR,GEQ,BECOMES,
    LPAREN,RPAREN,COMMA,SEMICOLON,PERIOD
} //保留字类型

