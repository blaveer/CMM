package com.company.CMMSem;

import com.company.Tools.ID.ID;
import com.company.Tools.RunError.BaseError;
import com.company.Tools.Token;
import com.company.Tools.TokenTree;

import java.util.ArrayList;


public class semAnalysis {
    /**下面这个变量是用来记录存储声明过的标识符的，
     * 这里谈一点个人关于标识符的理解，标识符是贯穿于整个程序的，是真正程序执行的意义所在
     * 标识符中的变量承载着程序的执行结果，就算是实现了类的概念，标识符的意义也不过如此
     * 在这里，这个id的目的是为了存储程序中的标识符的，并且通过动态增加和减少来实现作用域的问题*/
    private ArrayList<ID> id = new ArrayList<ID>();

    /**下面这个是根节点，在语法分析中生成的*/
    private TokenTree root=null;

    /**下面这一组数是用来作为全局变量来记录express那个函数的返回值结果的*/
    private int express_int=0;
    private double express_real=0;

    public semAnalysis(TokenTree root){
        this.root=root;
    }
    /**鉴于每个if等语句里面都是单独的程序结构，这里程序改动过*/
    public void sem(TokenTree rootTemp){
        TokenTree temp=null;
        for(int i=0;i<root.getChildSize();i++){
            temp=rootTemp.children.get(i);
            if(temp.getKind().equals("关键字")&&temp.getContent().equals("declare")){
                run_declare(temp);
            }
        }
    }
    private BaseError run_declare(TokenTree declare){
        //获取到目前声明的数据类型
        String kind=declare.get(0).getContent();
        TokenTree temp=null;
        for(int i=1;i<declare.getChildSize();i++){
            temp=declare.get(i);
            //这个是声明并初始化
            if(temp.getContent().equals("=")){
                //数组的声明  temp的第一个子节点就是要声明的标识符，如果其有子节点，那就是数组的长度，也就是下标
                if(isStatement(temp.get(0).getContent())){
                    return new BaseError("标识符"+temp.get(0).getContent()+"已经存在");
                }
                if(temp.get(0).hasChildren()){
                    int length=evaluationExpression(temp.get(0).get(0));//获取到
                    ArrayList<String> init=new ArrayList<>();
                    TokenTree tempInit=temp.get(1);//这个就是数组初始化，赋初值的时候的代码，暂时要求赋值全赋值
                    for(int counter=0;counter<tempInit.getChildSize();counter++){
                        /***这里简单处理一下类型兼容兼容的问题，在下面有一个函数单独处理，这里只调用*/
                        TokenTree tempOneInit=tempInit.get(counter);
                        if(tempOneInit.getKind().equals("标识符")){
                            if(!isStatement(tempOneInit.getContent())){
                                return new BaseError("使用未声明的标识符");
                            }
                            if(typeCompatibility(kind,tempOneInit.getContent(),2)){
                                init.add(tempOneInit.getContent());//TODO 这里先将其标识符加入，
                            }else{
                                return new BaseError("数组初始化时第"+(counter+1)+"个数据类型与声明的数据类型不符");
                            }
                        }
                        else if(tempOneInit.getKind().equals("运算符")){
                            init.add(String.valueOf(evaluationOneNode(tempInit)));
                        }else{
                            if(typeCompatibility(tempOneInit.getKind(),kind)){
                                init.add(tempOneInit.getContent());
                            }
                            else{
                                return new BaseError("数组初始化时第"+(counter+1)+"个数据类型与声明的数据类型不符");
                            }
                        }
                    }
                    if(length!=init.size()){
                        return new BaseError("数组实例化的长度与声明的长度不符");
                    }
                    ID tempID=new ID(kind,temp.get(0).getContent(),true,length,init);
                    id.add(tempID);
                }
                //普通的声明初始化
                else{
                    TokenTree tempInit=temp.get(1);
                    if(tempInit.getKind().equals("标识符")){
                        if(!typeCompatibility(kind,findIDKindByName(tempInit.getContent()),2)){
                            return new BaseError("类型不兼容");
                        }
                        //id.add(new ID(kind,tempInit.getContent(),))
                        //TODO 这里等着写一个能找到标识符对应的数据再说把
                    }
//                    if(isStatement(temp.get(0).getContent())){
//                        return new BaseError("标识符"+temp.get(0).getContent()+"已经存在");
//                    }

                    ID tempID=new ID(kind,temp.get(0).getContent(),temp.get(1).getContent());
                    id.add(tempID);
                }
            }
            //这个就只是声明
            else{
                if(temp.hasChildren()){

                }
                else{

                }
            }
        }
        return null;
    }

