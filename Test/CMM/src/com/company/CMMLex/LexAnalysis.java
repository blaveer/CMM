package com.company.CMMLex;

import com.company.Tools.ReservedWord;
import com.company.Tools.Token;

import java.util.ArrayList;

public class LexAnalysis {
    // 注释的标志，主要是为了多行注释准备的
    private boolean isNotation = false;
    // 错误个数
    private int errorNum = 0;
    // 错误信息
    private String errorInfo = "";

    // 分析后得到的tokens集合，用于其后的语法及语义分析
    private ArrayList<Token> tokens = new ArrayList<Token>();

    // 分析后得到的所有tokens集合，包含注释、空格等
    private ArrayList<Token> displayTokens = new ArrayList<Token>();

    public void lex(String cmmText){
        String[] lexSplit=cmmText.split("\n");
        //TODO /r也要解决 String[] lexText=new String[]();
        for(int counter=0;counter<lexSplit.length;counter++){
            if(lexSplit[counter]==null){
                displayTokens.add(new Token(counter+1,0,"分隔符","\n"));
                continue;
            }
            if(isNotation&&(!lexSplit[counter].contains("*/"))){
                displayTokens.add(new Token(counter+1,0,"annotation",lexSplit[counter]));
                displayTokens.add(new Token(counter+1,lexSplit[counter].length(),"分隔符","\n"));
            }
            else{
                lexLine(lexSplit[counter],counter+1);
            }
        }
        if(isNotation){
            System.out.println("请仔细检查程序，中间有未结束的多行注释");
        }
    }
    private void lexLine(String lineText,int line){
        int index=0;//标识第几个字符
        char ch;
        lineText=lineText+"\n";//这么做的目的是为了防止下面在++的溢出
        if(lineText.contains("*/")&&isNotation){
            String[] temp=lineText.split("\\*/");
            displayTokens.add(new Token(line,0,"annotation",temp[0]));
            displayTokens.add(new Token(line,temp[0].length(),"分隔符","*/"));
            isNotation=false;
            if(temp[0]!=null){
                index=temp[0].length()+2;
            }else{
                index=2;
            }
        }
        //index的值从非注释地方开始

        ch= lineText.charAt(index);
        while(index<lineText.length()){
            if(ch==';'||ch==','){
                displayTokens.add(new Token(line,index+1,"分隔符",String.valueOf(ch)));
                tokens.add(new Token(line,index+1,"分隔符",String.valueOf(ch)));
                index++;
                ch= lineText.charAt(index);
            }
            else if(ch==' '||ch=='\t'||ch=='\r'){
                displayTokens.add(new Token(line,index+1,"分隔符",String.valueOf(ch)));
                index++;
                ch= lineText.charAt(index);
            }
            else if(ch=='('||ch==')'||ch=='['||ch==']'||ch=='{'||ch=='}'){
                if(ch=='('){
                    index++;
                    ch= lineText.charAt(index);
                    if(ch=='-'){//理论上来时所有的负数都要有小括号括起来
                        displayTokens.add(new Token(line,index,"LLB","("));
                        int start=index;  //从这个负号开始
                        int end;
                        boolean err=false;
                        index++;
                        ch= lineText.charAt(index);
                        while (true){
                            if(ch=='\n'){
                                err=true;
                                end=lineText.length()-1;
                                break;
                            }else if(ch==')'){
                                index++;
                                ch= lineText.charAt(index);
                                end=index;
                                break;
                            }else{
                                index++;
                                ch= lineText.charAt(index);
                            }
                        }
                        String tempToken=lineText.substring(start,end-1);
                        if(err){
                            displayTokens.add(new Token(line,start,"错误",tempToken));
                            errorNum++;
                            String error="在第"+String.valueOf(line)+"第"+start+"处有不合法的数\n";
                            errorInfo=errorInfo+error;
                        }
                        else{
                            if(matchInteger(tempToken)){
                                displayTokens.add(new Token(line,start,"整数",tempToken));
                                tokens.add(new Token(line,start,"整数",tempToken));
                            }else if(matchReal(tempToken)){
                                displayTokens.add(new Token(line,start,"实数",tempToken));
                                tokens.add(new Token(line,start,"实数",tempToken));
                            }
                            else{
                                displayTokens.add(new Token(line,start,"错误",tempToken));
                                errorNum++;
                                String error="在第"+String.valueOf(line)+"第"+start+"处有不合法的数\n";
                                errorInfo=errorInfo+error;
                            }
                        }
                        displayTokens.add(new Token(line,index,"RLB",")"));
                    }
                    else{
                        displayTokens.add(new Token(line,index+1,"LLB","("));
                        tokens.add(new Token(line,index+1,"LLB","("));
                    }
                }else if(ch==')'){
                    //displayTokens.add(new Token(line,index+1,"RLB","）"));
                    displayTokens.add(new Token(line,index+1,"RLB",")"));
                    tokens.add(new Token(line,index+1,"RLB",")"));
                    index++;
                    ch= lineText.charAt(index);
                }else if(ch=='['){
                    displayTokens.add(new Token(line,index+1,"LMB","["));
                    tokens.add(new Token(line,index+1,"LMB","["));
                    index++;
                    ch= lineText.charAt(index);
                }else if(ch==']'){
                    displayTokens.add(new Token(line,index+1,"RMB","]"));
                    tokens.add(new Token(line,index+1,"RMB","]"));
                    index++;
                    ch= lineText.charAt(index);
                }else if(ch=='{'){
                    displayTokens.add(new Token(line,index+1,"LBB","{"));
                    tokens.add(new Token(line,index+1,"LBB","{"));
                    index++;
                    ch= lineText.charAt(index);
                }else if(ch=='}'){
                    displayTokens.add(new Token(line,index+1,"RBB","}"));
                    tokens.add(new Token(line,index+1,"RBB","}"));
                    index++;
                    ch= lineText.charAt(index);
                }

            }
            else if(isLetter(ch)){
                int start=index;
                int end;
                index++;
                ch= lineText.charAt(index);
                while(true){
                    if(isLetter(ch)||isDigit(ch)){
                        index++;
                        ch= lineText.charAt(index);
                    }
                    else{
                        end=index;
                        break;
                    }
                }
                String tempToken=lineText.substring(start,end);
                if(isKey(tempToken)){
                    tokens.add(new Token(line,start+1,"保留字",tempToken));
                    displayTokens.add(new Token(line,start+1,"保留字",tempToken));
                }else if(matchID(tempToken)){
                    tokens.add(new Token(line,start+1,"标识符",tempToken));
                    displayTokens.add(new Token(line,start+1,"标识符",tempToken));
                }else{
                    displayTokens.add(new Token(line ,start+1,"错误",tempToken));
                    errorNum++;
                    String error="在第"+String.valueOf(line)+"第"+(start+1)+"处有不合法的标识符\n";
                    errorInfo=errorInfo+error;
                }
            }
            else if(isDigit(ch)){
                boolean allIsNum=true;
                boolean hasDot=false;
                int start=index;
                int end;
                index++;
                ch= lineText.charAt(index);
                while (true){
                    if(isDigit(ch)){
                        index++;
                        ch=lineText.charAt(index);
                    }else if(isLetter(ch)){
                        index++;
                        ch= lineText.charAt(index);
                        allIsNum=false;
                    }else if(ch=='.'){
                        index++;
                        ch= lineText.charAt(index);
                        hasDot=true;
                    } else{
                        end=index;
                        break;
                    }
                }
                String tempToken=lineText.substring(start,end);
                if(allIsNum){
                    if(hasDot){
                        if(matchReal(tempToken)){
                            tokens.add(new Token(line,start+1,"实数",tempToken));
                            displayTokens.add(new Token(line,start+1,"实数",tempToken));
                        }
                        else{
                            displayTokens.add(new Token(line,start+1,"错误",tempToken));
                            errorNum++;
                            String error="在第"+String.valueOf(line)+"第"+(start+1)+"处有不合法的实数\n";
                            errorInfo=errorInfo+error;
                        }
                    }
                    else{
                        if(matchInteger(tempToken)){
                            tokens.add(new Token(line,start+1,"整数",tempToken));
                            displayTokens.add(new Token(line,start+1,"整数",tempToken));
                        }
                        else{
                            displayTokens.add(new Token(line,start+1,"错误",tempToken));
                            errorNum++;
                            String error="在第"+String.valueOf(line)+"第"+(start+1)+"处有不合法的整数\n";
                            errorInfo=errorInfo+error;
                        }
                    }
                }
                else{
                    displayTokens.add(new Token(line ,start+1,"错误",tempToken));
                    errorNum++;
                    String error="在第"+String.valueOf(line)+"第"+(start+1)+"处有不合法的标识符\n";
                    errorInfo=errorInfo+error;
                }
            }
            else if(ch=='/'){
                int start=index;
                int end;
                index++;
                ch= lineText.charAt(index);
                if(ch=='/'){
                    displayTokens.add(new Token(line,start+1,"分隔符","//"));
                    start=start+2;
                    end=lineText.length()-1;
                    String tempToken=lineText.substring(start,end);
                    displayTokens.add(new Token(line,start+1,"annotation",tempToken));
                    displayTokens.add(new Token(line,lineText.length(),"分隔符","\n"));
                    break;//直接结束整个while
                }
                else if(ch=='*'){
                    start=start+2;
                    index++;
                    ch= lineText.charAt(index);
                    isNotation=true;
                    displayTokens.add(new Token(line,start+1,"分隔符","/*"));
                    while (true){
                        if(ch=='\n'){
                            end=lineText.length()-1;
                            displayTokens.add(new Token(line,lineText.length(),"分隔符","\n"));
                            break;
                        }
                        else if(ch=='*'){
                            index++;
                            ch= lineText.charAt(index);
                            if(ch=='/'){
                                displayTokens.add(new Token(line,start+1,"分隔符","*/"));
                                isNotation=false;
                                end=index-1;
                                index++;
                                ch= lineText.charAt(index);
                                break;
                            }else if(ch=='\n'){
                                end=lineText.length()-1;
                                break;
                            }
                            else{
                                index++;
                                ch= lineText.charAt(index);
                            }
                        }
                        else{
                            index++;
                            ch= lineText.charAt(index);
                        }
                    }
                    String tempToken=lineText.substring(start,end);
                    if(isNotation){
                        displayTokens.add(new Token(line,start+1,"annotation",tempToken));
                        break;
                    }
                    else{
                        displayTokens.add(new Token(line,start+1,"annotation",tempToken));
                    }
                }
                else{
                    tokens.add(new Token(line,index,"运算符","/"));
                    displayTokens.add(new Token(line,index,"运算符","/"));
                }
            }
            else if(ch=='*'||ch=='+'||ch=='-'){
                tokens.add(new Token(line,index+1,"运算符",String.valueOf(ch)));
                displayTokens.add(new Token(line,index+1,"运算符",String.valueOf(ch)));
                index++;
                ch= lineText.charAt(index);
            }
            else if(ch=='"'){
                int start=index+1;
                int end;
                boolean err=false;
                index++;
                ch= lineText.charAt(index);
                while(true){
                    if(ch=='"'){
                        end=index;
                        index++;
                        ch= lineText.charAt(index);
                        break;
                    }else if(ch=='\n'){
                        end=lineText.length()-1;
                        err=true;
                        break;
                    }else{
                        index++;
                        ch= lineText.charAt(index);
                    }
                }
                String tempToken=lineText.substring(start,end);
                if(err){
                    displayTokens.add(new Token(line,start,"错误",tempToken));
                    errorNum++;
                    String error="在第"+String.valueOf(line)+"第"+start+"处有未结束的字符串\n";
                    errorInfo=errorInfo+error;
                    break;
                }else{
                    tokens.add(new Token(line,start-1,"字符串","\""));
                    displayTokens.add(new Token(line,start-1,"字符串","\""));
                    tokens.add(new Token(line,start,"字符串",tempToken));
                    displayTokens.add(new Token(line,start,"字符串",tempToken));
                    tokens.add(new Token(line,end,"字符串","\""));
                    displayTokens.add(new Token(line,end,"字符串","\""));
                }
            }
            else if(ch=='='){
                index++;
                ch= lineText.charAt(index);
                if(ch=='='){
                    tokens.add(new Token(line,index+1,"运算符","=="));
                    displayTokens.add(new Token(line,index+1,"运算符","=="));
                    index++;
                    ch= lineText.charAt(index);
                }else{
                    tokens.add(new Token(line,index,"运算符","="));
                    displayTokens.add(new Token(line,index,"运算符","="));
                }
            }
            else if(ch=='>'){
                tokens.add(new Token(line,index,"运算符",">"));
                displayTokens.add(new Token(line,index,"运算符",">"));
                index++;
                ch= lineText.charAt(index);
            }
            else if(ch=='<'){
                index++;
                ch= lineText.charAt(index);
                if(ch=='>'){
                    tokens.add(new Token(line,index+1,"运算符","<>"));
                    displayTokens.add(new Token(line,index+1,"运算符","<>"));
                    index++;
                    ch= lineText.charAt(index);
                }else{
                    tokens.add(new Token(line,index,"运算符","<"));
                    displayTokens.add(new Token(line,index,"运算符","<"));
                }
            }
            else if(ch=='\n'){
                displayTokens.add(new Token(line,index+1,"分隔符","\n"));
                break;
            }
            else if(ch=='!'){
                tokens.add(new Token(line,index,"运算符","!"));
                displayTokens.add(new Token(line,index,"运算符","!"));
                index++;
                ch=lineText.charAt(index);
            }
            else if(ch=='&'){
                index++;
                ch=lineText.charAt(index);
                if(ch=='&'){
                    tokens.add(new Token(line,index+1,"运算符","&&"));
                    displayTokens.add(new Token(line,index+1,"运算符","&&"));
                    index++;
                    ch= lineText.charAt(index);
                }
                else{
                    displayTokens.add(new Token(line ,index+1,"错误",String.valueOf(ch)));
                    errorNum++;
                    String error="在第"+String.valueOf(line)+"第"+(index)+"处有两个不合法的字符\n";
                    index++;
                    ch=lineText.charAt(index);
                }
            }
            else if(ch=='|'){
                index++;
                ch=lineText.charAt(index);
                if(ch=='|'){
                    tokens.add(new Token(line,index+1,"运算符","||"));
                    displayTokens.add(new Token(line,index+1,"运算符","||"));
                    index++;
                    ch= lineText.charAt(index);
                }
                else{
                    displayTokens.add(new Token(line ,index+1,"错误",String.valueOf(ch)));
                    errorNum++;
                    String error="在第"+String.valueOf(line)+"第"+(index)+"处有两个不合法的字符\n";
                    index++;
                    ch=lineText.charAt(index);
                }
            }
            else if(ch=='#'){
                tokens.add(new Token(line,index,"关键字","#"));
                displayTokens.add(new Token(line,index,"关键字","#"));
                index++;
                ch=lineText.charAt(index);
            }
            else{
                displayTokens.add(new Token(line ,index+1,"错误",String.valueOf(ch)));
                errorNum++;
                String error="在第"+String.valueOf(line)+"第"+(index+1)+"处有不合法的字符\n";
                errorInfo=errorInfo+error;
                index++;
                ch= lineText.charAt(index);
            }
        }

    }

