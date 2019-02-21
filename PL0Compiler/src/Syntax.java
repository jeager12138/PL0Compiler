import java.io.File;
import java.util.HashSet;

public class Syntax {
    private Lexica lex;
    private AllPcode allPcode;
    private AllSymbol allSymbol;
    private Error err;

    private HashSet<SymType> declareBegin,stateBegin,factorBegin;


    Syntax(File f) {
        lex = new Lexica(f);
        allPcode = new AllPcode();
        allSymbol = new AllSymbol();
        err = new Error();

        declareBegin = new HashSet<SymType>();
        stateBegin = new HashSet<SymType>();
        factorBegin = new HashSet<SymType>();
        declareBegin.add(SymType.CONST);
        declareBegin.add(SymType.VAR);
        declareBegin.add(SymType.PROCEDURE);
        stateBegin.add(SymType.BEGIN);
        stateBegin.add(SymType.CALL);
        stateBegin.add(SymType.IF);
        stateBegin.add(SymType.WHILE);
        stateBegin.add(SymType.REPEAT);
        factorBegin.add(SymType.IDENTIFIER);
        factorBegin.add(SymType.NUMBER);
        factorBegin.add(SymType.LPAREN);

    }

    public AllPcode getAllPcode(){ return allPcode;}
    public StringBuffer getErrorMessage() { return err.getAllErrorMessage(); }
    private void checkLegal(HashSet<SymType> s1, HashSet<SymType> s2, int n) {
        if(!s1.contains(lex.getType())) {
            err.printError(lex.getLine(),lex.getRow(),n);
            while(!s1.contains(lex.getType()) && !s2.contains(lex.getType()))
                lex.getSYM();
        }
    }
    public Error getErr() {return err;}
    public boolean program() {
        System.out.println(1);
        lex.getSYM();

        HashSet<SymType> tmp = new HashSet<SymType>();
        tmp.addAll(declareBegin);
        tmp.addAll(stateBegin);
        tmp.add(SymType.PERIOD);

        block(0,tmp);
        if(lex.getType()!=SymType.PERIOD) {
            err.printError(lex.getLine(),lex.getRow(),9); //结尾应为句号
        }

        if(err.getErrorTimes() == 0) return true;
        else return false;
    }  //程序判断子程序
    private void block(int lev,HashSet<SymType> begin) {
        System.out.println(2);
        int tx0,cx0;
        allSymbol.setVarPtr(3);
        tx0 = allSymbol.getPtr();

        allSymbol.getAllSymbol().get(tx0).setAddress(allPcode.getPcodePtr());
        allPcode.gen(Operator.JMP,0,0);

        if(lev>3) {
            err.printError(lex.getLine(),lex.getRow(),32); //嵌套层数
        }

        if(lex.getType() == SymType.CONST) {
            lex.getSYM();
            constDeclaration(lev);
            while(lex.getType() == SymType.COMMA) {
                lex.getSYM();
                constDeclaration(lev);
            }
            if(lex.getType() == SymType.SEMICOLON)
                lex.getSYM();
            else
                err.printError(lex.getLine(),lex.getRow(),5);
        }
        if(lex.getType() == SymType.VAR) {
            lex.getSYM();
            varDeclaration(lev);
            while(lex.getType() == SymType.COMMA) {
                lex.getSYM();
                varDeclaration(lev);
            }
            if(lex.getType() == SymType.SEMICOLON)
                lex.getSYM();
            else
                err.printError(lex.getLine(),lex.getRow(),5);
        }
        while(lex.getType() == SymType.PROCEDURE) {
            lex.getSYM();
            if(lex.getType() == SymType.IDENTIFIER) {
                allSymbol.enterProc(lex.getId(),lev);
                lex.getSYM();
            }
            else
                err.printError(lex.getLine(),lex.getRow(),4);
            if(lex.getType() == SymType.SEMICOLON)
                lex.getSYM();
            else
                err.printError(lex.getLine(),lex.getRow(),5);
            HashSet<SymType> s1 = new HashSet<SymType>();
            s1.addAll(begin);
            s1.add(SymType.SEMICOLON);
            block(lev+1,s1);

            if(lex.getType() == SymType.SEMICOLON) {
                lex.getSYM();
                HashSet<SymType> s2 = new HashSet<SymType>();
                s2.addAll(stateBegin);
                s2.add(SymType.IDENTIFIER);
                s2.add(SymType.PROCEDURE);
                checkLegal(s2,begin,6);
            } else
                err.printError(lex.getLine(),lex.getRow(),5);
        }
        HashSet<SymType> s1 = new HashSet<SymType>();
        s1.addAll(stateBegin);
        s1.add(SymType.IDENTIFIER);
        checkLegal(s1,declareBegin,7);



        cx0 = allPcode.getPcodePtr();
        allPcode.getAllPcode().get(allSymbol.getAllSymbol().get(tx0).getAddress()).setA(cx0);
        allSymbol.getAllSymbol().get(tx0).setAddress(cx0);
        allPcode.gen(Operator.INT,0,allSymbol.getVarPtr());

        HashSet<SymType> s2 = new HashSet<SymType>();
        s2.addAll(begin);
        s2.add(SymType.SEMICOLON);
        s2.add(SymType.END);
        statement(lev,s2);
        allPcode.gen(Operator.OPR,0,0);

        HashSet<SymType> s3 = new HashSet<SymType>();
        checkLegal(begin,s3,8);

    }    //分程序判断子程序
    private void constDeclaration(int lev) {
        System.out.println(3);
        if(lex.getType() == SymType.IDENTIFIER) {
            lex.getSYM();
            if(lex.getType() == SymType.EQU || lex.getType() == SymType.BECOMES) {
                if(lex.getType() == SymType.BECOMES)
                    err.printError(lex.getLine(),lex.getRow(),1);
                lex.getSYM();
                if(lex.getType() == SymType.NUMBER) {
                    allSymbol.enterConst(lex.getId(),lex.getNUM());
                    lex.getSYM();
                }
                else
                    err.printError(lex.getLine(),lex.getRow(),2);
            } else
                err.printError(lex.getLine(),lex.getRow(),3);
        } else
            err.printError(lex.getLine(),lex.getRow(),4);
    }   //常量声明判断子程序
    private void varDeclaration(int lev) {
        System.out.println(4);
        if(lex.getType() == SymType.IDENTIFIER) {
            allSymbol.enterVar(lex.getId(),lev);
            lex.getSYM();
        } else
            err.printError(lex.getLine(),lex.getRow(),4);
    }    //变量声明判断子程序
    private void statement(int lev,HashSet<SymType> begin) {
        System.out.println(5+" "+lex.getId());
//        System.out.println("statement"+lev + lex.getById());
        int cx1,cx2;
        Symbol by = null;
        if(lex.getType() == SymType.IDENTIFIER) {
            by = allSymbol.getSymbol(lex.getId());
            if(by == null) {
                err.printError(lex.getLine(),lex.getRow(),11); //标识符未说明
            } else if(by.getType() != 2) {
                err.printError(lex.getLine(),lex.getRow(),12); //常量和proc不可赋值
                by = null;
            }
            lex.getSYM();
            if(lex.getType() == SymType.BECOMES) {
                lex.getSYM();
            } else {
                err.printError(lex.getLine(),lex.getRow(),13); //应该为 :=
            }
            expression(lev,begin);
            if( by != null) {
                allPcode.gen(Operator.STO, lev - by.getLevel(), by.getAddress());
            }
        } else if(lex.getType() == SymType.CALL) {
            lex.getSYM();
            if(lex.getType() != SymType.IDENTIFIER)
                err.printError(lex.getLine(),lex.getRow(),14); //应为标识符
            else {
                by = allSymbol.getSymbol(lex.getId());
                if(by == null)
                    err.printError(lex.getLine(),lex.getRow(),11); //标识符未说明
                else {
                    if(by.getType() == 3) {
                        allPcode.gen(Operator.CAL,lev-by.getLevel(),by.getAddress());
                    } else
                        err.printError(lex.getLine(),lex.getRow(),15); //call后应为proc
                }
                lex.getSYM();
            }
        } else if(lex.getType() == SymType.IF) {
            lex.getSYM();

            HashSet<SymType> s1 = new HashSet<SymType>();
            s1.addAll(begin);
            s1.add(SymType.THEN);
            s1.add(SymType.DO);
            condition(lev,s1);

            if(lex.getType() == SymType.THEN) {
                lex.getSYM();
            } else
                err.printError(lex.getLine(),lex.getRow(),16); //条件语句缺失then
            cx1 = allPcode.getPcodePtr();
            allPcode.gen(Operator.JPC,0,0);
            HashSet<SymType> s2 = new HashSet<SymType>();
            s2.addAll(begin);
            s2.add(SymType.ELSE);
            statement(lev,s2);


            if(lex.getType() == SymType.ELSE) {
                lex.getSYM();
                cx2 = allPcode.getPcodePtr();
                allPcode.gen(Operator.JMP,0,0);
                allPcode.getAllPcode().get(cx1).setA(allPcode.getPcodePtr());
                statement(lev,begin);
                allPcode.getAllPcode().get(cx2).setA(allPcode.getPcodePtr());
            } else {
                allPcode.getAllPcode().get(cx1).setA(allPcode.getPcodePtr());
            }
        } else if(lex.getType() == SymType.BEGIN) {
            lex.getSYM();

            HashSet<SymType> s1 = new HashSet<SymType>();
            s1.addAll(begin);
            s1.add(SymType.SEMICOLON);
            s1.add(SymType.END);
            statement(lev,s1);

            HashSet<SymType> s2 = new HashSet<SymType>();
            s2.addAll(stateBegin);
            s2.add(SymType.SEMICOLON);
            while(s2.contains(lex.getType())) {
                if(lex.getType() == SymType.SEMICOLON)
                    lex.getSYM();
                else
                    err.printError(lex.getLine(),lex.getRow(),10);
                statement(lev,s1);
            }
            if(lex.getType() == SymType.END) {
                lex.getSYM();
            } else {
                err.printError(lex.getLine(),lex.getRow(),17); //缺少end
            }
        } else if(lex.getType() == SymType.WHILE) {
            cx1 = allPcode.getPcodePtr();
            lex.getSYM();

            HashSet<SymType> s1 = new HashSet<SymType>();
            s1.addAll(begin);
            s1.add(SymType.DO);
            condition(lev,s1);
            cx2 = allPcode.getPcodePtr();
            allPcode.gen(Operator.JPC,0,0);
            if(lex.getType() == SymType.DO) {
                lex.getSYM();
            } else {
                err.printError(lex.getLine(),lex.getRow(),18); //while中缺少do
            }
            statement(lev,begin);
            allPcode.gen(Operator.JMP,0,cx1);
            allPcode.getAllPcode().get(cx2).setA(allPcode.getPcodePtr());
        } else if(lex.getType() ==  SymType.READ) {
            lex.getSYM();
            if(lex.getType() == SymType.LPAREN) {
                do {
                    lex.getSYM();
                    if(lex.getType() == SymType.IDENTIFIER) {
                        by = allSymbol.getSymbol(lex.getId());
                        if(by == null) {
                            err.printError(lex.getLine(),lex.getRow(),11); //标识符未说明
                        } else {
                            if(by.getType() != 2) {
                                err.printError(lex.getLine(),lex.getRow(),12);//不可以向常量和proc赋值
                                by = null;
                            } else {
                                allPcode.gen(Operator.RED,lev-by.getLevel(),by.getAddress());
                            }
                        }
                    } else {
                        err.printError(lex.getLine(),lex.getRow(),28); //read语句内不是标识符
                    }
                    lex.getSYM();
                } while(lex.getType() == SymType.COMMA);
            } else
                err.printError(lex.getLine(),lex.getRow(),40); //缺少左括号
            if(lex.getType() != SymType.RPAREN) {
                err.printError(lex.getLine(),lex.getRow(),22); //缺少右括号
            }
            lex.getSYM();
        } else if(lex.getType() == SymType.WRITE) {
            lex.getSYM();
            if(lex.getType() == SymType.LPAREN) {
                    HashSet<SymType> s1 = new HashSet<SymType>();
                    s1.addAll(begin);
                    s1.add(SymType.RPAREN);
                    s1.add(SymType.COMMA);
                    do {
                        lex.getSYM();
                        expression(lev,s1);
                        allPcode.gen(Operator.WRT,0,0);
                    } while(lex.getType() == SymType.COMMA);

                if(lex.getType() != SymType.RPAREN) {
                    err.printError(lex.getLine(),lex.getRow(),22); //缺少右括号
                }
                lex.getSYM();
            } else
                err.printError(lex.getLine(),lex.getRow(),40); //缺少左括号
        } else if(lex.getType() == SymType.REPEAT) {
            cx1 = allPcode.getPcodePtr();
            lex.getSYM();
            HashSet<SymType> s1 = new HashSet<SymType>();
            s1.addAll(begin);
            s1.add(SymType.SEMICOLON);
            s1.add(SymType.UNTIL);
            statement(lev,s1);

            HashSet<SymType> s2 = new HashSet<SymType>();
            s2.add(SymType.BEGIN);
            s2.add(SymType.CALL);
            s2.add(SymType.IF);
            s2.add(SymType.WHILE);
            s2.add(SymType.SEMICOLON);

            while(s2.contains(lex.getType())) {
                if(lex.getType()== SymType.SEMICOLON)
                    lex.getSYM();
                else
                    err.printError(lex.getLine(),lex.getRow(),5);
                HashSet<SymType> s3 = new HashSet<SymType>();
                s3.addAll(begin);
                s3.add(SymType.SEMICOLON);
                s3.add(SymType.UNTIL);
                statement(lev,s3);
            }
            if(lex.getType() == SymType.UNTIL) {
                lex.getSYM();
                condition(lev,begin);
                allPcode.gen(Operator.JPC,0,cx1);
            } else {
                err.printError(lex.getLine(),lex.getRow(),25); //缺少until
            }
        }
        HashSet<SymType> test = new HashSet<SymType>();
        checkLegal(begin,test,19);
    }    //语句判断子程序
    private void expression(int lev,HashSet<SymType> begin) {
        //<表达式> ::= [+|-]<项>{<加法运算符><项>}
        SymType ss = null;
        HashSet<SymType> s1 = new HashSet<SymType>();
        s1.addAll(begin);
        s1.add(SymType.PLUS);
        s1.add(SymType.MINUS);

        if(lex.getType() == SymType.PLUS || lex.getType() == SymType.MINUS) {
            ss = lex.getType();
            lex.getSYM();
            term(lev,s1);
            if (ss == SymType.MINUS) {
                allPcode.gen(Operator.OPR, 0, 1);
            }
        } else
            term(lev,s1);
        while(lex.getType() == SymType.PLUS || lex.getType() == SymType.MINUS) {
            ss = lex.getType();
            lex.getSYM();
            term(lev,s1);
            if(ss == SymType.PLUS) {
                allPcode.gen(Operator.OPR,0,2);
            } else {
                allPcode.gen(Operator.OPR,0,3);
            }
        }
    }  //表达式判断子程序
    private void term(int lev,HashSet<SymType> begin) {

        HashSet<SymType> s1 =new HashSet<SymType>();
        s1.addAll(begin);
        s1.add(SymType.TIMES);
        s1.add(SymType.SLASH);

        SymType by = null;
        factor(lev,s1);
        while(lex.getType() == SymType.TIMES || lex.getType() == SymType.SLASH) {
            by = lex.getType();
            lex.getSYM();
            factor(lev,s1);
            if(by == SymType.TIMES)
                allPcode.gen(Operator.OPR,0,4);
            else
                allPcode.gen(Operator.OPR,0,5);
        }
    }    //项判断子程序
    private void factor(int lev,HashSet<SymType> begin) {
        Symbol by = null;
        checkLegal(factorBegin,begin,24);
        while(factorBegin.contains(lex.getType())) {
            if (lex.getType() == SymType.IDENTIFIER) {
                by = allSymbol.getSymbol(lex.getId());
                if (by == null) {
                    err.printError(lex.getLine(), lex.getRow(), 11);
                } else {
                    if (by.getType() == 1) {
                        allPcode.gen(Operator.LIT, 0, by.getValue());
                    } else if (by.getType() == 2) {
                        allPcode.gen(Operator.LOD, lev - by.getLevel(), by.getAddress());
                    } else {
                        err.printError(lex.getLine(), lex.getRow(), 21); //表达式内有proc
                    }
                }
                lex.getSYM();
            } else if (lex.getType() == SymType.NUMBER) {
                if (lex.getNUM() > Integer.MAX_VALUE) {
                    err.printError(lex.getLine(), lex.getRow(), 30);
                    lex.setNUM(0);
                }
                allPcode.gen(Operator.LIT, 0, lex.getNUM());
                lex.getSYM();
            } else if (lex.getType() == SymType.LPAREN) {
                lex.getSYM();

                HashSet<SymType> s2 = new HashSet<SymType>();
                s2.addAll(begin);
                s2.add(SymType.RPAREN);
                expression(lev,s2);
                if (lex.getType() == SymType.RPAREN) {
                    lex.getSYM();
                } else {
                    err.printError(lex.getLine(), lex.getRow(), 22);
                }
            }
            HashSet<SymType> test = new HashSet<SymType>();
            test.add(SymType.LPAREN);
            checkLegal(begin,test,23);
        }
    }   //因子判断子程序
    private void condition(int lev,HashSet<SymType> begin) {
        SymType by = null;
        if(lex.getType() == SymType.OOD) {
            lex.getSYM();
            expression(lev,begin);
            allPcode.gen(Operator.OPR,0,6);
        } else {
            HashSet<SymType> s1 = new HashSet<SymType>();
            s1.addAll(begin);
            s1.add(SymType.EQU);
            s1.add(SymType.NEQ);
            s1.add(SymType.GTR);
            s1.add(SymType.LEQ);
            s1.add(SymType.LES);
            s1.add(SymType.GEQ);
            expression(lev,s1);

            s1.clear();
            s1.add(SymType.EQU);
            s1.add(SymType.NEQ);
            s1.add(SymType.GTR);
            s1.add(SymType.LEQ);
            s1.add(SymType.LES);
            s1.add(SymType.GEQ);

            if(!s1.contains(lex.getType())) {
                err.printError(lex.getLine(),lex.getRow(),20);
            } else {
                by = lex.getType();
                lex.getSYM();
                expression(lev,begin);
                if (by == SymType.EQU) {
                    allPcode.gen(Operator.OPR, 0, 8);
                } else if (by == SymType.NEQ) {
                    allPcode.gen(Operator.OPR, 0, 9);
                } else if (by == SymType.LES) {
                    allPcode.gen(Operator.OPR, 0, 10);
                } else if (by == SymType.GEQ) {
                    allPcode.gen(Operator.OPR, 0, 11);
                } else if (by == SymType.GTR) {
                    allPcode.gen(Operator.OPR, 0, 12);
                } else if (by == SymType.LEQ) {
                    allPcode.gen(Operator.OPR, 0, 13);
                }
            }
        }
    } //条件判断子程序
}
