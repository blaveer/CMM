package com.company.CMMGra;

import com.company.Tools.Token;
import com.company.Tools.TokenTree;
import sun.nio.cs.ext.ISCII91;

import java.util.ArrayList;

import static com.company.Tools.ReservedWord.*;

public class GraAnalysis {
    // 错误个数
    private int errorNum = 0;
    // 错误信息
    private String errorInfo = "";
    // 分析后得到的tokens集合，用于其后的语法及语义分析
    private ArrayList<Token> tokens = new ArrayList<Token>();

    //语法分析的根节点
    public TokenTree Program=new TokenTree("Root","CMM");


    //用来遍历词法分析得到的Token。
    private int counter=0;
    private Token currentToken;

    public GraAnalysis(ArrayList<Token> lexToken){
        this.tokens=lexToken;
    }
    public void gra(){
        for(;counter<tokens.size();){
            Program.children.add(statement());
        }
    }
    /**
    * 语句可进行分类为
     * 声明语句，以那几个关键字开始
     * 赋值语句，以标识符开始
     * 单独的表达式认为是没有实际意义的，认为实错误的
     * for语句
     * if else 语句
     * while语句
     * read语句
     * write语句
    * */
    private TokenTree statement(){
        TokenTree state=null;
        currentToken=tokens.get(counter);
        if(currentToken.getKind()=="标识符"){
            state=assign_stm(false);
        }
        else if(currentToken!=null&&(currentToken.getContent().equals("int")||currentToken.getContent().equals("string")||currentToken.getContent().equals("read")||currentToken.getContent().equals("bool"))){
            state=declare_stm();
        }
        else if(currentToken!=null&&currentToken.getContent().equals("for")){
            state=for_stm();
        }
    }

    private TokenTree for_stm(){
        TokenTree forTemp=new TokenTree("关键字","for");
        //boolean hasElse=false;//是否有else
        boolean hasBB=false;//是否有{}
        //boolean hasElseBB=false;//else 是否有{}
        counter++;
        currentToken=tokens.get(counter);
        if(currentToken.getKind().equals("LLB")){
            counter++;
            currentToken=tokens.get(counter);
        }else{
            addError(currentToken.getLine(),currentToken.getCulomn(),"缺少(");
        }
        TokenTree forInit=new TokenTree("ForInit","赋值语句");
        forInit.children.add(assign_stm(false));
        forTemp.children.add(forInit);
        TokenTree forComp=new TokenTree("ForComp","比较语句");
        forComp.children.add(comp_stm());
        forTemp.children.add(forComp);
        TokenTree forCheck=new TokenTree("ForCheck","赋值语句");
        forCheck.children.add(assign_stm(true));
        forTemp.children.add(forCheck);
        if(currentToken.getKind().equals("RLB")){
            counter++;
            currentToken=tokens.get(counter);
        }else{
            addError(currentToken.getLine(),currentToken.getCulomn(),"缺少）");
        }
        if(currentToken.getKind().equals("LBB")){
            hasBB=true;
            counter++;
            currentToken=tokens.get(counter);
        }

    }