    //下划线也包括在字母里面了
    private static boolean isLetter(char c) {
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_')
            return true;
        return false;
    }

    /**
     * 识别数字
     *
     * @param c
     *            要识别的字符
     * @return
     */
    private static boolean isDigit(char c) {
        if (c >= '0' && c <= '9')
            return true;
        return false;
    }

    //识别正确的整数：排除多个零的情况
    private static boolean matchInteger(String input) {
        if (input.matches("^-?\\d+$") && !input.matches("^-?0{1,}\\d+$"))
            return true;
        else
            return false;
    }

    // 识别正确的浮点数：排除00.000的情况
    private static boolean matchReal(String input) {
        if (input.matches("^(-?\\d+)(\\.\\d+)+$")
                && !input.matches("^(-?0{2,}+)(\\.\\d+)+$"))
            return true;
        else
            return false;
    }

    //识别正确的标识符：有字母、数字、下划线组成，必须以字母开头，不能以下划线结尾
    private static boolean matchID(String input) {
        if (input.matches("^\\w+$") && !input.endsWith("_") //两个斜杠是因为在“”中要转义
                && input.substring(0, 1).matches("[A-Za-z]"))
            return true;
        else
            return false;
    }

    /**
     * 识别保留字
     *
     * @param str 要分析的字符串
     * @return 布尔值
     */
    private static boolean isKey(String str) {
        if (str.equals(ReservedWord.IF) || str.equals(ReservedWord.ELSE)
                || str.equals(ReservedWord.WHILE) || str.equals(ReservedWord.READ)
                || str.equals(ReservedWord.WRITE) || str.equals(ReservedWord.INT)
                || str.equals(ReservedWord.REAL) || str.equals(ReservedWord.BOOL)
                || str.equals(ReservedWord.STRING) || str.equals(ReservedWord.TRUE)
                || str.equals(ReservedWord.FALSE) || str.equals(ReservedWord.FOR)
                ||str.equals(ReservedWord.BREAK)||str.equals(ReservedWord.CONTINUE)
                ||str.equals(ReservedWord.VOID)||str.equals(ReservedWord.RETURN))

               // ||str.equals(ReservedWord.CHAR))
            return true;
        return false;
    }

    //region getset方法
    public boolean isNotation() {
        return isNotation;
    }

    public void setNotation(boolean notation) {
        isNotation = notation;
    }

    public int getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(int errorNum) {
        this.errorNum = errorNum;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public void getTokens(int a){
        for(int i =0;i<tokens.size();i++){
            System.out.println(tokens.get(i).toString());
        }
    }
    public void setTokens(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public ArrayList<Token> getDisplayTokens() {
        return displayTokens;
    }

    public void setDisplayTokens(ArrayList<Token> displayTokens) {
        this.displayTokens = displayTokens;
    }
    //endregion
    public void outToken(){
        for(int i=0;i<tokens.size();i++){
            System.out.println(tokens.get(i).getKind()+"    "+tokens.get(i).getContent());
        }
    }

    public String getToken(){
        String r="类型    内容\n";
        for(int i=0;i<tokens.size();i++){
            r=r+tokens.get(i).getKind()+"   "+tokens.get(i).getContent()+"\n";
        }
        return r;
    }
    public void outAllToken(){
        for(int i=0;i<displayTokens.size();i++){
            System.out.println(displayTokens.get(i).getKind()+"    "+displayTokens.get(i).getContent());
        }
    }
}
