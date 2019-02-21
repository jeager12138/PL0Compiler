public class Symbol {
    private int type;           //表示常量、变量或过程
    private int value;          //表示常量或变量的值
    private int level;          //嵌套层次
    private int address;        //相对于所在嵌套过程基地址的地址
    private String name;        //变量、常量或过程名

    public Symbol(int _type,int _value ,int _level, int _address,String _name) {
        //专为变量声明和过程声明写的构造函数
        //变量和过程声明时没有初始值
        value = _value;
        type = _type;
        level = _level;
        address = _address;
        name = _name;
    }
    public void setAll(int _type,int _value,int _level,int _address,String _name) {
        type = _type;
        value = _value;
        level = _level;
        address = _address;
        name = _name;
    }

    public void setAddress(int _address) {
        address = _address;
    }

    public int  getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public int getLevel() {
        return level;
    }

    public int getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }
}
