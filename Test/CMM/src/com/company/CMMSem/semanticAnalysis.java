package com.company.CMMSem;


import com.company.Tools.ID.IDBase;
import com.company.Tools.TokenTree;

import java.util.ArrayList;

/**
 * 关于建立这个类的原因，本来是想在上个类中直接实现检查内容和执行的效果，
 * 后来发现难度过大，所以就在这单独的先进行一个语义分析
 * 其实挺后悔在语法分析的时候没有将TokenTree的位置传过来
 * */
public class semanticAnalysis {
    private TokenTree root;/**用来继承其根节点*/
    private ArrayList<IDBase> ids=new ArrayList<>();/**用来动态的记录标识符的*/
    //private ArrayList<ID> idArrayList=new ArrayList<>();/**可以说这两个变量相辅相成，上面那个是用来监测是否有重复的标识符或者未声明巴拉巴拉，这个是用来记录每一个合法的标识符的*/
    private ArrayList<Integer> counter_id=new ArrayList<>();/**这个变量是记录在每个模块中增加的标识符的的数量的,为了能够实现变量的作用域，自己挖的坑，自己填吧*/
    private int errorNum=0;
    private String errorInfo="";
    public semanticAnalysis(TokenTree root){
        this.root=root;
    }
    public void semantic(TokenTree tempRoot){
        TokenTree temp=null;
        for(int i=0;i<tempRoot.getChildSize();i++){
            //counter_id=0;
            temp=tempRoot.get(i);
            if(temp.getKind().equals("关键字")&&temp.getContent().equals("declare")){
                declare_sem(temp);
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("read")){

            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("write")){

            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("assign")){
                assign_sem(temp);
            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("while")){

            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("for")){

            }
            else if(temp.getKind().equals("关键字")&&temp.getContent().equals("if")){

            }
            else if(temp.getKind().equals("finish")&&temp.getContent().equals("finish")){
                System.out.println("语义分析结束");
                break;
            }
            else{
                System.out.println("出现了不该出现的关键字");
            }
            removeID(counter_id.get(counter_id.size()-1));
        }
    }

    private void assign_sem(TokenTree temp){

    }

    private void declare_sem(TokenTree temp){
        String kind=temp.get(0).getContent();/**获取到声明的数据类型*/
        /**之所以从1开始，是因为声明节点的第一个子节点都是其声明的数据类型*/
        for(int counter=1;counter<temp.getChildSize();counter++){
            TokenTree one_declare=temp.get(counter);/**这个变量代表的是单独的一整个声明，比如int a=1,b=9,它可以代表其中的一个*/
            /**声明加初始化*/
            if(one_declare.getKind().equals("运算符")&&one_declare.getContent().equals("=")){
                TokenTree one_id=one_declare.get(0);/**这个变量代表的是声明的标识符*/
                if(isExist(one_id.getContent())){
                    addError("在声明的标识符"+one_id.getContent()+"已经存在");
                    continue;
                }
                if(one_id.hasChildren()){
                    TokenTree one_id_array_length=one_id.get(0);
                    TokenTree one_id_array_init=one_id.get(1);
                    if(!express_type_check_array_init(one_id_array_init,kind)){
                        addError("关于"+one_id.getContent()+"数组的初始化中数据类型不匹配");
                        continue;
                    }
                    if(one_id_array_length.getKind().equals("int")){
                        ids.add(new IDBase(kind,one_id.getContent(),2,true));
                    }
                    else if(one_id_array_length.getKind().equals("运算符")){
                        if(express_type_check(one_id_array_length,"int")){
                            ids.add(new IDBase(kind,one_id.getContent(),2,true));
                        }
                        else{
                            addError(one_id.getContent()+"的声明中数组长度不合法");
                        }
                    }
                    else{/**不允许使用标识符声明数组长度*/
                        addError(one_id.getContent()+"的声明中数组长度不合法");
                    }
                }
                else{
                    TokenTree one_init=one_declare.get(1);
                    if(one_init.getKind().equals("运算符")){
                        if(express_type_check(one_init,kind)){
                            ids.add(new IDBase(kind,one_id.getContent(),1));
                        }
                        else{
                            addError("关于"+one_id.getContent()+"的初始化类型不兼容");
                        }
                    }
                    else{
                        if(!typeCompatibility(kind,one_init.getKind())){
                            addError("关于"+one_id.getContent()+"的初始化类型不兼容");
                        }
                        else{
                            ids.add(new IDBase(kind,one_id.getContent(),1));
                        }
                    }
                }
            }/**只声明了*/
            else if(one_declare.getKind().equals("标识符")){
                if(isExist(one_declare.getContent())){
                    addError("要声明的标识符"+one_declare.getContent()+"已经存在");
                    continue;
                }
                if(one_declare.hasChildren()){
                    TokenTree one_id_array_length=one_declare.get(0);
                    if(one_id_array_length.getKind().equals("int")){
                        ids.add(new IDBase(kind,one_declare.getContent(),2));
                    }
                    else if(one_id_array_length.getKind().equals("运算符")){
                        if(express_type_check(one_id_array_length,"int")){
                            ids.add(new IDBase(kind,one_declare.getContent(),2));
                        }
                        else{
                            addError(one_declare.getContent()+"的声明中数组长度不合法");
                        }
                    }
                    else{/**不允许使用标识符声明数组长度*/
                        addError(one_declare.getContent()+"的声明中数组长度不合法");
                    }
                }
                else{
                    ids.add(new IDBase(kind,one_declare.getContent(),1));
                }

            }
            else{
                addError("语法分析有待完善");
            }
        }
    }

