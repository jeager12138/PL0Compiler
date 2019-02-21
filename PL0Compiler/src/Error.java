public class Error {
    private String[] errorMessage = {
        "常数说明中应是'='而不是':='",
        "常数说明中'='后应为数字",
        "常数说明中标识符后应为'='",
        "const,var,procedure后应为标识符",
        "漏掉逗号或分号",
        "过程说明后的符号不正确",
        "应为语句开始符号",
        "程序体内语句部分后的符号不正确",
        "程序结尾应为句号",
        "语句之间漏了分号",
        "标识符未说明",
        "不可向常量或过程赋值",
        "赋值语句中应为赋值运算符':='",
        "call后应为标识符",
        "call后标识符属性应为过程,不可调用常量或变量",
        "条件语句中缺失then",
        "应为分号或end",
        "while型循环语句中缺失do",
        "语句后的符号不正确",
        "应为关系运算符",
        "表达式内不可有过程标识符",
        "缺失右括号",
        "因子后不可为此符号",
        "表达式不能以此符号开始",
        "repeat循环语句中缺失until",
        "代码太长，无法编译",
        "RuntimeError，地址偏移越界",
        "Read语句括号内不是标识符",
        "这个数太大，超过INT32_MAX",
        "缺失左括号"
    };
    private int errorTimes = 0;
    private StringBuffer allErrorMessage;
    Error() { allErrorMessage = new StringBuffer();}

    public void printError(int line,int row,int ErrorId)  {
        errorTimes++;
        allErrorMessage.append("error"+ ErrorId +" happened in ("+line+","+row+"): "+errorMessage[ErrorId-1]+"\n");
    }

    public int getErrorTimes() { return errorTimes; }
    public StringBuffer getAllErrorMessage() { return allErrorMessage; }
}
