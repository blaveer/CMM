package com.company.Tools;

public class Token {
    // token类型
    private String kind;
    // token所在行
    private int line;
    // token所在列
    private int culomn;
    // token内容
    private String content;

    public Token(int l, int c, String k, String con) {
        this.line = l;  //行
        this.culomn = c; //列
        this.kind = k;   //Token的种类
        this.content = con;//内容
    }

    @Override
    public String toString(){
        return "第"+line+"行，第"+culomn+"列有一个"+kind+"类型的Token,内容是："+content;
    }

    public String getKind(){return kind;}
    public String getContent(){return content;}
    public int getLine(){return line;}
    public int getCulomn(){return culomn;}
}