    private TokenTree declare_stm(){
        TokenTree dec=new TokenTree("关键字","声明语句");
        dec.children.add(new TokenTree("关键字",currentToken.getKind()));
        counter++;
        currentToken=tokens.get(counter);
        while(true){
            if(currentToken.getKind().equals("标识符")){
                TokenTree temp =new TokenTree("标识符",currentToken.getContent());
                //先记下标识符的名字
                counter++;
                currentToken=tokens.get(counter);//此时如果是【，那就是数组，如果是=那就是赋值，如果是，那就是连续赋值，如果是；那就是结束了
                if(currentToken.getKind().equals("LMB")){
                    temp.children.add(new TokenTree("LMB","["));
                    counter++;
                    currentToken=tokens.get(counter);
                    temp.children.add(express_stm());
                    if(currentToken.getKind().equals("RMB")){
                        temp.children.add(new TokenTree("RMB","]"));
                        counter++;
                        currentToken=tokens.get(counter);
                    }else{
                        addError(currentToken.getLine(),currentToken.getCulomn(),"缺少】");
                    }
                    if(currentToken.getContent().equals("=")) {
                        TokenTree ass = new TokenTree("运算符", "=");
                        ass.children.add(temp);
                        counter++;
                        currentToken = tokens.get(counter);
                        ass.children.add(express_stm());
                        dec.children.add(ass);
                        counter++;
                        currentToken = tokens.get(counter);
                        if (currentToken.getContent().equals(";")) {
                            counter++;
                            currentToken = tokens.get(counter);
                            break;
                        } else if (currentToken.getContent().equals(",")) {
                            counter++;
                            currentToken = tokens.get(counter);
                            continue;
                        } else {
                            addError(currentToken.getLine(), currentToken.getCulomn(), "缺少；");
                            break;
                        }
                    }
                    else if (currentToken.getContent().equals(";")) {
                        dec.children.add(temp);
                        counter++;
                        currentToken = tokens.get(counter);
                        break;
                    } else if (currentToken.getContent().equals(",")) {
                        dec.children.add(temp);
                        counter++;
                        currentToken = tokens.get(counter);
                        continue;
                    } else {
                        addError(currentToken.getLine(), currentToken.getCulomn(), "缺少；");
                        break;
                    }

                }
                else if(currentToken.getContent().equals("=")){
                    TokenTree ass=new TokenTree("运算符","=");
                    ass.children.add(temp);
                    counter++;
                    currentToken=tokens.get(counter);
                    ass.children.add(express_stm());
                    dec.children.add(ass);
                    counter++;
                    currentToken=tokens.get(counter);
                    if(currentToken.getContent().equals(";")){
                        counter++;
                        currentToken=tokens.get(counter);
                        break;
                    }else if(currentToken.getContent().equals(",")){
                        counter++;
                        currentToken=tokens.get(counter);
                        continue;
                    }
                    else{
                        addError(currentToken.getLine(),currentToken.getCulomn(),"缺少；");
                        break;
                    }
                }
                else if (currentToken.getContent().equals(";")) {
                    dec.children.add(temp);
                    counter++;
                    currentToken = tokens.get(counter);
                    break;
                }
                else if (currentToken.getContent().equals(",")) {
                    dec.children.add(temp);
                    counter++;
                    currentToken = tokens.get(counter);
                    continue;
                }
                else {
                    addError(currentToken.getLine(), currentToken.getCulomn(), "缺少；");
                    break;
                }
            }
            else{
                addError(currentToken.getLine(),currentToken.getCulomn(),"缺少标识符");
                break;
            }
        }
        return dec;
    }

    /**
     * isFor变量是用来标识是否是For循环进来的赋值语句的后半段的，也就是不不要验证分号那个
     * */
    private TokenTree assign_stm(boolean IsFor) {
        TokenTree ass_stm=new TokenTree("运算符",ASSIGN);
        TokenTree tag=new TokenTree("标识符",tokens.get(counter).getContent());
        ass_stm.children.add(tag);
        counter++;//此时应该指向=号了,除非是数组赋值
        currentToken=tokens.get(counter);
        if(currentToken.getContent().equals("=")){
            counter++;
            currentToken=tokens.get(counter);
        }
        else if(currentToken.getKind().equals("LMB")) {
            //此时应该指向的是[
            counter++;
            currentToken=tokens.get(counter);
            tag.children.add(new TokenTree("LMB","["));
            counter++;
            currentToken=tokens.get(counter);
            tag.children.add(express_stm());//理应每个函数结束的时候会读取下一个token
            //TODO 这个不读很可能是个巨大的BUG
//            counter++; //【 】中间的完成了，这个应该是】了
//            currentToken=tokens.get(counter);
            if(currentToken.getKind().equals("RMB")){
                tag.children.add(new TokenTree("RMB","]"));
                counter++;
                currentToken=tokens.get(counter);
            }
            else{
                addError(currentToken.getLine(),currentToken.getCulomn(),"缺少】");
            }
            counter++;
            currentToken=tokens.get(counter);
            if(currentToken.getContent().equals("=")){
                counter++;
                currentToken=tokens.get(counter);
            }
            else{
                addError(currentToken.getLine(),currentToken.getCulomn(),"缺少=");
            }
        }
        else{
            addError(currentToken.getLine(),currentToken.getCulomn(),"缺少=");
        }
        ass_stm.children.add(express_stm());
        if(IsFor){
            return ass_stm;
        }
        if(currentToken.getContent().equals(";")){
            //分割符就不加入
            counter++;
            currentToken=tokens.get(counter);
        }else{
            addError(currentToken.getLine(),currentToken.getCulomn(),"缺少；");
        }
        return ass_stm;
    }
    private TokenTree express_stm(){
        TokenTree express=term();
        while (currentToken!=null&&(currentToken.getContent().equals("+")||currentToken.getContent().equals("-"))){
            TokenTree addTree=add_op();
            addTree.children.add(express);
            express=addTree;
            express.children.add(term());
        }
        return express;
    }