    /**
     * 这个也算是自己埋下的祸根，由于生成的是类似于二叉树的东西，所以在这里将全部表达式运算出一个结果的地方
     * 其中返回值是用来标识运算结果产生的异常的
     * 暂时：0是无异常，正常执行，将运算结果存储在一个全局变量中
     * 关于异常的类型以及返回的对应的值，待做*/
    private int evaluationExpression(TokenTree express){
        //TODO 待做  在上面判断数组的长度的时候，调用的是这个函数，就如何解决只能运算正数的问题，我觉着可以再写一个函数，待做
        evaluationOneNode(express);
        //再上面的函数调用中，该函数的返回值就是数组的长度
        return 0;
    }

    /**下面这个函数完成了正确情况下的加减乘除，但是关于
     * 错误处理如何加进去的问题，待做*/  //TODO 错误如何处理
    private double evaluationOneNode(TokenTree temp){
        if(temp.getKind().equals("运算符")){
            if(temp.get(0).getKind().equals("运算符")){
                if(temp.get(1).getKind().equals("运算符")){
                    return evaluation(String.valueOf(evaluationOneNode(temp.get(0))),String.valueOf(evaluationOneNode(temp.get(1))),temp.getContent());
                }else{
                    return evaluation(String.valueOf(evaluationOneNode(temp.get(0))),temp.get(1).getContent(),temp.getContent());
                }
            }
            else{
                if(temp.get(1).getKind().equals("运算符")){
                    return evaluation(temp.get(0).getContent(),String.valueOf(evaluationOneNode(temp.get(1))),temp.getContent());
                }else{
                    return evaluation(temp.get(0).getContent(),temp.get(1).getContent(),temp.getContent());
                }
            }
        }else{
            return Double.valueOf(temp.getContent());
        }
    }

    /**这个函数用来判断一个double是否是浮点数，如果不是的话，返回false*/
    private boolean isDouble(double temp){
        String SFroDouble=String.valueOf(temp);
        String []split=SFroDouble.split(".");
        for(int counter=0;counter<split[1].length();counter++){
            char c=split[1].charAt(counter);
            if(c!='0'){
                return true;
            }
        }
        return false;
    }

    /**下面这个函数是单独计算两个操作数及其运算符的*/
    private double evaluation(String opNum1,String opNum2,String op){
        double d1=Double.valueOf(opNum1);
        double d2=Double.valueOf(opNum2);
        switch (op){
            case "+":
                return d1+d2;
            case "-":
                return d1-d2;
            case "*":
                return d1*d2;
            case "/":
                return d1/d2;
            default:
                return 0;
        }
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
        for(int counter=0;counter<id.size();counter++){
            if(id.get(counter).getName().equals(name)){
                return id.get(counter).getKind();
            }
        }
        return null;
    }

    /**
     * 下面这个函数是用来判断是否是已经存在的标识符，如果是的，就返回true
     * 其中tempID 是标识符的ID
     * */
    public boolean isStatement(String tempId){
        for(int i=0;i<id.size();i++){
            if(tempId.equals(id.get(i).getName())){
                return true;
            }
        }
        return false;
    }
}