    private boolean express_type_check_array_init(TokenTree tempToot,String king){
        //初始化这也要考虑类型兼容了
        return true;//TODO 待做
    }
    private boolean express_type_check(TokenTree express,String kind){
        switch (kind){
            case "int":
                return type_int_check(express);
            default:return false;
        }
    }
    private boolean type_int_check(TokenTree express_int){
        return true;
    }
    private void removeID(int counter){
        //TODO 待做
    }

    /**下面这个函数是用来判断类型兼容的，第一个是声明的类型，第二个是赋值的类型**/
    private boolean typeCompatibility(String kindStatement,String kindAssignment){
        //TODO 在这做类型兼容的问题,暂时先实现这么多
        if(kindStatement.equals("real")){
            if(kindAssignment.equals("real")){
                return true;
            }else if(kindAssignment.equals("int")){
                return true;
            }else{
                return false;
            }
        }else if(kindStatement.equals("int")){
            if(kindAssignment.equals("int")){
                return true;
            }else{
                return false;
            }
        }else if(kindStatement.equals("string")){
            if(kindAssignment.equals("string")){
                return true;
            }
            else {
                return false;
            }
        }else if(kindStatement.equals("bool")){
            if(kindAssignment.equals("bool")){
                return true;
            }//TODO 这里想让bool兼容除了String之外的全部基本数据类型
            else{
                return false;
            }
        }else{
            return false;
        }
    }

    /**那个int变量是为了区别哪个是标识符，其中1代表声明的那个是标识符，2代表等号后面是标识符，3代表全是标识符
     * kindAssignmentID是标识符的名字，这里的含义是指当赋值的时候用的是标识符的话，就将其名字传进来
     * */
    private boolean typeCompatibility(String kindStatementIDName,String kindAssignmentIDName,int IsID){
        switch (IsID){
            case 1:
                String kindStatement=findIDKindByName(kindStatementIDName);
                if(kindStatement==null){
                    return false;
                }else{
                    return typeCompatibility(kindStatement,kindAssignmentIDName);
                }
            case 2:
                String kindAssignment=findIDKindByName(kindAssignmentIDName);
                if(kindAssignment==null){
                    return false;
                }else{
                    return typeCompatibility(kindStatementIDName,kindAssignment);
                }
            case 3:
                String kindStatement1=findIDKindByName(kindStatementIDName);
                String kindAssignment1=findIDKindByName(kindAssignmentIDName);
                if(kindStatement1==null||kindAssignment1==null){
                    return false;
                }else{
                    return typeCompatibility(kindStatement1,kindAssignment1);
                }
            default:
                return false;
        }

    }
    /**下面这个是用来根据变量的名字，也就是标识符的ID，来返回其数据类型*/
    private String findIDKindByName(String name){
        for(int counter=0;counter<ids.size();counter++){
            if(ids.get(counter).getName().equals(name)){
                return ids.get(counter).getKind();
            }
        }
        return null;
    }

    private boolean isExist(String id_name){
        for(int counter=0;counter<ids.size();counter++){
            if(ids.get(counter).getName().equals(id_name)){
                return true;
            }
        }
        return false;
    }

    private void addError(String info){
        errorInfo=errorInfo+info+"\n";
        errorNum++;
    }
}