    private TokenTree term(){
        TokenTree temp=factor();
        while (currentToken != null
                && (currentToken.getContent().equals("*") || currentToken
                .getContent().equals("/"))) {
            // mul_op
            TokenTree mulNode = mul_op();
            mulNode.children.add(temp);
            temp = mulNode;
            temp.children.add(factor());
        }
        return temp;
    }
    private TokenTree factor(){
        // 保存要返回的结点
        TokenTree tempNode = null;
        if (currentToken != null && currentToken.getKind().equals("整数")) {
            tempNode = new TokenTree("整数", currentToken.getContent());
            counter++;
            currentToken=tokens.get(counter);
        } else if (currentToken != null && currentToken.getKind().equals("实数")) {
            tempNode = new TokenTree("实数", currentToken.getContent());
            counter++;
            currentToken=tokens.get(counter);
        } else if (currentToken != null
                && currentToken.getContent().equals("true")) {
            tempNode = new TokenTree("布尔值", currentToken.getContent());
            counter++;
            currentToken=tokens.get(counter);
        } else if (currentToken != null
                && currentToken.getContent().equals("false")) {
            tempNode = new TokenTree("布尔值", currentToken.getContent());
            counter++;
            currentToken=tokens.get(counter);
        } else if (currentToken != null && currentToken.getKind().equals("标识符")) {
            tempNode = new TokenTree("标识符", currentToken.getContent());
            counter++;
            currentToken=tokens.get(counter);
            // array
            //region标识符可以是单独的标识符，也可以是代表数组的
            if (currentToken != null
                    && currentToken.getContent().equals("[")) {
                //tempNode.add(array());
            }
            //endregion
        } else if (currentToken != null
                && currentToken.getContent().equals("(")) { // 匹配左括号(
            counter++;
            currentToken=tokens.get(counter);
            //算数因子以左括号开始话，就是还是一个表达式
            tempNode = express_stm();
            // 匹配右括号)
            if (currentToken != null
                    && currentToken.getContent().equals(")")) {
                counter++;
                currentToken=tokens.get(counter);
            } else { // 报错
//                String error = " 算式因子缺少右括号\")\"" + "\n";
//                error(error);
//                return new TreeNode(ConstVar.ERROR + "算式因子缺少右括号\")\"");
            }
        } else if (currentToken != null
                && currentToken.getContent().equals("\"")) { // 匹配双引号
            counter++;
            currentToken=tokens.get(counter);
            tempNode = new TokenTree("字符串", currentToken.getContent());
            counter++;
            currentToken=tokens.get(counter);
            //如果是双引号开始话，下面开始就是一个字符串，可以直接略过
            // 匹配另外一个双引号
            counter++;
            currentToken=tokens.get(counter);
        } else { // 报错
//            String error = " 算式因子存在错误" + "\n";
//            error(error);
            if (currentToken != null
                    && !currentToken.getContent().equals(ConstVar.SEMICOLON)) {
                nextToken();
            }
            return new TreeNode(ConstVar.ERROR + "算式因子存在错误");
        }
        return tempNode;
    }
    private TokenTree add_op(){
        // 保存要返回的结点
        TokenTree tempNode = null;
        if (currentToken != null
                && currentToken.getContent().equals("+")) {
            tempNode = new TokenTree("运算符", "+");
            counter++;
            currentToken=tokens.get(counter);
        } else if (currentToken != null
                && currentToken.getContent().equals("-")) {
            tempNode = new TokenTree("运算符", "-");
            counter++;
            currentToken=tokens.get(counter);
        } else { // 报错
            addError(currentToken.getLine(),currentToken.getCulomn(),"加减符号错处");
        }
        return tempNode;
    }

    private TokenTree mul_op(){
        // 保存要返回的结点
        TokenTree tempNode = null;
        if (currentToken != null
                && currentToken.getContent().equals("*")) {
            tempNode = new TokenTree("运算符", "*");
            counter++;
            currentToken=tokens.get(counter);
        } else if (currentToken != null
                && currentToken.getContent().equals("/")) {
            tempNode = new TokenTree("运算符", "/");
            counter++;
            currentToken=tokens.get(counter);
        } else { // 报错
            addError(currentToken.getLine(),currentToken.getCulomn(),"乘除符号错处");
        }
        return tempNode;
    }
    private void addError(int line,int culomn,String oldText,String newText,String kind){
        errorNum++;
        if(kind==null){
            errorInfo=errorInfo+"第"+line+"行第"+culomn+"列出现不合语法Token"+oldText+",这里缺少字符"+newText+"\n";
        }
        else{
            errorInfo=errorInfo+"第"+line+"行第"+culomn+"列出现不合语法Token"+oldText+kind+"\n";
        }
    }
    private void addError(int line,int culomn,String narrate){
        errorNum++;
        errorInfo=errorInfo+"第"+line+"行第"+culomn+"列"+narrate+"\n";
    }
    //region setget函数
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

    public void setTokens(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }
    //endregion
}
