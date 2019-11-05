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
    public TokenTree Program=new TokenTree("关键字","root");


    //用来遍历词法分析得到的Token。
    private int counter=0;
    private Token currentToken;

    /**
     * 构造函数，参数是词法分析的token集合
     * */

    public GraAnalysis(ArrayList<Token> lexToken){
        this.tokens=lexToken;
    }

    public void gra(){
        counter=0;
        currentToken=tokens.get(counter);
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
        //currentToken=tokens.get(counter);
        if(currentToken.getKind()=="标识符"){
            state=assign_stm(false);
        }
        else if(currentToken!=null&&(currentToken.getContent().equals("int")||currentToken.getContent().equals("string")||currentToken.getContent().equals("read")||currentToken.getContent().equals("bool"))){
            state=declare_stm();
        }
        else if(currentToken!=null&&currentToken.getContent().equals("for")){
            state=for_stm();
        }
        else if(currentToken!=null&&currentToken.getContent().equals("while")){
            state=while_stm();
        }
        else if(currentToken!=null&&currentToken.getContent().equals("if")){
            state=if_stm();
        }
        else if(currentToken!=null&&currentToken.getContent().equals("write")){
            state=write_stm();
        }
        else if(currentToken!=null&&currentToken.getContent().equals("read")){
            state=read_stm();
        }
        else{
            addError(currentToken.getLine(),currentToken.getCulomn(),"以错误的Token开始");
            counter++;
            currentToken=tokens.get(counter);
            state=new TokenTree("错误","以错误的Token开始");
        }
        return state;
    }

    private TokenTree read_stm(){
        TokenTree read_tokem=new TokenTree("关键字","read");
        counter++;
        currentToken=tokens.get(counter);
        if(currentToken.getKind().equals("LLB")){
            counter++;
            currentToken=tokens.get(counter);
        }
        else{
            addError(currentToken.getLine(),currentToken.getCulomn(),"缺少左小括号（");
        }
        read_tokem.children.add(express_stm());
        if(currentToken.getKind().equals("RLB")){
            counter++;
            currentToken=tokens.get(counter);
        }
        else{
            addError(currentToken.getLine(),currentToken.getCulomn(),"缺少右小括号（");
        }
        if(currentToken.getContent().equals(";")){
            counter++;
            currentToken=tokens.get(counter);
        }
        else{
            addError(currentToken.getLine(),currentToken.getCulomn(),"分号");
        }
        return read_tokem;
    }

    private TokenTree write_stm(){
        TokenTree write_token=new TokenTree("关键字","write");
        counter++;
        currentToken=tokens.get(counter);
        if(currentToken.getKind().equals("LLB")){
            counter++;
            currentToken=tokens.get(counter);
        }
        else{
            addError(currentToken.getLine(),currentToken.getCulomn(),"缺少左小括号（");
        }
        write_token.children.add(express_stm());
        if(currentToken.getKind().equals("RLB")){
            counter++;
            currentToken=tokens.get(counter);
        }
        else{
            addError(currentToken.getLine(),currentToken.getCulomn(),"缺少右小括号（");
        }
        if(currentToken.getContent().equals(";")){
            counter++;
            currentToken=tokens.get(counter);
        }
        else{
            addError(currentToken.getLine(),currentToken.getCulomn(),"分号");
        }
        return write_token;
    }

    private TokenTree if_stm(){
        TokenTree if_token=new TokenTree("关键字","if");
        counter++;
        currentToken=tokens.get(counter);
        boolean hasBB=false;
        boolean hasElse=false;
        boolean hasElseBB=false;
        if(currentToken.getKind().equals("LLB")){
            counter++;
            currentToken=tokens.get(counter);
        }else{
            addError(currentToken.getLine(),currentToken.getCulomn(),"缺少左小括号（");
        }
        TokenTree if_check=new TokenTree("关键字","check");
        if_check.children.add(comp_stm());
        if_token.children.add(if_check);
        if(currentToken.getKind().equals("RLB")){
            counter++;
            currentToken=tokens.get(counter);
        }else{
            addError(currentToken.getLine(),currentToken.getCulomn(),"缺少右小括号）");
        }
        if(currentToken.getKind().equals("LBB")){
            hasBB=true;
            counter++;
            currentToken=tokens.get(counter);
        }
        if(hasBB){
            while(!currentToken.getKind().equals("RBB")){
                if_token.children.add(statement());
            }
            counter++;
            currentToken=tokens.get(counter);
        }
        else{
            if_token.children.add(statement());
        }
        if(currentToken.getContent().equals("else")){
            hasElse=true;
            counter++;
            currentToken=tokens.get(counter);
        }
        if(hasElse){
            TokenTree else_token=new TokenTree("关键字","else");
            if(currentToken.getKind().equals("LBB")){
                hasElseBB=true;
                counter++;
                currentToken=tokens.get(counter);
            }
            if(hasElseBB){
                while(!currentToken.getKind().equals("RBB")){
                    else_token.children.add(statement());
                }
                counter++;
                currentToken=tokens.get(counter);
                if_token.children.add(else_token);
            }
            else{
                else_token.children.add(statement());
                counter++;
                currentToken=tokens.get(counter);
                if_token.children.add(else_token);
            }
            return if_token;
        }else{
            return if_token;
        }
    }

    private TokenTree while_stm(){
        TokenTree while_token=new TokenTree("关键字","while");
        boolean haaBB=false;
        counter++;
        currentToken=tokens.get(counter);
        if(currentToken.getKind().equals("LLB")){
            counter++;
            currentToken=tokens.get(counter);
        }else{
            addError(currentToken.getLine(),currentToken.getCulomn(),"缺少左小括号（");
        }
        TokenTree while_check=new TokenTree("关键字","check");
        while_check.children.add(comp_stm());
        while_token.children.add(while_check);
        if(currentToken.getKind().equals("RLB")){
            counter++;
            currentToken=tokens.get(counter);
        }else{
            addError(currentToken.getLine(),currentToken.getCulomn(),"缺少右小括号）");
        }
        if(currentToken.getKind().equals("lBB")){
            haaBB=true;
            counter++;
            currentToken=tokens.get(counter);
        }
        if(haaBB){
            //这种写法是不健康的，要检测一下万一}整个未结束的情况
            while(!currentToken.getKind().equals("RBB")){
                while_token.children.add(statement());
            }
            counter++;
            currentToken=tokens.get(counter);
        }
        else{
            while_token.children.add(statement());
            counter++;
            currentToken=tokens.get(counter);
        }
        return while_token;
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
        TokenTree forInit=new TokenTree("关键字","init");
        forInit.children.add(assign_stm(false));
        forTemp.children.add(forInit);
        TokenTree forComp=new TokenTree("关键字","check");
        forComp.children.add(comp_stm());
        forTemp.children.add(forComp);
        TokenTree forCheck=new TokenTree("关键字","init");
        forCheck.children.add(assign_stm(true));
        forTemp.children.add(forCheck);
        if(currentToken.getKind().equals("RLB")){
            counter++;
            currentToken=tokens.get(counter);
        }else{
            addError(currentToken.getLine(),currentToken.getCulomn(),"缺少右括号）");
        }
        if(currentToken.getKind().equals("LBB")){
            hasBB=true;
            counter++;
            currentToken=tokens.get(counter);
        }
        //如果检测到大括号
        if(hasBB){
            while(!currentToken.getKind().equals("RBB")){
                forTemp.children.add(statement());
            }
            counter++;
            currentToken=tokens.get(counter);
            //if(currentToken.getKind().equals(""))
        }//如果没有大括号
        else{
            forTemp.children.add(statement());
            counter++;
            currentToken=tokens.get(counter);
        }
        return forTemp;
    }

    /**
     * 这个地方也得解释一下，免得自己以后也忘了，
     * 对于生命语句，只能声明同一种数据类型的，可以声明单独的变量，也可以声明数组。声明时候可以赋初值，也可以不符初值，
     * 对于数组的整体赋值可以这样做，int a[5]={1，2，3，4，5}，可以在声明之后a={1,2,3},声明之前不能这样赋值
     * 声明数组是这样 int a[5],方括号中不能啥都没有
     * 返回的节点的第一个子节点是数据类型，接下来声明一个变量就一个子节点
     * 对于只声明变量，直接将其存入根节点中，
     * 对于声明并赋值的，将=存入根节点中，=的子节点第一个是标识符，第二个是表达式的根节点、
     * 对于数组声明的，其标识符会直接存入根节点，标识符的子节点是一个表达式，其实更多的情况应该只是一个数
     * 对于数组声明并初始化的，=会存入根节点，标识符会存入=的第一个子节点，=的第二个子节点应该是一个kind为关键字，content是整体数组的一个声明，这个节点下面是数组每一项的值，这里并未检查其数量和数据类型是否与前面声明的情况相匹配，放到语义分析中去把
     * */
    private TokenTree declare_stm(){
        TokenTree dec=new TokenTree("关键字","declare");
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
                    //temp.children.add(new TokenTree("LMB","["));
                    counter++;
                    currentToken=tokens.get(counter);
                    temp.children.add(express_stm());
                    if(currentToken.getKind().equals("RMB")){
                        //temp.children.add(new TokenTree("RMB","]"));
                        counter++;
                        currentToken=tokens.get(counter);
                    }else{
                        addError(currentToken.getLine(),currentToken.getCulomn(),"缺少】");
                    }
                    //数组声明完成
                    if(currentToken.getContent().equals("=")) {
                        TokenTree ass = new TokenTree("运算符", "=");
                        ass.children.add(temp);
                        counter++;
                        currentToken = tokens.get(counter);
                        if(currentToken.getKind().equals("LBB")){
                            counter++;
                            currentToken=tokens.get(counter);
                        }
                        else{
                            addError(currentToken.getLine(),currentToken.getCulomn(),"数组赋值缺少{");
                        }
                        TokenTree array_token=new TokenTree("关键字","AllArrayInit");
                        counter++;
                        currentToken=tokens.get(counter);
                        while(!currentToken.getKind().equals("RBB")){
                            array_token.children.add(array_token);
                            counter++;
                            currentToken=tokens.get(counter);
                            if(currentToken.getContent().equals(",")){
                                counter++;
                                currentToken=tokens.get(counter);
                                continue;
                            }
                            else if(currentToken.getKind().equals("RBB")){
                                counter++;
                                currentToken=tokens.get(counter);
                                break;
                            }
                            else{
                                addError(currentToken.getLine(),currentToken.getCulomn(),"错误的token"+currentToken.getContent());
                            }
                        }
                        ass.children.add(array_token);
                        //ass.children.add(express_stm());
                        dec.children.add(ass);
//                        counter++;
//                        currentToken = tokens.get(counter);
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
                        dec.children.add(temp);
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
                    addError(currentToken.getLine(), currentToken.getCulomn(), "错误的token，缺少；");
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
        TokenTree ass_token=new TokenTree("关键字","assign");
        TokenTree ass_stm=new TokenTree("运算符","=");
        ass_token.children.add(ass_stm);/**每一个语句都有其解释，再有其子节点*/
        TokenTree tag=new TokenTree("标识符",currentToken.getContent());

        ass_stm.children.add(tag);//将标识符作为等号的第一个子节点
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
            /**这里做过修改，将【】不再作为标识符的子节点，而是将仅仅将数组的下标加入标识符的子节点了
             * 也就是说，如果标识符没有子节点的话，也就意味着其只是一个标识符。
             * 如果有子节点的话，也就是数组的标识符了
             * 关于数组的赋初值问题，还没有很好的想法
             * //TODO 待解决数组赋初值的问题
             * 同时，由于修改后将bool类型的单独放出了，所以数组中理论上是不应该存在bool类型的变量的，如果有，在语法分析就识别出来了
             * 但是像字符串这样的等等问题，放在语义分析中去解决
             * **/
            //tag.children.add(new TokenTree("LMB","["));
            counter++;
            currentToken=tokens.get(counter);
            tag.children.add(express_stm());//理应每个函数结束的时候会读取下一个token
            //TODO 这个不读很可能是个巨大的BUG
//            counter++; //【 】中间的完成了，这个应该是】了
//            currentToken=tokens.get(counter);
            if(currentToken.getKind().equals("RMB")){
                //tag.children.add(new TokenTree("RMB","]"));
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

        //这里来写一下数组赋值的问题
        if(currentToken.getKind().equals("LBB")){
            TokenTree array_token=new TokenTree("关键字","AllArrayInit");
            counter++;
            currentToken=tokens.get(counter);
            while(!currentToken.getKind().equals("RBB")){
                array_token.children.add(array_token);
                counter++;
                currentToken=tokens.get(counter);
                if(currentToken.getContent().equals(",")){
                    counter++;
                    currentToken=tokens.get(counter);
                    continue;
                }
                else if(currentToken.getKind().equals("RBB")){
                    counter++;
                    currentToken=tokens.get(counter);
                    break;
                }
                else{
                    addError(currentToken.getLine(),currentToken.getCulomn(),"错误的token"+currentToken.getContent());
                }
            }
            ass_stm.children.add(array_token);
        }
        else{
            ass_stm.children.add(express_stm());
        }

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
        return ass_token;
    }

    /**
     * 这里面表达式一个很大的不同支出在于表达式最后的根节点是一个运算符号，而像其他的语句的根节点都是能标识这句话的种类的，一般是以关键字表示其种类的
     *
     * **/
    private TokenTree express_stm(){
        TokenTree express=term();
        while (currentToken!=null&&(currentToken.getContent().equals("+")||currentToken.getContent().equals("-"))){
            TokenTree addTree=add_op();
            //下面这行代码相当于在这个运算符后面加了一个括号
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
        }
        else if (currentToken != null && currentToken.getKind().equals("实数")) {
            tempNode = new TokenTree("实数", currentToken.getContent());
            counter++;
            currentToken=tokens.get(counter);
        }
        //TODO 关于下面的两个布尔的判断，暂时没有很好的处理方式
        else if (currentToken != null
                && currentToken.getContent().equals("true")) {
            tempNode = new TokenTree("布尔值", currentToken.getContent());
            counter++;
            currentToken=tokens.get(counter);
        }
        else if (currentToken != null
                && currentToken.getContent().equals("false")) {
            tempNode = new TokenTree("布尔值", currentToken.getContent());
            counter++;
            currentToken=tokens.get(counter);
        }
        else if (currentToken != null && currentToken.getKind().equals("标识符")) {
            tempNode = new TokenTree("标识符", currentToken.getContent());
            counter++;
            currentToken=tokens.get(counter);
            if (currentToken != null
                    && currentToken.getContent().equals("[")) {
                //tempNode.add(array());
            }
            //endregion
        }
        /**在这里也能一并表示出括号的优先级较高*/
        else if (currentToken != null
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
            }
            else {
                addError(currentToken.getLine(),currentToken.getCulomn(),"缺少右小括号");
            }
        }
        else if (currentToken != null
                && currentToken.getContent().equals("\"")) { // 匹配双引号
            counter++;
            currentToken=tokens.get(counter);
            tempNode = new TokenTree("字符串", currentToken.getContent());
            counter++;
            currentToken=tokens.get(counter);
            if(currentToken.getContent().equals("\"")){
                counter++;
                currentToken=tokens.get(counter);
            }
            else{
                addError(currentToken.getLine(),currentToken.getCulomn(),"缺少右引号");
            }

        }
        else {
            if (currentToken != null
                && !currentToken.getContent().equals(";")) {
                counter++;
                currentToken=tokens.get(counter);
            }
            tempNode=new TokenTree("错误","错误的算术因子");
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
            tempNode=new TokenTree("错误","加减运算符错误");
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
            tempNode=new TokenTree("错误","错误的运算符，这里应该是乘除");
            addError(currentToken.getLine(),currentToken.getCulomn(),"乘除符号错处");
        }
        return tempNode;
    }

    private TokenTree comp_stm(){
        //TODO 待做
        TokenTree comp_token=null;
        TokenTree left_token=express_stm();
        if(currentToken.getContent().equals("<")
                ||currentToken.getContent().equals(">")
                ||currentToken.getContent().equals("<>")
                ||currentToken.getContent().equals("==")) {
            comp_token=new TokenTree("比较符",currentToken.getContent());
            counter++;
            currentToken=tokens.get(counter);
        }

        else{
            comp_token=new TokenTree("错误","错误的比较符号");
        }
        comp_token.children.add(left_token);
        comp_token.children.add(express_stm());
        return comp_token;
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
