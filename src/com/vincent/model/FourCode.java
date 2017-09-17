package com.vincent.model;

/**
 * 
 * 四元式
 */
public class FourCode{
    /**
     * jmp 条件  null 目标  是条件为假时跳转到目标
     * jmp null null 目标  无条件跳转到目标, 超过语句数,则程序结束
     * assign 元素 null 目标
     * int/real null 元素个数/null 变量名
     * read/write null null 元素
     * in null null null 进入语句块
     * out null null null 出语句块
     * assign 值 null 目标
     * +
     * -
     * *
     * /
     */
    public static final String JMP = "jmp";
    public static final String READ = "read";
    public static final String WRITE = "write";
    public static final String IN = "in";
    public static final String OUT = "out";
    public static final String INT = "int";
    public static final String REAL = "real";
    public static final String ASSIGN = "assign";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String MUL = "*";
    public static final String DIV = "/";
    public static final String GT = ">";
    public static final String LT = "<";
    public static final String GET = ">=";
    public static final String LET = "<=";
    public static final String EQ = "==";
    public static final String NEQ = "<>";
    
    private String first;
    private String second;
    private String third;
    private String forth;
    
    
    public FourCode(String first, String second, String third, String forth) {
        super();
        this.first = first;
        this.second = second;
        this.third = third;
        this.forth = forth;
    }

    

    public String getFirst() {
        return first;
    }



    public void setFirst(String first) {
        this.first = first;
    }



    public String getSecond() {
        return second;
    }



    public void setSecond(String second) {
        this.second = second;
    }



    public String getThird() {
        return third;
    }



    public void setThird(String third) {
        this.third = third;
    }



    public String getForth() {
        return forth;
    }



    public void setForth(String forth) {
        this.forth = forth;
    }



    @Override
    public String toString() {
        return String.format("(%s, %s, %s, %s)", first, second, third, forth);
    }
}
