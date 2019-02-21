import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AllSymbol {
    List<Symbol> allSymbol;

    private int con=1;              //常量类型用1表示
    private int var=2;              //变量类型用2表示
    private int proc=3;             //过程类型用3表示

    private int ptr = 0;
    private int varPtr;

    public AllSymbol() {
        allSymbol = new ArrayList<Symbol>();
        for(int i=0;i<200;i++)
            allSymbol.add(new Symbol(0,0,0,0,""));
    }

    //向符号表中插入常量
    public void enterConst(String name, int value) {
        ptr++;
        allSymbol.get(ptr).setAll(con,value,0,-1,name);
//        allSymbol.add(new Symbol(con,value,0,0,name));

     //   System.out.println("const name:"+ name +" value:"+value);
    }
    //向符号表中插入变量
    public void enterVar(String name, int level) {
//        allSymbol.add(new Symbol(var,0,level,varPtr++,name));
        ptr++;
        allSymbol.get(ptr).setAll(var,0,level,varPtr,name);
        varPtr++;
    //    System.out.println("var name:"+ name +" adr:"+ (varPtr-1));
    }
    //向符号表中插入过程
    public void enterProc(String name, int level) {
        ptr++;
//        allSymbol.add(new Symbol(proc,0,level,0,name));
        allSymbol.get(ptr).setAll(proc,0,level,-1,name);
    //    System.out.println("proc name:"+name+"level:"+level);
    }
    public void setVarPtr(int _varPtr) { varPtr = _varPtr; }
    //按名称查找变量
    public Symbol getSymbol(String name) {
        for (int i = ptr; i >= 1; i--) {
            if (allSymbol.get(i).getName().equals(name)) {

                //System.out.println(allSymbol.get(i).getName()+" "+allSymbol.get(i).getValue()+" "+allSymbol.get(i).getAddress());
                return allSymbol.get(i);
            }
        }
        return null;
    }
    public List<Symbol> getAllSymbol() {
        return allSymbol;
    }
    public void setPtr(int _ptr) {
        ptr = _ptr;
    }
    public int getPtr() {
        return ptr;
    }
    public int getVarPtr() {return varPtr;}

}

